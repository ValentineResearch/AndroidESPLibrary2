package com.esplibrary.bluetooth;

import androidx.annotation.NonNull;

/**
 * Supported Bluetooth type
 */
public enum ConnectionType {
    /**
     * V1connection (SPP/RFCOMM)
     */
    SPP ("V1 SPP"),
    /**
     * V1connection LE (BLE)
     */
    LE ("V1 LE"),
    /**
     * Demo V1connection
     */
    Demo("V1 Demo"),
    /**
     * Invalid connection
     */
    Invalid ("Invalid Connection Type");

    public final String value;

    ConnectionType(String value) { this.value = value; }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}