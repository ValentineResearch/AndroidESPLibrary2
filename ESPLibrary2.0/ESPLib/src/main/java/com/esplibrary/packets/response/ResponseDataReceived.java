/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.packets.ESPPacket;

/**
 * Created by JDavis on 3/13/2016.
 *
 * This class deliberate doesn't have logic and doesn't return anything because
 * the existence of this packet on the ESP bus acts as an inidcator for the
 * Conceal Display being attached.
 */
public class ResponseDataReceived extends ESPPacket {

    public ResponseDataReceived(int packetLength) {
        super(packetLength);
    }
}
