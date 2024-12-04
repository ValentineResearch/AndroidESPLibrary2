/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.constants;

/**
 * Valid ESP device IDs
 */
public enum DeviceId {

    /** Concealed Display ESP device */
    CONCEALED_DISPLAY(Constants.CONCEALED_DISPLAY_BYTE, "Concealed Display"),
    /** Tech Display ESP device uses the same device id as the Concealed Display */
    TECH_DISPLAY(Constants.TECH_DISPLAY_BYTE, "Tech Display"),
    /**Remote Audio ESP device */
    REMOTE_AUDIO(Constants.REMOTE_AUDIO_BYTE, "Remote Audio"),
    /**Savvy ESP device */
    SAVVY(Constants.SAVVY_BYTE, "SAVVY"),
    /**Third party assigned ESP device #1 */
    THIRD_PARTY_1(Constants.THIRD_PARTY_1_BYTE, "Third Party Device"),
    /**Third party assigned ESP device #2 */
    THIRD_PARTY_2(Constants.THIRD_PARTY_2_BYTE, "Third Party Device 2"),
    /** Third party assigned ESP device #3 */
    THIRD_PARTY_3(Constants.THIRD_PARTY_3_BYTE, "Third Party Device 3"),
    /** V1connection ESP device */
    V1CONNECTION(Constants.V1CONNECTION_BYTE, "V1connection"),
    /** Reserved ESP device ID */
    RESERVED(Constants.RESERVED_BYTE, "Reserved"),
    /** General ESP device. Packets with this destination ID are suitable for anyone */
    GENERAL_BROADCAST(Constants.GENERAL_BROADCAST_BYTE, "General Broadcast"),
    /**Valentine One W/o Checksum */
    VALENTINE_ONE_NO_CHECKSUM(Constants.VALENTINE_ONE_NO_CHECKSUM_BYTE, "Valentine One w/o CS"),
    /**Valentine One W/ Checksum */
    VALENTINE_ONE(Constants.VALENTINE_ONE_BYTE, "Valentine One w/ CS"),
    /** Legacy Valentine One */
    VALENTINE_ONE_LEGACY(Constants.VALENTINE_ONE_LEGACY_BYTE,"Valentine One Legacy"),
    /**Unknown ESP Device */
    UNKNOWN_DEVICE(Constants.UNKNOWN_DEVICE_BYTE, "Unknown Device");

    public final byte value;

    public final String name;

    DeviceId(byte value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Get a {@link DeviceId} enum for the specified byte value.
     * @param value Byte value that maps to the enum
     *              (Review the Device ID's in the ESP Specification on valid
     *              band values)
     * @return Device Id equivalent to value
     */
    public static DeviceId get(byte value) {
        switch (value) {
            case 0x00: return CONCEALED_DISPLAY;
            case 0x01: return REMOTE_AUDIO;
            case 0x02: return SAVVY;
            case 0x03: return THIRD_PARTY_1;
            case 0x04: return THIRD_PARTY_2;
            case 0x05: return THIRD_PARTY_3;
            case 0x06: return V1CONNECTION;
            case 0x07: return RESERVED;
            case 0x08: return GENERAL_BROADCAST;
            case 0x09: return VALENTINE_ONE_NO_CHECKSUM;
            case 0x0A: return VALENTINE_ONE;
            case (byte) 0x98: return VALENTINE_ONE_LEGACY;
            default: return UNKNOWN_DEVICE;
        }
    }

    /**
     * Returns the byte value of the ESP Device.
     * @return Byte equivalent of the ESP device
     */
    public byte toByte() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }
}
