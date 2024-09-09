package com.esplibrary.packets.request;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;

/**
 * Created by RDickerson on 06/20/2024.
 */
public class RequestAbortAudioDelay extends RequestPacket{

    public RequestAbortAudioDelay(int packetLength) {
        super(packetLength);
    }

    public RequestAbortAudioDelay(DeviceId v1Type) {
        super(v1Type, DeviceId.V1CONNECTION, v1Type, PacketId.REQABORTAUDIODELAY, null);
    }
}
