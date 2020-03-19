/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.data.UserSettings;

/**
 * Created by JDavis on 3/13/2016.
 */
public class RequestWriteUserBytes extends RequestPacket {

    public RequestWriteUserBytes(DeviceId v1Type, UserSettings settings) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQWRITEUSERBYTES, settings.getBytes());
    }

    public RequestWriteUserBytes(DeviceId v1Type, byte [] userBytes) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQWRITEUSERBYTES, userBytes);
    }

    public RequestWriteUserBytes(int packetLength) {
        super(packetLength);
    }
}
