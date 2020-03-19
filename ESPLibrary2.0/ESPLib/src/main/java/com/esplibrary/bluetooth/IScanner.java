package com.esplibrary.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

/**
 * Interface for interacting with a V1 {@link BluetoothDevice} Scanner.
 */
public interface IScanner {
    /**
     * Sets a listener that will received callbacks every time a {@link BluetoothDevice} is scanned.
     */
    void setScanCallback(BTScanListener listener);
    /**
     * Sets the {@link BluetoothDevice} scan timeout.
     * @param timeout the scan timeout in milliseconds.
     */
    void setTimeout(long timeout);
    /**
     * Set scan mode for Bluetooth LE scan.
     *
     * @param scanMode The scan mode can be one of {@link ScanSettings#SCAN_MODE_LOW_POWER},
     *            {@link ScanSettings#SCAN_MODE_BALANCED} or
     *            {@link ScanSettings#SCAN_MODE_LOW_LATENCY}.
     * @throws IllegalArgumentException If the {@code scanMode} is invalid.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void setScanMode(int scanMode);

    /**
     * Asynchronously scans for a {@link BluetoothDevice} with the MAC address {@code ==} address.
     * @param address   The address to scan for.
     * @param type      The type of the {@link BluetoothDevice}.
     *
     * @return  True if the device scan has been started.
     */
    boolean scanForDevice(String address, ConnectionType type);

    /**
     * Start a scan for the specified {@link ConnectionType} type.
     * @param type  Connection type to scan. Note {@link ConnectionType#Demo}
     *              is not a valid scan type
     *
     * @return  True if a scan was started.
     */
    boolean scanForType(ConnectionType type);
    /** Indicates if a scanning is currently taking place. */
    boolean isScanning();
    /** Stops the {@link BluetoothDevice} scan taking place. */
    void stopScan();
}
