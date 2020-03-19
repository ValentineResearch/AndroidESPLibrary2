/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.esplibrary.utilities.Range;

/**
 * Created by JDavis on 3/14/2016.
 */
public class SweepSection implements Parcelable {

    public final Range range;

    private int mIndexCount;

    /**
     * Constructs an empty sweep section
     */
    public SweepSection() {
        this(new Range(), 0, 0);
    }

    /**
     * Constructs a sweep section using the provided data.
     * @param r
     * @param index Index of this sweep section.
     * @param count Number of sections available.
     */
    public SweepSection(Range r, int index, int count) {
        range = r.clone();
        // Lob off the upper nibbles of both the index and count bytes.
        mIndexCount = ((index & 0x0F) << 4 | (count & 0x0F));
    }

    /**
     * Gets the index of this Sweep Section instance.
     *
     * @return  Returns the index of this Sweep Section.
     */
    public int getIndex() {
        return (mIndexCount & 0xF0) >> 4;
    }

    /**
     * Gets the number of available Sweep Sections.
     *
     * @return  Returns the number of Sweep Sections.
     */
    public int getCount() {
        return (mIndexCount & 0x0F);
    }

    /**
     * Returns the sweep sections upper edge.
     *
     * @return  Upper edge as a freqeuncy in Mhz.
     */
    public int getUpperEdge() {
        return range.high;
    }

    /**
     * Returns the sweep sections lower edge.
     *
     * @return  Lower edge as a freqeuncy in Mhz.
     */
    public int getLowerEdge() {
        return range.low;
    }

    /**
     * Indicates if the sections edges are non-zero.
     *
     * @return True if non-zero
     */
    public boolean isZero() {
        return range.isZero();
    }

    /**
     * Initializes this instance with the data from sweepSection byte [].
     *
     * @param sweepSection  Byte [] containing sweep section data
     * @param startIndex    Start index of the sweep range data
     */
    public void buildFromBytes(byte [] sweepSection, int startIndex) {
        mIndexCount = sweepSection[startIndex];
        // Bitwise AND the upper edge freq msb by 255 then shift the bytes over 8 bits before adding the lsb to create the SweepSection upper freq.
        // We have to AND by 255 because bitwise math is done only on integers and we need to convert the signed by to a positive int value.
        int upEdge = ((sweepSection[startIndex + 1] & 0xFF) << 8) + (sweepSection[startIndex + 2] & 0xFF);
        // Bitwise AND the lower edge freq by 255 then shift the bytes over bits before adding the lsb to create the SweepSection lower freq.
        int lowEdge = ((sweepSection[startIndex + 3] & 0xFF) << 8) + (sweepSection[startIndex + 4] & 0xFF);
        range.set(lowEdge, upEdge);
    }

    /**
     * Indicates if {@link Range r} is contained in this section.
     *
     * @param r The {@link Range r} to check.
     *
     * @return  True if contained
     */
    public boolean contains(Range r) {
       return range.contains(r);
    }

    /**
     * Indicates if frequency is contained in this section.
     *
     * @param freq The frequency to check.
     *
     * @return  True if contained
     */
    public boolean contains(int freq) {
        return range.contains(freq);
    }

    /**
     * Indicates if {@link Range r} intersects with this section.
     *
     * @param r The {@link Range} to check.
     *
     * @return True if intersects
     */
    public boolean intersects(Range r) {
        return range.intersects(r);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(16);
        builder.append(String.format("%02X", mIndexCount))
                .append("\t\t")
                .append(range.low)
                .append("\t\t")
                .append(range.high);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        SweepSection that = (SweepSection) o;

        if (!range.equals(that.range)) {return false;}
        return mIndexCount == that.mIndexCount;
    }

    @Override
    public int hashCode() {
        int result = range.high;
        result = 31 * result + range.low;
        result = 31 * result + mIndexCount;
        return result;
    }

    protected SweepSection(SweepSection self) {
        range = self.range.clone();
        mIndexCount = self.mIndexCount;
    }

    public SweepSection clone() {
        return new SweepSection(this);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.range, flags);
        dest.writeInt(this.mIndexCount);
    }

    protected SweepSection(Parcel in) {
        this.range = in.readParcelable(Range.class.getClassLoader());
        this.mIndexCount = in.readInt();
    }

    public static final Parcelable.Creator<SweepSection> CREATOR = new Parcelable.Creator<SweepSection>() {
        @Override
        public SweepSection createFromParcel(Parcel source) {
            return new SweepSection(source);
        }

        @Override
        public SweepSection[] newArray(int size) {
            return new SweepSection[size];
        }
    };
}
