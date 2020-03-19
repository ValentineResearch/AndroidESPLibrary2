/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Interface definition for a callback to be invoked when a {@link BluetoothDevice} has be discovered.
 */
public interface BTScanListener {
    /**
     * Callback method to be invoked when a {@link BluetoothDevice} has been scanned.
     *
     * @param device Scanned device
     * @param type Bluetooth type of device
     * @param dbm Signal strength of device
     */
    void onDeviceScanned(BluetoothScanner scanner, BluetoothDevice device, ConnectionType type, int dbm);

    /**
     * Callback method to be invoked when the scan has completed.
     * @param scanner   The {@link BluetoothScanner} that fired this callback.
     */
    void onScanCompleted(BluetoothScanner scanner);
}
