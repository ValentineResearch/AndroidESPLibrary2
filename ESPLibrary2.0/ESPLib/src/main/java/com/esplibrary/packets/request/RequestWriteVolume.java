package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;

/**
 * Created by JDavis on 05/04/2021.
 */
public class RequestWriteVolume extends RequestPacket {

    public RequestWriteVolume(int packetLength) {
        super(packetLength);
    }

    public RequestWriteVolume(DeviceId v1Type, byte mainVolume, byte currentVolume, byte aux0) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQWRITEVOLUME, mainVolume,
                currentVolume, aux0);
    }
}
