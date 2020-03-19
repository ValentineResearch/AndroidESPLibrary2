package com.esplibrary.bluetooth;

/**
 * Possible connection states
 */
public enum ConnectionEvent {
    /**
     * Disconnected state
     */
    Disconnected("Disconnected"),
    /**
     * Connecting state
     */
    Connecting("Connecting"),
    /**
     * Connected state
     */
    Connected("Connected"),
    /**
     * Connection Failed state.
     *
     * <p>Note: This status is temporary and the library immediately
     * transitions to {@link #Disconnected} without invoking the
     * {@link ConnectionListener#onConnectionEvent(ConnectionEvent, boolean)}
     * </p>
     */
    ConnectionFailed("Connection Failed"),
    /**
     * Connection lost state
     *
     * <p>Note: This status is temporary and the library immediately
     * transitions to {@link #Disconnected} without invoking the
     * {@link ConnectionListener#onConnectionEvent(ConnectionEvent, boolean)}
     * </p>
     */
    ConnectionLost("Connection Lost"),
    /**
     * Disconnecting
     */
    Disconnecting("Disconnecting");

    final String name;

    ConnectionEvent(String label) {
        this.name = label;
    }

    @Override
    public String toString() {
        return name;
    }
}
