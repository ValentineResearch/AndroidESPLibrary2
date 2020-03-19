/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;

/**
 * Created by JDavis on 3/13/2016.
 */
public class InfV1Busy extends ESPPacket {

    public InfV1Busy(int packetLength) {
        super(packetLength);
    }

    /**
     * Return the data from a Response* ENSPacket.
     *
     * @return Returns response data of instance Response* ESPPacket.(Must type-cast to byte []})
     *
     * @see #getBusyPacketIds()
     */
    @Override
    public Object getResponseData() {
        return getBusyPacketIds();
    }

    /**
     * Return the packet ids that the V1 is currently processing.
     *
     * @return  byte [] containing the busy packet ids.
     */
    public int [] getBusyPacketIds() {
        byte[] packetData = getPacketData();
        int payloadLn = packetData[PacketUtils.PAYLOAD_LEN_IDX];
        if(mV1Type == DeviceId.VALENTINE_ONE) {
            payloadLn -= 1;
        }
        @PacketId.PacketID int [] data = new int [payloadLn];
        int j = 0;
        for(int i = PacketUtils.PAYLOAD_START_IDX; i < payloadLn; i++ ) {
            data[j++] = packetData[i];
        }
        return data;
    }
}
