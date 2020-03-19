/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.data.SAVVYStatus;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseSAVVYStatus extends ESPPacket {

    public ResponseSAVVYStatus(int packetLength) {
        super(packetLength);
    }

    /**
     * Checks if the Threshold is overridden.
     *
     * @return  Returns true if the threshold is being overridden otherwise, returns false.
     */
    public boolean isThresholdOverriddenByUser() {
        byte[] packetData = getPacketData();
        byte status = packetData[PacketUtils.PAYLOAD_START_IDX + 1];
        return (status & (byte) 0x01) != 0;
    }

    /**
     * Checks if Unmute is enabled.
     *
     * @return  Returns true if Unmute is enabled otherwise, returns false.
     */
    public boolean isUmmuteEnabled() {
        byte[] packetData = getPacketData();
        byte status = packetData[PacketUtils.PAYLOAD_START_IDX + 1];
        return (status & (byte) 0x02) != 0;
    }

    /**
     * Gets the Savvy's Speed Threshold.
     *
     * @return  Returns the Savvy's Speed Threshold.
     */
    public int getSpeedThreshold() {
        byte[] packetData = getPacketData();
        byte status = packetData[PacketUtils.PAYLOAD_START_IDX];
        return (status & 0xFF);
    }

    /**
     * Returns the Savvy's current configuration
     * @return  Savvy's current configuration
     */
    public SAVVYStatus getSavvyStatus() {
        return new SAVVYStatus(getSpeedThreshold(), isThresholdOverriddenByUser(), isUmmuteEnabled());
    }
}
