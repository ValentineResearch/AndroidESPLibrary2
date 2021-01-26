package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Interface definition for a callback to be invoked when RSSI has been determined for a BLE
 * device.
 */
public interface RSSICallback {
    /**
     * Callback method to be invoked when RSSI has been received.
     *
     * @param device the remote bluetooth device for which this RSSI callback was invoked.
     * @param rssi the received signal strength in dBm. The valid range is [-127, 126]
     */
    void onRssiReceived(BluetoothDevice device, int rssi);
}