/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.data;

import com.esplibrary.utilities.Range;

/**
 * Represents a Valentine One sweep definitions.
 */
public class SweepDefinition {
    /**
     * Payload data index constants to extract the Sweep index.
     */
    private static final int SWEEP_DEF_INDEX_IDX = 0;
    /**
     * Payload data index constants to extract the upper frequency most significant byte.
     */
    private static final int MSB_UPPER_EDGE_IDX = 1;
    /**
     * Payload data index constants to extract the upper frequency least significant byte.
     */
    private static final int LSB_UPPER_EDGE_IDX = 2;
    /**
     * Payload data index constants to extract the lower frequency most significant byte.
     */
    private static final int MSB_LOWER_EDGE_IDX = 3;
    /**
     * Payload data index constants to extract the lower frequency edge least significant byte.
     */
    private static final int LSB_LOWER_EDGE_IDX = 4;
    /**
     * The value used to shift the bytes in order to construct a sweep edge.
     */
    public static final int SHIFT_CONST = 8;

    protected static final int SWEEP_PAYLOAD_SIZE = 5;

    /**
     * Holds the index of this Sweep Definition instance.
     */
    private int mIndex;
    /**
     * Indicates if this Sweep Definition will contain the commit changes bit.
     */
    private boolean mCommit;
    /**
     * Holds the value of the lower and upper frequency edge
     */
    public final Range range;

    public SweepDefinition() {
        this(0, false, 0, 0);
    }

    public SweepDefinition(SweepDefinition definition) {
        this(definition.getIndex(), definition.isCommit(), definition.getLowerEdge(), definition.getUpperEdge());
    }

    public SweepDefinition(int index, boolean commit, Range range) {
        this(index, commit, range.low, range.high);
    }

    public SweepDefinition(int index, boolean commit, int lower, int upper) {
        mIndex = index;
        mCommit = commit;
        range = new Range(lower, upper);
    }

    /**
     * Gets the index of this Sweep Definition.
     *
     * @return  Returns the index of this Sweep Definition.
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Sets the index of this Sweep Definition.
     *
     * @param index The new value of the index. This value must not exceed max sweep index, @see RequestMaxSweepIndex.
     */
    public void setIndex(int index) {
        this.mIndex = index;
    }

    /**
     * Checks if this Sweep Definition will contain the commit changes bit.
     *
     * @return  Returns true if the commit changes bit is set otherwise, returns false.
     */
    public boolean isCommit() {
        return mCommit;
    }

    /**
     * Sets the commit changes bit.
     *
     * @param commit    The new value of the commit changes bit.
     */
    public void setCommit(boolean commit) {
        this.mCommit = commit;
    }

    /**
     * Gets the upper Frequency edge of this Sweep Definition.
     *
     * @return  Returns the upper Frequency edge of this Sweep Definition
     */
    public int getUpperEdge() {
        return range.high;
    }

    /**
     * Sets the upper Frequency edge of this Sweep Definition.
     *
     * @param upperFrequencyEdge    The new lower frequency edge.
     */
    public void setUpperEdge(int upperFrequencyEdge) {
        range.high = upperFrequencyEdge;
    }

    /**
     * Gets the lower Frequency edge of this Sweep Definition.
     *
     * @return  Returns the lower Frequency edge of this Sweep Definition
     */
    public int getLowerEdge() {
        return range.low;
    }

    /**
     * Gets the lower Frequency edge of this Sweep Definition.
     *
     * @param lowerFrequencyEdge    The new lower frequency edge.
     */
    public void setLowerEdge(int lowerFrequencyEdge) {
        range.low = lowerFrequencyEdge;
    }

    /**
     * Indicates if this Sweep Definition is none empty;
     * i.e. the upper and lower frequency edges are both none zero.
     *
     * @return  Returns true if both the upper and lower frequency edges are none zero, otherwise false is returned.
     */
    public boolean isEmpty() {
        return range.isZero();
    }

    /**
     * Indicates if the Sweep Definition contains valid upper and lower frequency edges;
     * i.e the lower frequency ege is less than the upper frequency edge.
     *
     * @return  Returns true if the lower frequency edge is less than the upper frequency edge, otherwise false is returned.
     */
    public boolean isRangeValid() {
        return range.isValid();
    }

    /**
     * Populates this object using the byte array data.
     *
     * @param sweepDef  A byte array containing sweep definition data.
     */
    public boolean buildFromBytes(byte [] sweepDef, int offset) {
        mIndex = sweepDef[offset + SWEEP_DEF_INDEX_IDX] & 0x3f;
        // Check if this sweep definition is the commit sweep.
        if((sweepDef[offset + SWEEP_DEF_INDEX_IDX] & 0x40) != 0) {
            mCommit = true;
        }
        // Left shift the MSB upper edge byte by eight bits then OR it with the LSB upper edge
        // Bitwise AND the MSB bytes to convert the value into a positive int. This is necessary because the bytes are signed and when its shifted over 8 bits the value maybe negative.
        int upEdge = (((sweepDef[offset + MSB_UPPER_EDGE_IDX] & 0xff) << SHIFT_CONST) | (sweepDef[offset + LSB_UPPER_EDGE_IDX] &  0xff));
        // Left shift the MSB lower edge byte by eight bits then OR it with the LSB lower edge
        // Bitwise AND the MSB bytes to convert the value into a positive int. This is necessary because the bytes are signed and when its shifted over 8 bits the value maybe negative.
        int lowEdge = (((sweepDef[offset + MSB_LOWER_EDGE_IDX] & 0xff)  << SHIFT_CONST) | (sweepDef[offset + LSB_LOWER_EDGE_IDX] & 0xff));
        range.set(lowEdge, upEdge);
        return true;
    }

    /**
     * Serializes this object into a byte array that can be sent to a V1.
     *
     * @return  Returns a byte array that contains this SweepDefinitions data.
     */
    public byte[] buildBytes() {
        byte [] sweepDef = new byte[SWEEP_PAYLOAD_SIZE];
        // Normalize the sweep index.
        sweepDef[SWEEP_DEF_INDEX_IDX] = (byte) (mIndex & 0x3f);

        if(mCommit){
            // Or the index byte to contain the index.
            sweepDef[SWEEP_DEF_INDEX_IDX] |= 0x40;
        }
        // The V1 requires that the last bit to be set.
        sweepDef[SWEEP_DEF_INDEX_IDX] |= 0x80;

        // Zero right shift eight bits the upper frequency edge then bitwise and the value to extract the upper MSB.
        sweepDef[MSB_UPPER_EDGE_IDX] = (byte) (range.high >>> SHIFT_CONST & 0xFF);
        // Bitwise and the upper frequency edge to extract the upper LSB.
        sweepDef[LSB_UPPER_EDGE_IDX] = (byte) (range.high & 0xFF);

        // Zero right shift eight bits the lower frequency edge then bitwise and the value to extract the upper MSB.
        sweepDef[MSB_LOWER_EDGE_IDX] = (byte) (range.low >>> SHIFT_CONST & 0xFF);
        // Bitwise and the lower frequency edge to extract the upper LSB.
        sweepDef[LSB_LOWER_EDGE_IDX] = (byte) (range.low & 0xFF);

        return sweepDef;
    }

    @Override
    public String toString() {
        return "SweepDefinition {" +
                "Index = " + mIndex +
                ", Commit = " + mCommit +
                ", Upper Freq= " + range.high +
                ", Lower Freq= " + range.low +
                "\n}";
    }

    public SweepDefinition clone() {
        return new SweepDefinition(this);
    }
}
