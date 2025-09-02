package com.esplibrary.packets;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.utilities.ByteList;

/**
 * Created by jdavis on 4/5/2018.
 */

public class PacketUtils {
     /*
     * Constants that represents the Seven Segment display possible values.
     */
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '0'**/
    public static final int SEVEN_SEG_VALUE_0 = 0x3f;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '1'**/
    public static final int SEVEN_SEG_VALUE_1 = 0x06;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '2'**/
    public static final int SEVEN_SEG_VALUE_2 = 0x5B;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '3'**/
    public static final int SEVEN_SEG_VALUE_3 = 0x4F;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '4'**/
    public static final int SEVEN_SEG_VALUE_4 = 0x66;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '5'**/
    public static final int SEVEN_SEG_VALUE_5 = 0x6D;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '6'**/
    public static final int SEVEN_SEG_VALUE_6 = 0x7D;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '7'**/
    public static final int SEVEN_SEG_VALUE_7 = 0x07;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '8'**/
    public static final int SEVEN_SEG_VALUE_8 = 0x7F;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '9'**/
    public static final int SEVEN_SEG_VALUE_9 = 0x6F;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'A'**/
    public static final int SEVEN_SEG_VALUE_A = 0x77;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'B'**/
    public static final int SEVEN_SEG_VALUE_b = 0x7C;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'C'**/
    public static final int SEVEN_SEG_VALUE_C = 0x39;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'D'**/
    public static final int SEVEN_SEG_VALUE_d = 0x5E;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'E'**/
    public static final int SEVEN_SEG_VALUE_E = 0x79;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'F'**/
    public static final int SEVEN_SEG_VALUE_F = 0x71;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character '#'**/
    public static final int SEVEN_SEG_VALUE_POUND = 0x49;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'amp;'**/
    public static final int SEVEN_SEG_VALUE_AMP = 0x18;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'L'**/
    public static final int SEVEN_SEG_VALUE_L = 0x38;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'J'**/
    public static final int SEVEN_SEG_VALUE_J = 0x1E;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'c'**/
    public static final int SEVEN_SEG_VALUE_c = 0x58;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'U'**/
    public static final int SEVEN_SEG_VALUE_U = 0x3E;
    /**Constant value that represents the Valentine One's Seven Segment displaying the character 'u'**/
    public static final int SEVEN_SEG_VALUE_u = 0x1C;

    public final static byte ESP_PACKET_SOF = (byte) 0xAA;
    public final static byte ESP_PACKET_EOF = (byte) 0xAB;
    public final static byte DEST_INDENTIFIER_BASE_CONST = (byte) 0xD0;
    public final static byte ORIG_INDENTIFIER_BASE_CONST = (byte) 0xE0;
    public final static byte SPP_PACKET_DELIMITER_BYTE = 0x7F;
    public final static byte DATA_LINK_ESCAPE_BYTE_7D = 0X7D;
    public final static byte DATA_LINK_ESCAPE_BYTE_5F = 0X5F;
    public final static byte DATA_LINK_ESCAPE_BYTE_5D = 0X5D;

    public final static int SOF_IDX = 0;
    public final static int DEST_IDX = 1;
    public final static int ORIG_IDX = 2;
    public final static int PACK_ID_IDX = 3;
    public final static int PAYLOAD_LEN_IDX = 4;
    public final static int PAYLOAD_START_IDX = 5;

    private static ByteList sCopyCache;

