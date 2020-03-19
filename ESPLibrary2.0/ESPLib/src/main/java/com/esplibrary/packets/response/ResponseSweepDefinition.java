/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.data.SweepDefinition;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseSweepDefinition extends ESPPacket {

    private SweepDefinition mSweepDefinition;

    public ResponseSweepDefinition(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the SweepDefinition stored inside of this packet's payload data.
     *
     * @return Returns a SweepDefinition. (Must type-cast to SweepDefinition)
     */
    @Override
    public Object getResponseData() {
        // Pass through to getSweepDefinition.
        return getSweepDefinition();
    }

    /**
     * Gets the SweepDefinition stored inside of this packet's payload data.
     *
     * @return Returns a SweepDefinition.
     */
    public SweepDefinition getSweepDefinition() {
        // Perform caching of SweepDefinition.
        if(mSweepDefinition == null) {
            mSweepDefinition = new SweepDefinition();
            byte[] packetData = getPacketData();
            // Build SweepDefinition using payload data.
            mSweepDefinition.buildFromBytes(packetData, PacketUtils.PAYLOAD_START_IDX);
        }

        return mSweepDefinition;
    }

}
