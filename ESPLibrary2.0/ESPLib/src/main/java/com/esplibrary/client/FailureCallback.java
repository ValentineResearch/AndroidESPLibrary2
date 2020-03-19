package com.esplibrary.client;

/**
 * Interface definition for a callback to be invoked when an ESP failure occurs.
 */
public interface FailureCallback {
    /**
     * Callback method to be invoked when a failure occurs.
     *
     * @param error Error describing the failure.
     */
    void onFailure(String error);
}
