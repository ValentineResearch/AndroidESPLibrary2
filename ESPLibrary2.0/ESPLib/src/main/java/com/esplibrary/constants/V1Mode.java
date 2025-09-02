package com.esplibrary.constants;

import androidx.annotation.NonNull;

public enum V1Mode {
    /**
     * USA: All Bogeys / Euro: K &amp; Ka
     */
    AllBogeysKKa(0x01, "All Bogeys/ K & Ka"),
    /**
     * USA: Logic / Euro: Ka
     */
    LogicKa(0x02, "Logic/ Ka"),
    /**
     * USA: Advanced Logic
     */
    AdvancedLogic(0x03, "Advanced Logic"),
    /**
     * Unknown Mode
     */
    Unknown(0x00, "Unknown");

    final int value;

    final String description;

    V1Mode(int modeValue, String description) {
        this.value = modeValue;
        this.description = description;
    }

    /**
     * Integer value of the mode.
     *
     * @return Integer value
     */
    public int getValue()  {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return description;
    }
}