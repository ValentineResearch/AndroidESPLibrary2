/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.data.AlertData;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseAlertData extends ESPPacket {

    public ResponseAlertData(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the {@link AlertData} stored inside of this packet's payload data.
     *
     * @return  Returns the {@link AlertData} from the payload data. (Must type-cast to {@link AlertData})
     * @see #getAlertData()
     */
    @Override
    public Object getResponseData() {
        return getAlertData();
    }

    /**
     * Gets the {@link AlertData} stored inside of this packet's payload data.
     *
     * @return  Returns the {@link AlertData} from the payload data.
     */
    public AlertData getAlertData() {
        return new AlertData(getPacketData(), PacketUtils.PAYLOAD_START_IDX, 7);
    }
}
