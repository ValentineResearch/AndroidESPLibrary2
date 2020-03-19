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
public class ResponseBatteryVoltage extends ESPPacket {

    private String mBatteryVoltage = null;

    public ResponseBatteryVoltage(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the Battery Voltage as a String from this EPSPacket's payload data.
     *
     * @return  Returns the Battery Voltage from the payload data. (Must type-cast to {@link AlertData})
     * @see #getBatteryVoltage()
     */
    @Override
    public Object getResponseData() {
        return getBatteryVoltage();
    }

    /**
     * Gets the battery voltage stored inside of this packet's payload data.
     *
     * @return  Returns a String representing the battery voltage.
     */
    public String getBatteryVoltage() {
        if(mBatteryVoltage == null) {
            byte[] packetData = getPacketData();
            // Construct a String containing the battery Voltage.
            mBatteryVoltage = new StringBuilder().append(Byte.toString(packetData[PacketUtils.PAYLOAD_START_IDX + 0]))
                    .append(".").append(Byte.toString(packetData[PacketUtils.PAYLOAD_START_IDX + 1])).toString();
        }
        return mBatteryVoltage;
    }
}
