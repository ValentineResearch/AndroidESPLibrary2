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
public class ResponseRequestNotProcessed extends ESPPacket {

    public ResponseRequestNotProcessed(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the packetId of the unprocessed {@link ESPPacket} stored in packet's payload data
     *
     * @return Returns the packet Id of the unprocessed packet. (Must type-cast to {@link Byte}).
     * @see #getUnprocesedPacketId()
     */
    @Override
    public Object getResponseData() {
        byte[] packetData = getPacketData();
        return packetData[PacketUtils.PAYLOAD_START_IDX];
    }

    /**
     * Gets the packetId of the unprocessed {@link ESPPacket} stored in packet's payload data.
     *
     * @return  Returns the packet Id of the unprocessed packet.
     */
    @PacketId.PacketID
    public int getUnprocesedPacketId() {
        byte[] packetData = getPacketData();
        return packetData[PacketUtils.PAYLOAD_START_IDX];
    }
}
