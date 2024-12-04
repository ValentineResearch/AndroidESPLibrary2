/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.data.UserSettings;
import com.esplibrary.data.V18UserSettings;
import com.esplibrary.data.V19UserSettings;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketUtils;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseUserBytes extends ESPPacket {

    private byte [] mUserBytes;

    public ResponseUserBytes(int packetLength) {
        super(packetLength);
    }

    /**
     * Returns a byte array containing the V1's user settings as packed bytes. View Valentine ESP
     * Spec. V.3 for byte definitions.
     */
    @Override
    public Object getResponseData() {
        return getUserBytes();
    }

    /**
     * Returns {@link UserSettings} based on the contained user bytes data and the specified V1
     * version.
     *
     * @apiNote This function should not be used for the Tech Display
     *
     * @param v1Version V1 version; This influences how userbytes are interpreted.
     *
     * @return UserSettings for interpreting the contained user bytes.
     *
     * @see V18UserSettings
     * @see V19UserSettings
     */
    public UserSettings getUserSettings(double v1Version) {
        return UserSettings.getUserSettingsForV1Version(v1Version, getUserBytes());
    }

    /**
     * Byte array containing user bytes stored inside of this packets payload.
     *
     * @return byte array
     */
    public byte [] getUserBytes() {
        if(mUserBytes == null) {
            mUserBytes = new byte [6];
            byte[] packetData = getPacketData();
            int offset = PacketUtils.PAYLOAD_START_IDX;
            for (int i = 0; i < mUserBytes.length; i++) {
                mUserBytes[i] = packetData[offset + i];
            }
        }
        return mUserBytes;
    }
}
