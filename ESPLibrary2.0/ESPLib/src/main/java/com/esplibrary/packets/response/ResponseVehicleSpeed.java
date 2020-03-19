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
public class ResponseVehicleSpeed extends ESPPacket {

    public ResponseVehicleSpeed(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the Vehicle speed from the Savvy stored inside of this packet's payload data.
     *
     * @return  Returns the vehicle speed. (Must type-cast to Integer)
     */
    @Override
    public Object getResponseData() {
        return getVehicleSpeed();
    }

    /**
     * Gets the Vehicle speed from the Savvy stored inside of this packet's payload data.
     *
     * @return  Returns the vehicle speed.
     */
    public int getVehicleSpeed() {
        byte[] packetData = getPacketData();
        return packetData[PacketUtils.PAYLOAD_START_IDX] & 0x000000FF;
    }
}
