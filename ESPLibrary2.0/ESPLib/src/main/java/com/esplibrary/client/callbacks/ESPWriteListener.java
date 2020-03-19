package com.esplibrary.client.callbacks;

import com.esplibrary.packets.ESPPacket;

/**
 * Interface definition for a callback to be invoked when an {@link ESPPacket} has been written to the
 * ESP bus (BT)
 */
public interface ESPWriteListener {
    /**
     * Callback method to be invoked when an {@link ESPPacket} has been written.
     * @param packet Packet that was written
     */
    void onPacketWritten(ESPPacket packet);
}
