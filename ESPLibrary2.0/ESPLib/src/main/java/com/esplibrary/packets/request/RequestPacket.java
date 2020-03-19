package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by jdavis on 4/9/2018.
 */

public abstract class RequestPacket extends ESPPacket {

    protected RequestPacket(int packetLength) {
        super(packetLength);
    }

    public RequestPacket(DeviceId v1Type, DeviceId dest, @PacketId.PacketID int packetId, byte... payload) {
        this(v1Type, DeviceId.V1CONNECTION, dest, packetId, payload);
    }

    public RequestPacket(DeviceId v1Type, DeviceId origin, DeviceId dest, @PacketId.PacketID int packetId, byte... payload) {
        super(v1Type, payload);
        init(v1Type, origin, dest, packetId, payload);
    }

    /**
     * Initializes the {@link RequestPacket packet} with the provided packet data.
     *
     * @param v1Type
     * @param origin
     * @param dest
     * @param packetId
     * @param payload
     */
    private void init(DeviceId v1Type, DeviceId origin, DeviceId dest, @PacketId.PacketID int packetId, byte... payload) {
        byte[] packetData = getPacketData();
        packetData[0] = PacketUtils.ESP_PACKET_SOF;
        packetData[1] = (byte) (dest.toByte() | PacketUtils.DEST_INDENTIFIER_BASE_CONST);
        packetData[2] = (byte) (origin.toByte() | PacketUtils.ORIG_INDENTIFIER_BASE_CONST);
        packetData[3] = (byte) (packetId & 0xFF);

        boolean chksum = (v1Type == DeviceId.VALENTINE_ONE);
        packetData[4] = getPacketLengthForPayload(payload, chksum);

        if(payload != null) {
            System.arraycopy(payload, 0, packetData, 5, payload.length);
        }

        if(chksum) {
            packetData[packetData.length - 2] = PacketUtils.calculateChecksumFor(packetData, packetData.length - 2);
        }
        packetData[packetData.length - 1] = PacketUtils.ESP_PACKET_EOF;
        this.mV1Type = v1Type;
    }

    /**
     * Helper method for determining the payload length byte of an {@link ESPPacket} based on the payload array and if checksums are present.
     * @param data      Payload data for an {@link ESPPacket}.
     * @param checksum  Flag that indicates if checksums are to be used.
     * @return  Value that denotes the payload length of an {@link ESPPacket}. This value will account includes the checksum byte.
     */
    private final static byte getPacketLengthForPayload(byte [] data, boolean checksum) {
        if(data == null) {
            return (byte) (checksum ?  1 : 0);
        }
        return (byte) (checksum ? data.length + 1 : data.length);
    }

    @PacketId.PacketID
    protected final static int convertIntToPacketId(int packetId) {
        return packetId;
    }
}