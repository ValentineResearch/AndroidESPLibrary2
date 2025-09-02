/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import com.esplibrary.utilities.ByteUtils;

import java.util.Arrays;

/**
 * Created by JDavis on 3/13/2016.
 */
public class TechDisplayUserSettings implements Parcelable {

    protected static final int USER_BYTE_0 = 0;
    protected static final int USER_BYTE_1 = USER_BYTE_0 + 1;
    protected static final int USER_BYTE_2 = USER_BYTE_1 + 1;
    protected static final int USER_BYTE_3 = USER_BYTE_2 + 1;
    protected static final int USER_BYTE_4 = USER_BYTE_3 + 1;
    protected static final int USER_BYTE_5 = USER_BYTE_4 + 1;

    public static final int V1_DISPLAY_OFF_ON_BIT_INDEX = 0;      // 1 = V1 display is off
    public static final int TECH_DISPLAY_ON_OFF_BIT_INDEX = 1;    // 1 = Tech Display is on
    public static final int EXTENDED_RECALL_MODE_TIMEOUT_OFF_ON_BIT_INDEX = 2;      // 1 = Extended recall timeout ON
    public static final int RESTING_DISPLAY_BIT_INDEX = 3;
    public static final int EXTENDED_FREQUENCY_DISPLAY_BIT_INDEX = 4;       // 1 = Extended frequency display ON

    protected final byte [] mUserBytes;

    /**
     * Factory default User bytes for Tech Display T1.0000 and above.
     */
    private final static byte [] DEFAULT_USER_BYTES = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    /**
     * Constructs a UserSettings instance using the provided V1 user bytes and
     *
     * @param userBytes         Array containing V1 user bytes
     */
    public TechDisplayUserSettings(byte [] userBytes) {
        mUserBytes = userBytes;
    }

    /**
     * Indicates if the Tech Display is configured to allow the V1 display to be on.
     *
     * @return True if the Tech Display is configured to allow the V1 display to be on.
     */
    public boolean isV1DisplayOn() {
        return !isSet(USER_BYTE_0, V1_DISPLAY_OFF_ON_BIT_INDEX);
    }

    /**
     * Configure the Tech Display to allow the V1 display to be on or off.
     *
     * @param enabled True to allow the V1 display to be on, false to force the V1 display off.
     */
    public void setV1DisplayOn(boolean enabled) {
        setBit(USER_BYTE_0, V1_DISPLAY_OFF_ON_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Tech Display is configured to have its own display on.
     *
     * @return True if the Tech Display is configured to have its own display on.
     */
    public boolean isTechDisplayOn() {
        return isSet(USER_BYTE_0, TECH_DISPLAY_ON_OFF_BIT_INDEX);
    }

    /**
     * Configure the Tech Display to have its own display on or off.
     *
     * @param enabled True to allow the Tech Display display on, false to force the Tech Display off.
     */
    public void setTechDisplayOn(boolean enabled) {
        setBit(USER_BYTE_0, TECH_DISPLAY_ON_OFF_BIT_INDEX, enabled);
    }

    /**
     * Indicates if the extended Recall Mode timeout is enabled.
     *
     * @return True if the Tech Display is configured to allow the V1 display to be on.
     */
    public boolean isExtendedRecallModeTimeoutOn() {
        return !isSet(USER_BYTE_0, EXTENDED_RECALL_MODE_TIMEOUT_OFF_ON_BIT_INDEX);
    }

    /**
     * Configure the extended Recall Mode timeout to be on or off.
     *
     * @param enabled True to allow the V1 display to be on, false to force the V1 display off.
     */
    public void setExtendedRecallModeTimeoutOn(boolean enabled) {
        setBit(USER_BYTE_0, EXTENDED_RECALL_MODE_TIMEOUT_OFF_ON_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the resting display is disabled.
     *
     * @return True if the Tech Display is configured to disable the resting display.
     */
    public boolean isRestingDisplayEnabled() {
        return isSet(USER_BYTE_0, RESTING_DISPLAY_BIT_INDEX);
    }

    /**
     * Configure the resting display to be on or off.
     *
     * @param enabled True to turn off the resting display, false to keep it on
     */
    public void setRestingDisplayEnabled(boolean enabled) {
        setBit(USER_BYTE_0, RESTING_DISPLAY_BIT_INDEX, enabled);
    }

    /**
     * Indicates if the extended Frequency display is enabled.
     *
     * @return True if the Tech Display is configured to allow extended frequency display
     */
    public boolean isExtendedFrequencyDisplayOn() {
        return !isSet(USER_BYTE_0, EXTENDED_FREQUENCY_DISPLAY_BIT_INDEX);
    }

    /**
     * Configure the extended frequency display to be on or off.
     *
     * @param enabled True to allow extended frequency display, false to turn it off.
     */
    public void setExtendedFrequencyDisplay(boolean enabled) {
        setBit(USER_BYTE_0, EXTENDED_FREQUENCY_DISPLAY_BIT_INDEX, !enabled);
    }

    /**
     * Returns the array containing this instances user settings.
     *
     * @return Array containing user settings.
     */
    public byte [] getBytes() {
        byte [] copy = new byte[6];
        // Make a copy of the settings array so no one holds a references to our data.
        System.arraycopy(mUserBytes, 0, copy, 0, copy.length);
        return copy;
    }

    /**
     * Set the specified bit in whichByte.
     *
     * @param whichByte The index of the byte to set.
     * @param whichBit  The bit in whichByte to set.
     * @param isSet     Controls if the bit is set or cleared.
     */
    protected void setBit(@IntRange(from = 0, to = 5) int whichByte, @IntRange(from = 0, to = 7) int whichBit, boolean isSet) {
        ByteUtils.setBit(mUserBytes, whichByte, whichBit, isSet);
    }

    /**
     * Indicates if whichBit in whichByte is set.
     *
     * @param whichByte The index of the byte to check.
     * @param whichBit  The bit in whichByte to check.
     *
     * @return True if whichBit in whichByte is set.
     */
    protected boolean isSet(@IntRange(from = 0, to = 5) int whichByte, @IntRange(from = 0, to = 7) int whichBit) {
        return ByteUtils.isSet(mUserBytes[whichByte], whichBit);
    }

    /**
     * Resets all bytes to hex FF.
     */
    public void reset() {
        reset(mUserBytes);
    }

    /**
     * Resets all the bytes to hex FF.
     * @param bytes Array of bytes to set to hex FF.
     */
    public static void reset(byte [] bytes) {
        System.arraycopy(DEFAULT_USER_BYTES, 0, bytes, 0, bytes.length);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if(o == this) { return true; }
        if(!(o instanceof TechDisplayUserSettings)) { return false; }
        // If the class doesn't match, consider the User settings unequal
        if(o.getClass() != getClass()) { return false; }

        TechDisplayUserSettings other = (TechDisplayUserSettings) o;
        // Check that the user bytes match
        return Arrays.equals(this.mUserBytes, other.mUserBytes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.mUserBytes);
    }

    protected TechDisplayUserSettings(Parcel in) {
        this.mUserBytes = in.createByteArray();
    }

    public static final Parcelable.Creator<TechDisplayUserSettings> CREATOR = new Parcelable.Creator<TechDisplayUserSettings>() {
        @Override
        public TechDisplayUserSettings createFromParcel(Parcel source) { return new TechDisplayUserSettings(source); }

        @Override
        public TechDisplayUserSettings[] newArray(int size) { return new TechDisplayUserSettings[size]; }
    };


}
