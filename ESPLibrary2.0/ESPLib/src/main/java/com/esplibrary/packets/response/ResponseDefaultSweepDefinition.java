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
 * Created by JDavis on 7/10/2016.
 */
public class ResponseDefaultSweepDefinition extends ESPPacket {

    private SweepDefinition mSweepDef;

    public ResponseDefaultSweepDefinition(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the {@link SweepDefinition} stored inside of this packet's payload data.
     *
     * @return Returns a {@link SweepDefinition}. (Must type-cast to {@link SweepDefinition})
     */
    @Override
    public Object getResponseData() {
        // Pass through to getSweepDefinition.
        return getSweepDefinition();
    }

    /**
     * Gets the {@link SweepDefinition} stored inside of this packet's payload data.
     *
     * @return Returns the {@link SweepDefinition} inside of this packet's payload data.
     */
    public SweepDefinition getSweepDefinition() {
        if(mSweepDef == null) {
            mSweepDef = new SweepDefinition();
            byte[] packetData = getPacketData();
            mSweepDef.buildFromBytes(packetData, PacketUtils.PAYLOAD_START_IDX);
        }
        return mSweepDef;
    }
}