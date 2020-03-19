/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.data.SweepSection;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseSweepSections extends ESPPacket {

    public ResponseSweepSections(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the Valentine One's SweepSection[s] stored inside of the packet's payload data.
     *
     * @return Returns the Valentine One's SweepSection[s]. (Must type-cast to SweepSection [])
     */
    @Override
    public Object getResponseData() {
        return getSweepSections();
    }

    /**
     * Gets all non zero {@link SweepSection sweep sections} contained inside of this packet's payload data.
     *
     * @return All non-zero {@link SweepSection sweep sections}. Contains no null indices
     */
    public SweepSection [] getSweepSections() {
        byte[] payload = getPayloadData();
        final int plSize = payload.length;
        final int sweepSectionCnt = sectionCountFromPayloadSize(plSize);

        int validSections = 0;
        SweepSection [] sectionsTemp = new SweepSection[sweepSectionCnt];
        for (int i = 0; i < sweepSectionCnt; i++) {
            SweepSection section = new SweepSection();
            int dataOffset = i * 5;
            section.buildFromBytes(payload, dataOffset);
            if (!section.isZero()) {
                sectionsTemp[validSections++] = section;
            }
        }

        SweepSection [] sections;
        // We could potentially have empty sweep range indices due to the loop above, so we wanna
        // construct a new array containing non empty and non-zero sweeps sections.
        if(validSections != sweepSectionCnt) {
            sections = new SweepSection[validSections];
            int realSwpSecIdx = 0;
            for (int i = 0; i < sweepSectionCnt; i++) {
                SweepSection section = sectionsTemp[i];
                // Section should never be zero due to the loop above but it can be null.
                if(section != null && !section.isZero()) {
                    sections[realSwpSecIdx] = section;
                }
            }
        }
        else {
            sections = sectionsTemp;
        }
        return sections;
    }

    /**
     * Returns the number of sweep sections contained in payload data based on the size of the payload.
     *
     * @param plSize Size of the payload data.
     *
     * @return Number of sweep sections contained in payload data of length plSize.
     */
    private int sectionCountFromPayloadSize(int plSize) {
        if(15 <= plSize) {
            return 3;
        }
        else if(10 <= plSize) {
            return 2;
        }
        else if(5 <= plSize) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * Returns the number of sweep sections store inside of this packets payload.
     *
     * @return Sweep section count
     */
    public int getSweepSectionCount() {
        byte[] packetData = getPacketData();
        byte sweepDefIdx = packetData[PacketUtils.PAYLOAD_START_IDX];
        final int swpCnt = (sweepDefIdx & 0x0F);
        return swpCnt;
    }

    /**
     * Returns the number of {@link SweepSection sweep sections} contained in this packet.
     *
     * @return Number of contained {@link SweepSection sweep sections}
     */
    public int numberOfContainedSweepSections() {
        byte[] packetData = getPacketData();
        int payloadSize = packetData[PacketUtils.PAYLOAD_LEN_IDX];
        boolean chksum = ESPPacket.isChecksum(getValentineType());
        // If checksum, subtract one.
        if (chksum) {
            payloadSize--;
        }
        return sectionCountFromPayloadSize(payloadSize);
    }
}
