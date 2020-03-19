package com.esplibrary.utilities;

import androidx.annotation.IntRange;

public class ByteUtils {

    private ByteUtils() {}

    /**
     * Sets or clears the the bit at bitIndex in the byte at byteIndex.
     *
     * @param bytes     Array containing the byte to set/clear
     * @param byteIndex Index of the byte to set/clear
     * @param bitIndex  Index of the bit to set/clear
     * @param isSet     True to set the bit. False to clear the bit.
     */
    public final static void setBit(byte [] bytes, int byteIndex, @IntRange(from = 0, to = 7) int bitIndex, boolean isSet) {
        byte b = bytes[byteIndex];
        if(!isSet) {
            // Turn the given bit off
            b &= ~(1 << bitIndex);
        }
        else {
            b |= (1 << bitIndex);
        }
        bytes[byteIndex] = b;
    }


    /**
     * Set a specified bit in b.
     *
     * @param b     The byte to set bit in
     * @param bit   The index of the bit to set in b.
     * @param isSet Controls if bit should be set or cleared.
     */
    public final static byte setBit(byte b, @IntRange(from = 0, to = 7) int bit, boolean isSet) {
        // Clamp bit to an
        bit = Math.max(0, Math.min(bit, 7));
        if(!isSet) {
            // Turn the given bit off
            return (byte) (b  & ~(1 << bit));
        }
        else {
            return (byte) (b | (1 << bit));
        }
    }

    /**
     * Indicates if whichBit was set in b.
     *
     * @param b         The byte to check if whichBit is set.
     * @param whichBit  The bit to check if it's set.
     *
     * @return  True if whichBit is set in b.
     */
    public final static boolean isSet(byte b, @IntRange(from = 0, to = 7) int whichBit) {
        byte mask = (byte) (0x01 << whichBit);
        return (b & mask) != 0x00;
    }
}
