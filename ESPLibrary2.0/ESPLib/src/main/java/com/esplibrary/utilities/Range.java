/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Created by JDavis on 3/14/2016.
 */
public class Range implements Parcelable, Comparable<Range> {

    public int low;

    public int high;

    /**
     * Creates a mutable range initialized to zero for both {@link #low} and {@link #high}
     */
    public Range() {
        low = 0;
        high = 0;
    }

    /**
     * Creates a mutable range initialized from r.
     *
     * @param r The range to initialize this instance with
     */
    public Range(Range r) {
        if(r != null) {
            this.low = r.low;
            this.high = r.high;
        }
    }

    /**
     * Creates a mutable range initialized with low and high.
     *
     * @param low The lower bound (inclusive)
     * @param high The upper bound (inclusive)
     */
    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }

    /**
     * Returns the width of this range i.e. the different between {@link #high} and {@link #low}
     * Value is always positive
     *
     * @return Width of this range
     */
    public int getWidth() {
        return Math.abs(high - low);
    }

    /**
     * Creates an identical copy of this instance
     *
     * @return Identical copy
     */
    public Range clone() {
        return new Range(low, high);
    }

    /**
     * Updates this instances {@link #low} and {@link #high} endpoints.
     *
     * @param low The lower endpoint (inclusive)
     * @param high The upper endpoint (inclusive)
     */
    public void set(int low, int high) {
        this.low = low;
        this.high = high;
    }

    /**
     * Updates this instance with r.
     *
     * @param r The range to update this instance with
     */
    public void set(Range r) {
        set(r.low, r.high);
    }

    /**
     * Updates this instances {@link #low lower} bound
     *
     * @param low The lower bound (inclusive)
     */
    public void setLow(int low) {
        this.low = low;
    }

    /**
     * Updates this instances {@link #high upper} bound
     *
     * @param high The upper bound (inclusive)
     */
    public void setHigh(int high) {
        this.high = high;
    }

    /**
     * Indicates if the the low and high edges are equal to '0'
     * @return True if the edges are equal to zero
     */
    public boolean isZero() {
        return low == 0 && high == 0;
    }

    /**
     * Indicates if the low and high bounds are equivalent to one another
     * @return True if the edges are equal to one another
     */
    public boolean areEdgesEqual() {
        return low == high;
    }

    /**
     * Indicates if the low edge is smaller than the high edges
     * @return True if the valid ({@link #low} {@code <} {@link #high})
     */
    public boolean isValid() {
        return low < high;
    }

    /**
     * Returns true if r is contained within this instances lower and upper bounds.
     *
     * @param r The range to check for containment
     *
     * @return True if r is contained within this instances bounds.
     */
    public boolean contains(Range r) {
        // Make sure the range to check inside this ranges low and high edges.
        return (low <= r.low && r.high <= high);
    }

    /**
     * Returns true if val is contained within this instances lower and upper bounds.
     *
     * @param val The value to check for containment
     *
     * @return True if val is contained with this instances bounds.
     */
    public boolean contains(int val) {
        return (low <= val && val <= high);
    }

    /**
     * Returns true if r intersects r.
     *
     * <p>Note: A Range wholly contained in another is considered an intersection.</p>
     *
     * @param r The range to check for intersections
     *
     * @return True if r intersects with this instance.
     */
    public boolean intersects(Range r) {
        // If r.low falls within our range, return true.
        return (contains(r.low) ||
                // If r.high falls within our range, return true.
                contains(r.high) ||
                // If r wholly contains this instance, return true.
                r.contains(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Range other = (Range) o;
        if(high == other.high && low == other.low) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[low = %d, high = %d]", low, high);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(low);
        dest.writeInt(high);
    }

    protected Range(Parcel in) {
        low = in.readInt();
        high = in.readInt();
    }

    public static final Creator<Range> CREATOR = new Creator<Range>() {
        @Override
        public Range createFromParcel(Parcel source) {
            return new Range(source);
        }

        @Override
        public Range[] newArray(int size) {
            return new Range[size];
        }
    };

    @Override
    public int compareTo(@NonNull Range r) {
        if(low < r.low) {
            return -1;
        }
        else if(low == r.low) {
            return 0;
        }
        return 1;
    }
}
