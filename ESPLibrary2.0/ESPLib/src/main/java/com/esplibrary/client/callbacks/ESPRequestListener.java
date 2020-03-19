package com.esplibrary.client.callbacks;

/**
 * Interface definition for a callback to be invoked when packet ESP data has been sent and there
 * is no corresponding data to be returned.
 */
public interface ESPRequestListener {
    /**
     * Callback method to be invoked when a packet the packet has been successfully sent.
     * @param error A nullable message indicating why the packet failed.
     */
    void onRequestCompleted(String error);
}
