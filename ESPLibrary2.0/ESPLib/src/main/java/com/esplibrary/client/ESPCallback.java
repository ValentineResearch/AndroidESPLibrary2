package com.esplibrary.client;

import com.esplibrary.packets.ESPPacket;

/**
 * Interface definition for a callback to be invoked when an {@link ESPPacket} is received.
 *
 * <p>This inteface is to be used by {@link ResponseHandler}</p>
 *
 * @param <P> Typed ESPPacket
 */
public interface ESPCallback<P extends ESPPacket> {
    /**
     * Callback method to be invoked when an ESP packet is received.
     * @param packet
     */
    boolean onPacketReceived(P packet);
}
