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
import com.esplibrary.utilities.V1VersionInfo;

import java.util.Arrays;

/**
 * Created by JDavis on 3/13/2016.
 */
public abstract class UserSettings implements Parcelable {

    protected static final int USER_BYTE_0 = 0;
    protected static final int USER_BYTE_1 = USER_BYTE_0 + 1;
    protected static final int USER_BYTE_2 = USER_BYTE_1 + 1;
    protected static final int USER_BYTE_3 = USER_BYTE_2 + 1;
    protected static final int USER_BYTE_4 = USER_BYTE_3 + 1;
    protected static final int USER_BYTE_5 = USER_BYTE_4 + 1;

    public static final int X_BAND_BIT_INDEX = 0;
    public static final int K_BAND_BIT_INDEX = 1;
    public static final int KA_BAND_BIT_INDEX = 2;
    public static final int LASER_BIT_INDEX = 3;

    protected final byte [] mUserBytes;

    /**
     * Factory default User bytes for V1 version 4.1007 and above.
     */
    private final static byte [] DEFAULT_USER_BYTES = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    /**
     * Constructs a UserSettings instance using the provided V1 user bytes and
     *
     * @param userBytes         Array containing V1 user bytes
     */
    public UserSettings(byte [] userBytes) {
        mUserBytes = userBytes;
    }

    /**
     * Creates a typed UserSettings instances appropriate for interpreting userBytes.
     *
     * @param v1Version V1 version of the userBytes
     * @param userBytes User setting bytes
     *
     * @return Typed UserSettings; possible values {@link V18UserSettings}, {@link V19UserSettings}
     *
     * @see V18UserSettings
     * @see V19UserSettings
     */
    public static UserSettings getUserSettingsForV1Version(double v1Version, byte [] userBytes) {
        if(v1Version >= V1VersionInfo.V1_GEN_2_PLATFORM_BASELINE_VERSION) {
            return new V19UserSettings(userBytes);
        }
        else {
            return new V18UserSettings(userBytes);
        }
    }

    /**
     * Indicates if X band is enabled.
     *
     * @return True if X band is enabled.
     */
    public boolean isXBandEnabled() {
        return isSet(USER_BYTE_0, X_BAND_BIT_INDEX);
    }

    /**
     * Enables X band.
     *
     * @param enabled True to enable X band, false to disable X band.
     */
    public void setXBandEnabled(boolean enabled) {
        setBit(USER_BYTE_0, X_BAND_BIT_INDEX, enabled);
    }

    /**
     * Indicates if K band is enabled.
     *
     * @return True if K band is enabled.
     */
    public boolean isKBandEnabled() {
        return isSet(USER_BYTE_0, K_BAND_BIT_INDEX);
    }

    /**
     * Enables K band.
     *
     * @param enabled True to enable K band, false to disable K band.
     */
    public void setKBandEnabled(boolean enabled) {
        setBit(USER_BYTE_0, K_BAND_BIT_INDEX, enabled);
    }

    /**
     * Indicates if Ka band is enabled.
     *
     * @return True if Ka band is enabled.
     */
    public boolean isKaBandEnabled() {
        return isSet(USER_BYTE_0, KA_BAND_BIT_INDEX);
    }

    /**
     * Enables Ka band.
     *
     * @param enabled True to enable Ka band, false to disable Ka band.
     */
    public void setKaBandEnabled(boolean enabled) {
        setBit(USER_BYTE_0, KA_BAND_BIT_INDEX, enabled);
    }

    /**
     * Indicates if Laser alerting is enabled.
     *
     * @return True if Laser alerting is enabled.
     */
    public boolean isLaserEnabled() {
        return isSet(USER_BYTE_0, LASER_BIT_INDEX);
    }

    /**
     * Enable Laser alerting
     *
     * @param enabled True to enable Laser alerting, false to disable laser alerting.
     */
    public void setLaserEnabled(boolean enabled) {
        setBit(USER_BYTE_0, LASER_BIT_INDEX, enabled);
    }

    /**
     * Indicates if Ku band is enabled
     *
     * @return True if Ku band is enabled
     */
    public abstract boolean isKuBandEnabled();

    /**
     * Enables Ku band.
     *
     * @param enabled True to enable Ku band, false to disable Ku band.
     */
    public abstract void setKuBandEnabled(boolean enabled);

    /**
     * Indicates if Euro mode is enable.
     *
     * @return True if Euro mode is enabled.
     */
    public abstract boolean isEuroEnabled();

    /**
     * Enables Euro mode.
     *
     * @param enabled True to enable Euro mode.
     */
    public abstract void setEuroEnabled(boolean enabled);

    /**
     * Indicates if Traffic monitor filtering (TMF) is enabled.
     *
     * @return True if TMF is enabled.
     */
    public abstract boolean isTMFEnabled();

    /**
     * Enables Traffic monitor filtering (TMF).
     *
     * @param enabled True to enable TMF, false to disable TMF.
     */
    public abstract void setTMFEnabled(boolean enabled);

    /**
     * Indicates if muting is using the mute volume.
     *
     * @return  True if muting at the mute volume.
     */
    public abstract boolean isMutingAtMuteVolume();

    /**
     * Enables muting to the muted volume (zero).
     *
     * @param enabled True to enable muting at muted volume.
     */
    public abstract void setMuteToMuteVolume(boolean enabled);

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

    /**
     * Sets the user setting bytes to their defaults according to the specified V1 version.
     *
     * @param settings     V1 user settings bytes.
     * @param v1Version Version of the V1 that bytes belong
     */
    public static void defaultSettingsForV1Version(UserSettings settings, double v1Version) {
        defaultSettingsForV1Version(settings.mUserBytes, v1Version);
    }

    /**
     * Sets the user setting bytes to their defaults according to the specified V1 version.
     *
     * @param bytes     V1 user settings bytes.
     * @param v1Version Version of the V1 that bytes belong
     */
    public static void defaultSettingsForV1Version(byte [] bytes, double v1Version) {
        // If the version is greater than the V1 version initial group, we are on the V1 gen 2
        // platform
        if(v1Version >= V1VersionInfo.V1_GEN_2_PLATFORM_BASELINE_VERSION) {
            V19UserSettings.defaultBytesForV1Version(bytes, v1Version);
        }
        else {
            // Version groups don't apply to V18 setting so just pass in the V1 version
            V18UserSettings.defaultBytesForV1Version(bytes, v1Version);
        }
    }

    /**
     * Indicates if Euro mode is enable in the provided user bytes for the specified V1 version.
     *
     * @param userBytes User bytes
     * @param v1Version V1 version used to determine which bit to check.
     *
     * @return True if Euro mode is enabled
     */
    public static boolean isEuroMode(byte [] userBytes, double v1Version) {
        if(v1Version >= V1VersionInfo.V1_GEN_2_PLATFORM_BASELINE_VERSION) {
            return V19UserSettings.isEuroEnabled(userBytes);
        }
        return V18UserSettings.isEuroEnabled(userBytes);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if(o == this) { return true; }
        if(!(o instanceof UserSettings)) { return false; }
        // If the class doesn't match, consider the User settings unequal
        if(o.getClass() != getClass()) { return false; }

        UserSettings other = (UserSettings) o;
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

    protected UserSettings(Parcel in) {
        this.mUserBytes = in.createByteArray();
    }
}
