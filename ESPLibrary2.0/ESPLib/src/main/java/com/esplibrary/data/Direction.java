package com.esplibrary.data;

/**
 * Represents a direction an {@link AlertData} instance can have a signal.
 */
public enum Direction {
    /**
     * Front signal
     */
    Front ((byte) 0x20, "Front", 0),
    /**
     * Side signal
     */
    Side ((byte) 0x40, "Side", 1),
    /**
     * Rear signal
     */
    Rear((byte) 0x80, "Rear", 2),
    /**
     * Direction undetermined. This should be treated as an error state.
     */
    Invalid((byte) -1, "Invalid Direction", 4);

    private final byte value;

    private final String name;

    private final int index;

    Direction(byte bVal, String name, int index) {
        this.value = bVal;
        this.name = name;
        this.index = index;
    }

    /**
     * Returns the byte value of {@code this}
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

    /**
     * Get a {@link Direction} enum for the specified byte value.
     * @param value (Review the RespAlertData packet definition in the ESP Specification on valid
     *              direction values)
     *
     * @return Direction equivalent to value
     */
    public static Direction get(int value) {
        switch (value) {
            case 0x20:
                return Front;
            case 0x40:
                return Side;
            case 0x80:
                return Rear;
            default:
                return Invalid;
        }
    }
}
