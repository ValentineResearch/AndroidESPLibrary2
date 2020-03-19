/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.response;

import com.esplibrary.packets.ESPPacket;

/**
 * Created by JDavis on 3/13/2016.
 */
public class ResponseVersion extends ESPPacket {

    private String mVersion;

    public ResponseVersion(int packetLength) {
        super(packetLength);
    }

    /**
     * Gets the version of Valentine One stored inside of this packet's payload data.
     *
     * @return Returns the version of Valentine One. (Must type-cast to String)
     */
    @Override
    public Object getResponseData() {
        return getVersion();
    }

    /**
     * Gets the version of Valentine One stored inside of this packet's payload data.
     *
     * @return Returns the version of Valentine One.
     */
    public String getVersion() {
        if(mVersion == null) {
            /*
             0 The version identification letter for the responding device
                ‘v’ for Valentine One
                ‘C’ for Concealed Display
                ‘R’ for Remote Audio
                ‘S’ for Savvy
             1 ASCII value of the major version number.
             2 Decimal point (‘.’)
             3 ASCII value of the minor version number.
             4 ASCII value of the first digit of the revision number.
             5 ASCII value of the second digit of the revision number.
             6 ASCII value of the Engineering Control Number.
            */
            byte[] payloadData = getPayloadData();
            StringBuilder builder = new StringBuilder();
            // Find the version String length.
            for(int i = 0; i < payloadData.length; i++) {
                char temp = (char) payloadData[i];
                // If we encounter a zero break.
                if(temp == 0) {
                    break;
                }
                builder.append(temp);
            }
            // Return the string data store inside of the StringBuilder.
            mVersion = builder.toString();
        }
        return  mVersion;
    }

    /**
     * Returns the versions stirng as a {@code double}.
     *
     * @return {@code double} equivalent of the version string
     */
    public double getVersionAsDouble() {
        return getVersionDouble(getVersion());
    }

    /**
     * Converts a valid ESP version string into a {@code double}.
     *
     * @param versionStr ESP device version string
     * @return {@code double} equivalent of version string
     */
    public static double getVersionDouble(String versionStr) {
        double version = 0.0d;
        if(versionStr.length() == 7 && Character.isAlphabetic(versionStr.codePointAt(0))) {
            version = Double.parseDouble(versionStr.substring(1));
        }
        return version;
    }
}
