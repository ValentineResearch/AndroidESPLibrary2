package com.esplibrary.data;

/**
 * Represents an alert band that an {@link AlertData} instance can belong to.
 */
public enum AlertBand {
    /**
     * X RF band
     */
    X((byte)0x08, "X"),
    /**
     * KU RF band
     */
    Ku((byte)0x10, "Ku"),
    /**
     * K RF band
     */
    K((byte)0x04, "K"),
    /**
     * Ka RF band
     */
    Ka((byte)0x02, "Ka"),
    /**
     * Currently not used
     */
    Laser((byte)0x01, "Laser"),
    /**
     * Photo band
     */
    Photo((byte)0xFE, "Photo"),
    /**
     * Band undetermined. This should be treated as an error state.
     */
    Invalid((byte)0xFF, "Invalid Band");

    private final byte value;

    private final String name;

    AlertBand(byte bVal, String name) {
        this.value = bVal;
        this.name = name;
    }

    /**
     * Byte value of the band
     *
     * @return Byte value
     */
    public byte toByte() {
        return value;
    }

    @Override
    public String toString() {
        return name;
    }

    public static int index(String name) {
        for (AlertBand band : AlertBand.values()) {
            if (band.toString().equals(name)) {
                return band.ordinal();
            }
        }
        throw new IllegalArgumentException("Unknown band name: " + name);
    }

    /**
     * Get a {@link AlertBand} enum for the specified byte value.
     * @param value Byte value that maps to the enum
     *              (Review the RespAlertData packet definition in the ESP Specification on valid
     *              band values)
     * @return Band equivalent to value
     */
    public static AlertBand get(int value) {
        switch (value) {
            case 0x01:
                return Laser;
            case 0x02:
                return Ka;
            case 0x04:
                return K;
            case 0x08:
                return X;
            case 0x10:
                return Ku;
            case 0xFE:
                return Photo;
            default:
                return Invalid;
        }
    }

    /**
     * Returns an alert band for the specified integer.
     *
     * @param val integer mapping to an {@link AlertBand}
     *
     * @return Alertband mapping to the integer
     */
    public static AlertBand fromInt(int val) {
        switch(val) {
            case 0:
                return X;
            case 1:
                return Ku;
            case 2:
                return K;
            case 3:
                return Ka;
            case 4:
                return Laser;
            case -2:
                return Photo;
            default:
                return Invalid;
        }
    }
}