    /**
     * Utility method for determining if buffer contains valid ESP framing data.
     *
     * @param buffer Source data
     * @param start starting index (inclusive)
     * @param stop stop index (exclusive)
     * @param v1Type current V1 type.
     *
     * @return True if the framing data is valid
     */
    private final static boolean isValidESPFramingData(ByteList buffer, int start, int stop, DeviceId v1Type) {
        byte sof = buffer.get(start + 0);
        if((sof != PacketUtils.ESP_PACKET_SOF)) {
            return false;
        }

        byte destId = buffer.get(start + 1);
        if((destId & PacketUtils.DEST_INDENTIFIER_BASE_CONST) != PacketUtils.DEST_INDENTIFIER_BASE_CONST) {
            return false;
        }

        byte origId = buffer.get(start + 2);
        if((origId & PacketUtils.ORIG_INDENTIFIER_BASE_CONST) != PacketUtils.ORIG_INDENTIFIER_BASE_CONST) {
            return false;
        }

        byte eof = buffer.get(stop - 1);
        if((eof != PacketUtils.ESP_PACKET_EOF)) {
            buffer.clear();
            return false;
        }

        byte packetId = buffer.get(start + 3);

        // If we haven't determined the V1 type throw this packet away.
        if(!isFromV1(origId) && v1Type == DeviceId.UNKNOWN_DEVICE) {
            // If this packet ID is a version response, allow it through.
            // If the origin is from the V1connection, accept it.
            if(packetId != PacketId.RESPVERSION || origId != DeviceId.V1CONNECTION.toByte()) {
                return false;
            }
        }

        boolean useChksum = v1Type == DeviceId.VALENTINE_ONE;
        // If the packet is directly from the V1, we wanna override the useChksum boolean
        if(isFromV1(origId)) {
            byte newOrig = (byte) (origId - ORIG_INDENTIFIER_BASE_CONST);
            useChksum = newOrig == DeviceId.VALENTINE_ONE.toByte();
        }

        if(useChksum) {
            int calCksum = buffer.calculateSumNoCarry(0, buffer.size() - 2);
            byte foundChkSm = buffer.get(stop - 2);
            // Checksum isn't valid, return false
            if(foundChkSm != calCksum) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indicate if the origin is from a Valentine One whether checksum, no checksum or Legacy
     * @param orig  Origin to check
     * @return  True if the origin is from a Valentine One
     */
    public final static boolean isFromV1(byte orig) {
        orig = (byte) (orig - ORIG_INDENTIFIER_BASE_CONST);
        return ((orig == DeviceId.VALENTINE_ONE.toByte()) ||
                (orig == DeviceId.VALENTINE_ONE_NO_CHECKSUM.toByte()) ||
                (orig == DeviceId.VALENTINE_ONE_LEGACY.toByte()));
    }

    /**
     * Creates an ESPPacket with the ESP byte data in buffer.
     *
     * @param factory Factory for constructing a ESPPacket
     * @param buffer Buffer holding byte data
     * @param v1Type V1 type
     *
     * @return ESPPacket if the byte data in buffer contains valid ESP data.
     */
    public final static ESPPacket makeFromBufferLE(PacketFactory factory, ByteList buffer, DeviceId v1Type) {
        int size = buffer.size();
        if(size < 6) {
            return null;
        }
        ESPPacket packet = null;
        // Make sure the start of frame, destination, origination, and end of frame byte are valid.
        if(isValidESPFramingData(buffer, 0 , size, v1Type)) {
            int packetId = buffer.get(3) & 0xFF;
            packet = factory.getPacketForId(packetId, size);
            // Copy the buffer into the packet's backing array.
            buffer.copyTo(packet.packetData);
            // If the packet is from a V1, update the packets V1 type otherwise we want to use the previously determined V1Type.
            if(packet.isFromV1()) {
                packet.mV1Type = packet.getOrigin();
            }
            else {
                packet.mV1Type = v1Type;
            }
        }
        // Always clear the buffer once we've retrieved all of the byte data.
        buffer.clear();
        return packet;
    }

    /**
     * Removes data link escape bytes as well as removes the Android BT packets.
     * NOTE: This function assumes packetData is a valid, delimited Android BT packet
     *
     * @param packetData    Android BT packet to strip of Android BT packet formatting
     * @return  Byte array contain the raw transmitted ESP data
     */
    public final static byte [] removeDelimitersDLEBytes(byte [] packetData) {
        int dlePackCnt = 0;
        // Walk packetData and count all instances of DLE bytes we encounter. We will use this count
        // to determine the true size of the packetData
        for (int i = 0; i < packetData.length; i++) {
            byte b = packetData[i];
            // Any time we encounter 7D we assume it's an DLE byte and we increment the timer.
            if(b == PacketUtils.DATA_LINK_ESCAPE_BYTE_7D) {
                dlePackCnt++;
            }
        }
        // Create a new array that will hold the unescaped packet data.
        byte [] delStrippedData = new byte[packetData.length - dlePackCnt];
        int byteIdx = 0;
        // We wanna walk packetData and every
        for (int i = 0; i < packetData.length; i++) {
            byte b = packetData[i];
            // If we encounter a DLE byte, we wanna look ahead and determine what the actual value
            // should be.
            if(b == DATA_LINK_ESCAPE_BYTE_7D) {
                // Calculate the next position beyond the current
                final int nextIdx = (i + 1);
                if(nextIdx < packetData.length) {
                    // If the next byte is equal to 5D, we wanna set 'b' equal to '7D'
                    if(packetData[nextIdx] == DATA_LINK_ESCAPE_BYTE_5D) {
                        i++;
                        b = DATA_LINK_ESCAPE_BYTE_7D;
                    }
                    // If the next byte is equal to 5D, we wanna set 'b' equal to '7F'
                    else if(packetData[nextIdx] == DATA_LINK_ESCAPE_BYTE_5F) {
                        i++;
                        b = SPP_PACKET_DELIMITER_BYTE;
                    }
                }
            }
            delStrippedData[byteIdx++] = b;
        }
        return delStrippedData;
    }


    /**
     * Validates an array of Android BT packet data to determine if the checksum is valid
     * NOTE: This function assumes that packetData contains a both a start and end packet delimiter
     * bytes and packet Checksum at the second to last
     * position
     *
     * @param packetData    Android BT packet to validate
     *
     * @return              True if the packetData is a valid Android BT packet
     */
    public final static boolean validateFrameChecksum(byte [] packetData) {
        byte expectedChecksum = packetData[packetData.length - 2];
        byte calculated = 0x00;
        for (int i = 1, size = packetData.length - 2; i < size; i++) {
            calculated += packetData[i];
        }
        return calculated == expectedChecksum;
    }

    /**
     * Utility method for creating an ESP packet from the SPP byte data inside of buffer.
     *
     * @param factory Packet Factory
     * @param buffer Buffer containing ESP byte data
     * @param v1Type Last known V1 type (this is used as a hint for determining if checksums are used)
     *
     * @return ESPPacket constructed using the ESP byte data in buffer. Null if the data contained
     * in buffer is in valid.
     */
    public final static ESPPacket makeFromBufferSPP(PacketFactory factory, ByteList buffer, DeviceId v1Type) {
        int size = buffer.size();
        int startIndex = -1;
        int endIndex = -1;
        for(int i = 0; i < size; i++) {
            byte b = buffer.get(i);
            if (b == PacketUtils.SPP_PACKET_DELIMITER_BYTE) {
                if (startIndex == -1) {
                    startIndex = i;
                } else {
                    // We want to keep checking to see that the next byte is equal to a frame
                    // delimiter. We do this in case we have a partial esp packet in the front of
                    // the buffer. We can't recover partial data in the start of the buffer.
                    if (i == startIndex + 1) {
                        startIndex = i;
                    } else {
                        endIndex = i;
                        // Once we find the last index of the delimiter byteJ, we want to break so
                        // we don't starting reading in another packet.
                        break;
                    }
                }
            }
        }
        // If we don't have both a starting and ending packet delimiter, return because we haven't yet received a full packet.
        if(startIndex == -1 || endIndex == -1) {
            // Return null and leave the buffer untouched.
            return null;
        }
        // Trim of the data in front that we cannot use.
        if(startIndex != 0) {
            buffer.removeRange(0, startIndex, false);
            endIndex -= startIndex;
            startIndex = 0;
        }

        byte sPD = buffer.get(0);
        if((sPD != PacketUtils.SPP_PACKET_DELIMITER_BYTE)) {
            buffer.removeRange(startIndex, endIndex + 1, false);
            return null;
        }

        // Make sure the checksums are correct
        // Check to see if the packet length byte has been delimited with PACK bytes
        int messageDataStart = 2;
        byte packetLength = buffer.get(1);
        if(packetLength == PacketUtils.DATA_LINK_ESCAPE_BYTE_7D) {
            messageDataStart++;
            if(packetLength == PacketUtils.DATA_LINK_ESCAPE_BYTE_5D) {
                packetLength = PacketUtils.DATA_LINK_ESCAPE_BYTE_7D;
            }
            else if(packetLength == PacketUtils.DATA_LINK_ESCAPE_BYTE_5F) {
                packetLength = PacketUtils.SPP_PACKET_DELIMITER_BYTE;
            }
        }

        int messageDataStop = endIndex - 1;
        // Grab the packet checksum
        byte packetChksm = buffer.get(messageDataStop);
        // Check to see if the packet checksum has been delimited with PACK bytes
        if(buffer.get(endIndex - 2) == PacketUtils.DATA_LINK_ESCAPE_BYTE_7D) {
            messageDataStop--;
            if(packetChksm == PacketUtils.DATA_LINK_ESCAPE_BYTE_5D) {
                packetChksm = PacketUtils.DATA_LINK_ESCAPE_BYTE_7D;
            }
            else if(packetChksm == PacketUtils.DATA_LINK_ESCAPE_BYTE_5F) {
                packetChksm = PacketUtils.SPP_PACKET_DELIMITER_BYTE;
            }
        }

        byte calculatedChksum = packetLength;
        for (int i = messageDataStart; i < messageDataStop; i++) {
            byte b = buffer.get(i);
            if(b == PacketUtils.DATA_LINK_ESCAPE_BYTE_7D) {
                i++;
                b = buffer.get(i);
                if(b == PacketUtils.DATA_LINK_ESCAPE_BYTE_5D) {
                    b = PacketUtils.DATA_LINK_ESCAPE_BYTE_7D;
                }
                else if(b == PacketUtils.DATA_LINK_ESCAPE_BYTE_5F) {
                    b = PacketUtils.SPP_PACKET_DELIMITER_BYTE;
                }
            }
            calculatedChksum += b;
        }

        if(calculatedChksum != packetChksm) {
            buffer.removeRange(startIndex, endIndex + 1, false);
            return null;
        }


        // Byte list cache
        if (sCopyCache == null) {
            sCopyCache = new ByteList(15);
        }

        // Strip the junk
        stripDLEBytes(buffer, sCopyCache, messageDataStart, messageDataStop);
        buffer.removeRange(startIndex, endIndex + 1, false);
        ESPPacket packet = makeFromBufferLE(factory, sCopyCache, v1Type);
        return packet;
    }

    /**
     * Utility for removing delimiter bytes from original.
     *
     * @param original Source data
     * @param copyList Destination array.
     * @param start  Start index (inclusive)
     * @param stop Stop index (exclusive)
     */
    private final static void stripDLEBytes(ByteList original, ByteList copyList, int start, int stop) {
        copyList.clear();
        for (int i = start; i < stop; i++) {
            byte b = original.get(i);
            if(b == PacketUtils.DATA_LINK_ESCAPE_BYTE_7D) {
                i++;
                b = original.get(i);
                if(b == PacketUtils.DATA_LINK_ESCAPE_BYTE_5D) {
                    b = PacketUtils.DATA_LINK_ESCAPE_BYTE_7D;
                }
                else if(b == PacketUtils.DATA_LINK_ESCAPE_BYTE_5F) {
                    b = PacketUtils.SPP_PACKET_DELIMITER_BYTE;
                }
            }
            copyList.add(b);
        }
    }

    /**
     * Utility method for calculating a checksum by summing the bytes in data.
     *
     * @param data Source array
     * @param length number of bytes to use when calculating the checksum.
     *
     * @return Checksum calculate from data. If data is empty, zero is returned.
     */
    public final static byte calculateChecksumFor(byte [] data, int length) {
        byte cksum = 0x00;
        for (int i = 0; i < length; i++) {
            cksum += data[i];
        }
        return cksum;
    }
}
