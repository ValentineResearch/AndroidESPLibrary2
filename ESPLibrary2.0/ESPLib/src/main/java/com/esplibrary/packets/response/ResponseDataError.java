
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
public class ResponseDataError extends ESPPacket {

    public ResponseDataError(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the PacketIds of the invalid packet stored inside of this packet's payload data.
     *
     * @return Returns the invalid packetId. (Must type-cast to Byte)
     */
    @Override
    public Object getResponseData() {
        return getInvalidPacketId();
    }

    /**
     * Gets the PacketIds of the invalid packet stored inside of this packet's payload data.
     *
     * @return Returns the invalid packetId.
     */
    @PacketId.PacketID
    public int getInvalidPacketId() {
        return getPacketData()[PacketUtils.PAYLOAD_START_IDX];
    }
}
