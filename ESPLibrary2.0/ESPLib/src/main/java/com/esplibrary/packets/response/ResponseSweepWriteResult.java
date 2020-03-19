/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseSweepWriteResult extends ESPPacket {

    public ResponseSweepWriteResult(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the result of the sweep write store inside of this packet's payload data.
     *
     * @return Returns the result of the sweep write. (Must type-cast to Integer).
     */
    @Override
    public Object getResponseData() {
        byte[] packetData = getPacketData();
        // Cast to int, this will be autoboxed to an Integer.
        return (int) packetData[PacketUtils.PAYLOAD_START_IDX];
    }

    /**
     * Gets the result of the sweep write store inside of this packet's payload data.
     *
     * @return Returns the result of the sweep write.
     */
    public int getWriteResult() {
        byte[] packetData = getPacketData();
        return (int) packetData[PacketUtils.PAYLOAD_START_IDX];
    }
}
