package com.esplibrary.client.callbacks;

/**
 * Interface definition for a callback to be invoked when an ESP data has been received.
 * @param <T> Type of the desired ESP data
 *           NOTE: This type must correspond to the ESP data defined in the ESP Specification
 */
public interface ESPRequestedDataListener<T>{
    /**
     * Callback method to be invoked when the requested ESP data has been received.
     * @param data  Requested ESP data
     *              Note: This value will be null is there was an error requesting the data
     * @param error Message indicating the reason the ESP data requested failed
     *              Note: This value will be null is there was no error requesting data
     *
     */
    void onDataReceived(T data, String error);
}
