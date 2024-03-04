/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets;

import androidx.annotation.CallSuper;

import com.esplibrary.constants.Constants;
import com.esplibrary.constants.DeviceId;

import java.util.Arrays;

import static com.esplibrary.constants.DeviceId.UNKNOWN_DEVICE;

/**
 * Base class for all ESPPackets. Has all the basic functionality for getting source, destination, building from a byte buffer,
 * as well as converting an ESPPacket into a byte buffer to send to the Valentine One.
 * Use the PacketFactory class to generate an ESPPacket of the correct type.
 *
 */
public abstract class ESPPacket {

    byte [] packetData;
    protected DeviceId mV1Type = UNKNOWN_DEVICE;

    public ESPPacket(int packetLength) {
        packetData = new byte[packetLength];
    }

    /**
     * Constructs a new instance with the specified V1 type and payload data
     *
     * @param v1Type V1 type
     * @param payload packet payload
     */
    public ESPPacket(DeviceId v1Type, byte [] payload) {
        resetPacket (v1Type, payload);
    }

    /**
     * Reset this packet back to the state it was in after construction
     *
     * @param v1Type V1 type
     * @param payload packet payload
     */
    protected void resetPacket (DeviceId v1Type, byte [] payload) {
        boolean chksum = (v1Type == DeviceId.VALENTINE_ONE);
        final int framingLength = chksum ? 7 : 6;
        int length = (payload != null ? (payload.length + framingLength) : framingLength);
        packetData = new byte[length];
    }

    /**
     * Convenience field that internally stores when the {@link ESPPacket} was sent or received.
     */
    protected long mTransmissionTime = Long.MAX_VALUE;

    /**
     * Type of the V1 on the ESP bus this packet was transmitted.
     *
     * @return V1 type
     *
     * @see DeviceId#VALENTINE_ONE_LEGACY
     * @see DeviceId#VALENTINE_ONE_NO_CHECKSUM
     * @see DeviceId#VALENTINE_ONE
     */
    public DeviceId getValentineType() {
        return mV1Type;
    }

    /**
     * Update the V1 type of the packet.
     *
     * @param newV1Type V1 type.
     */
    public synchronized void setV1Type(DeviceId newV1Type) {
        // If the V1 type has changed, we need to decided whether we need to create or remove the checksum.
        if(this.mV1Type != newV1Type) {
            // Update the destination if going to the V1.
            if(isForV1()) {
                packetData[1] = (byte) (newV1Type.toByte() | PacketUtils.DEST_INDENTIFIER_BASE_CONST);
            }
            // Handle the checksum byte...
            if(wasChecksum(mV1Type, newV1Type)) {
                // Get the payload length minus the checksum byte.
                int payloadLen = packetData[PacketUtils.PAYLOAD_LEN_IDX];
                byte [] newData = new byte[6 + payloadLen];
                packetData[PacketUtils.PAYLOAD_LEN_IDX] -= 1;
                System.arraycopy(packetData, 0, newData, 0, newData.length);
                this.packetData = newData;
            }
            else if(hasBecomeChecksum(mV1Type, newV1Type)) {
                byte [] newData = new byte[7 + packetData[PacketUtils.PAYLOAD_LEN_IDX]];
                packetData[PacketUtils.PAYLOAD_LEN_IDX] += 1;
                // Copy all of the data from packetData minus the end of frame byte.
                System.arraycopy(packetData, 0, newData, 0, packetData.length - 1);
                // Add in the new checksum and end of frame byte.
                newData[newData.length - 2] = PacketUtils.calculateChecksumFor(newData, newData.length - 2);
                newData[newData.length - 1] = PacketUtils.ESP_PACKET_EOF;
                this.packetData = newData;
            }
            else if(ESPPacket.isChecksum(newV1Type)) {
                packetData[packetData.length - 2] = PacketUtils.calculateChecksumFor(packetData, packetData.length - 2);
            }
            this.mV1Type = newV1Type;
        }
    }

