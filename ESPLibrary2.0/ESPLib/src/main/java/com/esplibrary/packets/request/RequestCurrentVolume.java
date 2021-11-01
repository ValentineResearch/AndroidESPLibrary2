package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;

/**
 * Created by JDavis on 05/04/2021.
 */
public class RequestCurrentVolume extends RequestPacket {

    public RequestCurrentVolume(int packetLength) {
        super(packetLength);
    }

    public RequestCurrentVolume(DeviceId v1Type) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQCURRENTVOLUME);
    }
}
