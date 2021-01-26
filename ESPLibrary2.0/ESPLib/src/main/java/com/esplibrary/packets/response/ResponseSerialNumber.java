/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.packets.ESPPacket;

import java.nio.charset.StandardCharsets;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseSerialNumber extends ESPPacket {

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
        byte[] payload = getPayloadData();
        return new String(payload, StandardCharsets.UTF_8);
    }
}
