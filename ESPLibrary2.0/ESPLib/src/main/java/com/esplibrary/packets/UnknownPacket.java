/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets;

/**
 * This packet does nothing nothing, and simply is used to indicate that the V1 does not
 * support the packet.
 */
public class UnknownPacket extends ESPPacket {

    public UnknownPacket(int packetLength) {
        super(packetLength);
    }
}
