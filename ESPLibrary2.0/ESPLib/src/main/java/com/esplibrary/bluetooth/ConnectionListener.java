package com.esplibrary.bluetooth;

/**
 * Interface definition for a callback to be invoked when a connection event has occurred.
 */
public interface ConnectionListener {

    /**
     *
     * Callback method to be invoked when a connection event has occurred.
     *
     * @param event The connection event that has occurred.
     * @param demo Indicates if the connectionEvent was for a demo device.
     */
    void onConnectionEvent(ConnectionEvent event, boolean demo);
}
