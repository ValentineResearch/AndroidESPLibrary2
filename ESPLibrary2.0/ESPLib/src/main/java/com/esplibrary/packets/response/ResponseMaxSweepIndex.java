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
public class ResponseMaxSweepIndex extends ESPPacket {

    public ResponseMaxSweepIndex(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets max sweep index from the Valentine One stored inside of this packet's payload data..
     *
     * @return Returns the max sweep index from the Valentine One. (Must type-cast to a Integer)
     */
    @Override
    public Object getResponseData() {
        return getMaxSweepIndex();
    }

    /**
     * Gets max sweep index from the Valentine One stored inside of this packet's payload data..
     *
     * @return Returns the max sweep index from the Valentine One.
     */
    public int getMaxSweepIndex() {
        byte[] packetData = getPacketData();
        return (int) packetData[PacketUtils.PAYLOAD_START_IDX];
    }
}