    /**
     * Utility method for determining if a {@link DeviceId v1Type) has transitioned from checksum
     * to 'no checksum'.
     *
     * @param oldV1Type Previous V1 type
     * @param newV1Type New V1 type
     *
     * @return True if transition from checksum to 'no checksum'
     */
    final static boolean wasChecksum(DeviceId oldV1Type, DeviceId newV1Type) {
        return oldV1Type == DeviceId.VALENTINE_ONE && newV1Type != DeviceId.VALENTINE_ONE;
    }

    /**
     * Utility method for determining if a {@link DeviceId v1Type) has transitioned from no checksum
     * to checksum.
     *
     * @param oldV1Type Previous V1 type
     * @param newV1Type New V1 type
     *
     * @return True if transition from no checksum to checksum
     */
    final static boolean hasBecomeChecksum(DeviceId oldV1Type, DeviceId newV1Type) {
        return oldV1Type != DeviceId.VALENTINE_ONE && newV1Type == DeviceId.VALENTINE_ONE;
    }

    /**
     * Sets the time the {@link ESPPacket} was sent out on the ESP bus.
     *
     * @param time The time in milliseconds this {@link ESPPacket} was sent out onto the ESP bus.
     *
     * @see System#currentTimeMillis()
     */
    public void setTransmissionTime(long time) {
        mTransmissionTime = time;
    }

    /**
     * Retrieves the time this {@link ESPPacket} was sent out on the ESP bus.
     *
     * @return  The time in milliseconds this {@link ESPPacket} was sent out onto the ESP bus.
     * If this ESPPacket has not been sent out yet, the value '-1' is returned.
     */
    public long getTransmissionTime() {
        return mTransmissionTime;
    }

    /**
     * Helper method for checking if this ESPPacket has matching destination and originator bytes.
     *
     * @return  Returns true if the ESPPacket has matching destination and originator bytes.
     */
    public boolean isSameDestinationAsOrigin() {
        if(packetData != null) {
            byte dest = (byte) (packetData[PacketUtils.DEST_IDX] & 0x0F);
            byte orig = (byte) (packetData[PacketUtils.ORIG_IDX] & 0x0F);
            return (dest == orig);
        }
        return false;
    }

    /**
     * Indicates if this ESPPacket instance is for the ESPLibrary.
     *
     * @return  Returns true if the ESPPacket is destined for the ESPLibrary, otherwise false is returned.
     */
    public boolean isPacketForMe(){
        if(packetData != null) {
            int dest = (packetData[PacketUtils.DEST_IDX] & 0x0F);
            return ((dest == DeviceId.V1CONNECTION.toByte()) || (dest == DeviceId.GENERAL_BROADCAST.toByte()));
        }
        return false;
    }

    /**
     * Indicates if the packet is from a V1.
     *
     * @return True if from a V1
     */
    public boolean isFromV1() {
        if(packetData != null) {
            return isV1(packetData[PacketUtils.ORIG_IDX]);
        }
        return false;
    }

    /**
     * Indicates if the packet is for a V1.
     *
     * @return True if for a V1
     *
     * @see DeviceId#VALENTINE_ONE
     * @see DeviceId#VALENTINE_ONE_NO_CHECKSUM
     * @see DeviceId#VALENTINE_ONE_LEGACY
     */
    public boolean isForV1() {
        if(packetData != null) {
            return isV1(packetData[PacketUtils.DEST_IDX]);
        }
        return false;
    }

    /**
     * Indicates if the destination is from a V1.
     *
     * @param destination Destination Id byte value
     *
     * @return True if destination is from a V1
     *
     * @see DeviceId#VALENTINE_ONE
     * @see DeviceId#VALENTINE_ONE_NO_CHECKSUM
     * @see DeviceId#VALENTINE_ONE_LEGACY
     */
    public static boolean isV1(byte destination) {
        int maskedByte = (destination & 0x0F);
        return ((maskedByte == Constants.VALENTINE_ONE_BYTE) ||
                (maskedByte == Constants.VALENTINE_ONE_NO_CHECKSUM_BYTE) ||
                (maskedByte == Constants.VALENTINE_ONE_LEGACY_BYTE));
    }

