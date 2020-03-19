package com.esplibrary.client.callbacks;

/**
 * Interface definition for a callback to be invoked when no ESP data has been received.
 */
public interface NoDataListener {
    /**
     * Callback method to be invoked when no ESP data has been received within a configurable
     * timeout.
     * Note: {@link com.esplibrary.client.IESPClient#setDataTimeout(long)} allows you to configure
     * the no data timeout.
     */
    void onNoDataDetected();
}
