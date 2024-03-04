/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.utilities.V1VersionInfo;

/**
 * Created by JDavis on 3/13/2016.
 */
public class RequestTurnOffMainDisplay extends RequestPacket {

    public RequestTurnOffMainDisplay(DeviceId v1Type) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQTURNOFFMAINDISPLAY, null);
    }

    public RequestTurnOffMainDisplay(DeviceId v1Type, double v1Version, boolean keepBTLedOn) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQTURNOFFMAINDISPLAY, null);

        if ( keepBTLedOn && V1VersionInfo.isKeepBTLedOnAvailable(v1Version)){
            // Only add the optional payload byte if keeping the Bluetooth LED on and the V1 supports the feature.
            reinitPacket (getValentineType(), getOrigin(), getDestination(), PacketId.REQTURNOFFMAINDISPLAY, (byte)0x01);
        };
    }

    public RequestTurnOffMainDisplay(int packetLength) {
        super(packetLength);
    }
}