    /**
     * Returns the packet destination ID byte value
     *
     * @return Destination Id
     */
    public DeviceId getDestination() {
        byte destID = (byte) (packetData[PacketUtils.DEST_IDX] & 0x0F);
        return DeviceId.get(destID);
    }

    /**
     * Returns the packet destination ID byte value
     *
     * @return Destination Id
     */
    public byte getDestinationByte() {
        return (byte) (packetData[PacketUtils.DEST_IDX] & 0x0F);
    }

    /**
     * Returns the packet origin {@link DeviceId Id}
     *
     * @return Origin Id
     */
    public DeviceId getOrigin() {
        byte origID = (byte) (packetData[PacketUtils.ORIG_IDX] & 0x0F);
        return DeviceId.get(origID);
    }

    /**
     * Returns the packet origin Id byte value
     *
     * @return Origin Id
     */
    public byte getOriginByte() {
        return (byte) (packetData[PacketUtils.ORIG_IDX] & 0x0F);
    }

    /**
     * Gets the Packet Identifier of this current instance.
     *
     * @return  Returns this ESPPacket's packet identifier. @see PacketIds for possible values.
     */
    public int getPacketID() {
        return (packetData[PacketUtils.PACK_ID_IDX] & 0x000000FF);
    }

    /**
     * Gets the Packet Identifier of this current instance.
     *
     * @return  Returns this ESPPacket's packet identifier. @see PacketIds for possible values.
     */
    public byte getPacketIdByte() {
        return packetData[PacketUtils.PACK_ID_IDX];
    }

    /**
     * Returns an array containing this packets payload data (does not include the checksum byte if present in the data).
     *
     * @return  Returns the ESPPackets payload data if it is not null otherwise, an empty byte array is returned.
     */
    public byte [] getPayloadData() {
        int length = packetData[PacketUtils.PAYLOAD_LEN_IDX];
        // If the V1 type is a Valentine one w/ checksum decrement the length to avoid the
        // checksum byte.
        if(mV1Type == DeviceId.VALENTINE_ONE) {
            length -= 1;
        }
        final byte [] array = new byte[length];
        System.arraycopy(packetData, PacketUtils.PAYLOAD_START_IDX, array, 0, length);
        return array;
    }

    /**
     * Return the backing array for this ESP packet.
     * <p>Note: This is a direct reference to the backing array and should not be modified </p>
     *
     * @return Backing array
     */
    public byte [] getPacketData() {
        return packetData;
    }

    /**
     * Indicates if the device ID is from a V1 with checksum.
     *
     * @param v1Type V1 type
     *
     * @return True if the V1 type has checksum
     */
    public final static boolean isChecksum(DeviceId v1Type) {
        return v1Type == DeviceId.VALENTINE_ONE;
    }

    /**
     * ESP payload data.
     *
     * @return payload
     */
    public Object getResponseData() {
        return null;
    }

    /**
     *  Resets this {@link ESPPacket} state to default values.
     */
    @CallSuper
    public void reset() {
        if(packetData != null) {
            // Default all byte values to zero.
            for (int i = 0, len = packetData.length; i < len; i++)
                packetData[i] = 0x00;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ESPPacket)) return false;
        ESPPacket espPacket = (ESPPacket) o;
        if (!Arrays.equals(packetData, espPacket.packetData)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getPacketData());
    }

    @Override
    public String toString() {
        // The SB's initial capacity is equal to the bytes in packet data multiplied by 3, then
        // subtract one from the result (Two chars for every byte in packetData and one char
        // for spaces in between every byte except for the last).
        final int capacity = (3 *  packetData.length) - 1;
        StringBuilder b = new StringBuilder(capacity);
        if(packetData != null) {
            // We wanna stop one before the last byte so we can add it after the loop and save
            // having a trailing whitespace.
            for(int i = 0, size = packetData.length - 1; i < size; i++) {
                // iterate over the byte values and convert them into hex string.
                b.append(String.format("%02X", packetData[i])).append(" ");
            }
            b.append(String.format("%02X", packetData[packetData.length - 1]));
        }
        return b.toString();
    }
}
