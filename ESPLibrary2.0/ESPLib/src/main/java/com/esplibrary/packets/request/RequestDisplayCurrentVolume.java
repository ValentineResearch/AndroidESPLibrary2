package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;

/**
 * Created by RDickerson on 06/20/2024.
 */
public class RequestDisplayCurrentVolume extends RequestPacket{

    public RequestDisplayCurrentVolume(int packetLength) {
        super(packetLength);
    }

    public RequestDisplayCurrentVolume(DeviceId v1Type) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQDISPLAYCURRENTVOLUME, null);
    }
}
