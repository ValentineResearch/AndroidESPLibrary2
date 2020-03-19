/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.constants.PacketId;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseUnsupported extends ESPPacket {

    public ResponseUnsupported(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the unsupported packet identifier store inside of this packet's payload data.
     *
     * @return Returns the unsupported packet identifier. (Must type-cast to Byte)
     */
    @Override
    public Object getResponseData() {
        byte[] packetData = getPacketData();
        return packetData[PacketUtils.PAYLOAD_START_IDX];
    }

    /**
     * Gets the unsupported packet identifier store inside of this packet's payload data.
     *
     * @return Returns the unsupported packet identifier.
     */
    @PacketId.PacketID
    public int getUnsupportedPacketId() {
        byte[] packetData = getPacketData();
        return (int) packetData[PacketUtils.PAYLOAD_START_IDX];
    }
}
