package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Bundles a BluetoothDevice with it's RSSI value and last time scanned.
 *
 * <p>This class is used by {@link BluetoothDeviceAdapter}</p>
 */
public class BTBundle {

    final BluetoothDevice mDevice;
    final ConnectionType mConnType;
    int rssi;
    long mLastUpdated;

    public BTBundle(BluetoothDevice device, ConnectionType connectionType, int rssi){
        this.mDevice = device;
        this.mConnType = connectionType;
        setRSSI(rssi);
    }

    /**
     * Returns the contained {@link BluetoothDevice}
     *
     * @return BluetoothDevice
     */
    @NonNull
    public BluetoothDevice getDevice() {
        return mDevice;
    }

    /**
     * Returns the Bluetooth type of the {@link #getDevice()}
     *
     * @return Bluetooth type
     */
    @NonNull
    public ConnectionType getConnectionType() {
        return mConnType;
    }

    /**
     * Time the {@link #getDevice()} was last scanned in milliseconds.
     *
     * @return Last scan time
     */
    public long getLastTimeUpdated() {
        return mLastUpdated;
    }

    /**
     * Sets' the last time {@link #getDevice()} was scanned in milliseconds.
     *
     * @param currentTimeMillis Last time scanned
     */
    public void setLastUpdated(long currentTimeMillis) {
        mLastUpdated = currentTimeMillis;
    }

    /**
     * Returns the last read signal strength of {@link #getDevice()}.
     *
     * @return Signal strength
     */
    public int getRSSI() {
        return rssi;
    }

    /**
     * Set the signal strength of {@link #getDevice()}.
     *
     * @param rssi Signal strength
     */
    public void setRSSI(int rssi) {
        setLastUpdated(System.currentTimeMillis());
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        // If a bt device is passed into this object directly compare it to our own BluetoothDevice
        if(o.getClass() == BluetoothDevice.class) {
            return o.equals(mDevice);
        }

        if(getClass() != o.getClass()) return false;
        BTBundle btBundle = (BTBundle) o;
        return Objects.equals(mDevice, btBundle.mDevice);
    }
}
