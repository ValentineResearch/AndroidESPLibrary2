package com.esplibrary.client.callbacks;

/**
 * Interface definition for a callback to be invoked when invalid ESP data has been received.
 */
public interface MalformedDataListener {
    /**
     * Callback method to be invoked when bad ESP data is received.
     * @param message   A message detailing the bad ESP data.
     */
    void onBadESPData(String message);
}



