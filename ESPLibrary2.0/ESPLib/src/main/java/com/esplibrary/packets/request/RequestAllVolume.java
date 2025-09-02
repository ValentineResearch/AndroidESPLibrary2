package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;

/**
 * Represents a request packet to retrieve all volume settings from a device (current and saved)
 *
 */
public class RequestAllVolume extends RequestPacket {

    public RequestAllVolume(int packetLength) {
        super(packetLength);
    }

    public RequestAllVolume(DeviceId v1Type) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQALLVOLUME);
    }
}

