/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseSerialNumber extends ESPPacket {

    private String mSerialNumber = null;

    public ResponseSerialNumber(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the originators serial number stored inside of this packet's payload data.
     *
     * @return  Returns the originators Serial Number. (Must type-cast to a String)
     */
    @Override
    public Object getResponseData() {
        return getSerialNumber();
    }

    /**
     * Returns the originators serial number stored inside of this packet's payload data.
     *
     * @return  Returns the originators Serial Number.
     */
    public String getSerialNumber() {
        // Perform caching incase this method is called several times.
        if(mSerialNumber == null) {
            byte[] packetData = getPacketData();
            // Error checking
            if(packetData[PacketUtils.PACK_ID_IDX] != PacketId.RESPSERIALNUMBER) {
                return null;
            }
            int snLength = 10;

            int payloadLn = packetData[PacketUtils.PAYLOAD_LEN_IDX];
            if(mV1Type == DeviceId.VALENTINE_ONE) {
                payloadLn -= 1;
            }
            if (snLength > payloadLn) {
                // This must be an different format so just use the payload data
                snLength = payloadLn;
            }

            StringBuilder builder = new StringBuilder();
            // Iterate over the payload data and convert the values to chars.
            for(int i = 0; i < snLength; i++) {
                char temp = (char) packetData[PacketUtils.PAYLOAD_LEN_IDX + i];
                // If we encounter a zero break.
                if(temp == 0) {
                    break;
                }
                builder.append(temp);
            }
            mSerialNumber = builder.toString();
        }
        return mSerialNumber;
    }
}
