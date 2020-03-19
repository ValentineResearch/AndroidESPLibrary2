package com.esplibrary.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.esplibrary.utilities.ByteUtils;
import com.esplibrary.utilities.V1VersionInfo;

public class V18UserSettings extends UserSettings {

    /**3-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_3_USERBYTE = (byte) 0x00;
    /**4-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_4_USERBYTE = (byte) 0x02;
    /**5-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_5_USERBYTE = (byte) 0x04;
    /**7-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_7_USERBYTE = (byte) 0x06;
    /**15-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_15_USERBYTE = (byte) 0x08;
    /**10-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_10_USERBYTE = (byte) 0x0E;
    /**30-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_30_USERBYTE = (byte) 0x0C;
    /**20-second initial K Muting byte value.*/
    public static final byte K_BAND_MUTE_20_USERBYTE = (byte) 0x0A;

    /**10-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_10 = "10";
    /**30-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_30 = "30";
    /**20-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_20 = "20";
    /**15-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_15 = "15";
    /**7-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_7 = "7";
    /**5-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_5 = "5";
    /**4-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_4 = "4";
    /**3-second initial K Muting string value.*/
    public static final String K_BAND_MUTING_TIME_3 = "3";

    // User byte 0 bit indices (zero-based)
    public static final int BARGRAPH_BIT_INDEX = 4;
    public static final int KA_GUARD_BIT_INDEX = 5;
    public static final int K_MUTING_BIT_INDEX = 6;
    public static final int MUTE_VOLUME_BIT_INDEX = 7;
    // User byte 1 bit indices (zero-based)
    public static final int POST_MUTE_BOGEY_LOCK_BIT_INDEX = 0;
    public static final int K_INITIAL_UNMUTE_BIT_INDEX = 4;
    public static final int K_PERSISTENT_UNMUTE_BIT_INDEX = 5;
    public static final int K_REAR_MUTE_BIT_INDEX = 6;
    public static final int KU_BAND_BIT_INDEX = 7;
    // User byte 2 bit indices (zero-based)
    public static final int POP_BIT_INDEX = 0;
    public static final int EURO_BIT_INDEX = 1;
    public static final int EURO_X_BIT_INDEX = 2;
    public static final int TMF_BIT_INDEX = 3;
    public static final int FORCE_LEGACY_BIT_INDEX = 4;

    /**
     * Constructs a new user V1.8 settings instance backed with the provided userBytes.
     *
     * @param userBytes Backing array containing user settings. A deep copy is made
     */
    public V18UserSettings(byte[] userBytes) {
        super(userBytes);
    }

    /**
     * Volume control states
     */
    public enum VolumeControl {
        /**
         * Volume controlled using V1 lever
         */
        Lever,
        /**
         * Volume controlled using V1 Knob
         */
        Knob,
        /**
         * Volume at zero
         */
        Zero
    }

    /**
     * Bargraph sensitivity states
     */
    public enum BargraphSensitivity {
        /**
         * Normal Bargraph sensitivity
         */
        Normal,
        /**
         * Responsive bargraph sensitivity
         */
        Responsive
    }

    /**
     * Indicates if the Bargraph feature is {@link BargraphSensitivity#Normal}.
     *
     * @return  True if Bargraph sensitivity is {@link BargraphSensitivity#Normal}.
     */
    public boolean isBargraphNormal() {
        return isSet(USER_BYTE_0, BARGRAPH_BIT_INDEX);
    }

    /**
     * Indicates if the Bargraph feature is {@link BargraphSensitivity#Responsive}.
     *
     * @return  True if Bargraph sensitivity is {@link BargraphSensitivity#Responsive}.
     */
    public boolean isBargraphResponsive() {
        return !isSet(USER_BYTE_0, BARGRAPH_BIT_INDEX);
    }

    /**
     * Set the bargraph sensitivity.
     *
     * @param normal True for normal signal-strength meter false for responsive signal-strength
     *               meter.
     */
    public void setBargraphSensitivity(boolean normal) {
        setBit(USER_BYTE_0, BARGRAPH_BIT_INDEX, normal);
    }

    /**
     * Returns the bargraph sensitivity for Ka band alerts.
     *
     * @return Bargraph sensitivity
     */
    public BargraphSensitivity getBargraphSensitivity() {
        return isBargraphNormal() ? BargraphSensitivity.Normal : BargraphSensitivity.Responsive;
    }

    /**
     * Set the bargraph sensitivity for Ka band alerts.
     *
     * @param sensitivity Bargraph sensitivity; possible values: {@link BargraphSensitivity#Normal},
     * {@link BargraphSensitivity#Responsive}
     *
     * @see BargraphSensitivity#Normal
     * @see BargraphSensitivity#Responsive
     */
    public void setBargraphSensitivity(BargraphSensitivity sensitivity) {
        boolean isSet = (sensitivity == BargraphSensitivity.Normal);
        setBargraphSensitivity(isSet);
    }

    /**
     * Indicates if the K false guard feature is enabled.
     *
     * @return true if enabled
     */
    public boolean isKaFalseGuardEnabled() {
        return isSet(USER_BYTE_0, KA_GUARD_BIT_INDEX);
    }

    /**
     * Enables the Ka false guard features.
     *
     * @param enabled True to enable
     */
    public void setKaFalseGuardEnabled(boolean enabled) {
        setBit(USER_BYTE_0, KA_GUARD_BIT_INDEX, enabled);
    }

    /**
     * Indicates if the K mutnig feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isKMutingEnabled() {
        return !isSet(USER_BYTE_0, K_MUTING_BIT_INDEX);
    }

    /**
     * Enables the K muting features.
     *
     * @param enabled True to enable
     */
    public void setKMutingEnabled(boolean enabled) {
        // K mute is enabled when the bit is cleared.
        setBit(USER_BYTE_0, K_MUTING_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if V1's control {@link VolumeControl#Knob} controls the post mute bogey lock tone
     * volume.
     *
     * @return True if V1's control {@link VolumeControl#Knob} is enabled
     */
    public boolean isPostMuteBogeyLockVolumeKnob() {
        return isSet(USER_BYTE_1, POST_MUTE_BOGEY_LOCK_BIT_INDEX);
    }

    /**
     * Indicates if V1's control {@link VolumeControl#Lever} controls the post mute bogey lock tone
     * volume.
     *
     * @return True if V1's control {@link VolumeControl#Lever} is enabled
     */
    public boolean isPostMuteBogeyLockVolumeLever() {
        return !isSet(USER_BYTE_1, POST_MUTE_BOGEY_LOCK_BIT_INDEX);
    }

    /**
     * Returns the volume control the post mute bogey lock tone is set to
     *
     * @return Volume control; valid values: {@link VolumeControl#Lever}, {@link VolumeControl#Knob}
     *
     * @see VolumeControl#Lever
     * @see VolumeControl#Knob
     */
    public VolumeControl getPostMuteBogeyLockVolumeControl() {
        return isPostMuteBogeyLockVolumeKnob() ? VolumeControl.Knob : VolumeControl.Lever;
    }

    /**
     * Set's the post mute bogey lock volume to the V1's control knob.
     *
     * @param knob True to set the post mute bogey lock tone volume to the V1's knob control level;
     *             false to set the volume level to the V1's control lever.
     */
    public void setPostMuteBogeyLock(boolean knob) {
        setBit(USER_BYTE_1, POST_MUTE_BOGEY_LOCK_BIT_INDEX, knob);
    }

    /**
     * Sets the the post mute bogey lock volume.
     *
     * @param control {@link VolumeControl#Knob} to set the post mute bogey lock tone volume to the
     *                                          V1's knob control level. {@link VolumeControl#Lever}
     *                                          to set the bogey lock tone volume to the V1's lever
     *                                          control level.
     */
    public void setPostMuteBogeyLock(VolumeControl control) {
        boolean isKnob = control == VolumeControl.Knob;
        setPostMuteBogeyLock(isKnob);
    }

    /**
     * Helper method for converting a String into a packed byte containing the d,C,b bits.
     *
     * @param muteTimer     A StringKMutingTimes constant.
     *
     * @return              Returns a packet bit containing the d,C,b bits.
     */
    public static byte convertKMuteStringToByte(String muteTimer) {
        byte retVal;
        switch (muteTimer) {
            case K_BAND_MUTING_TIME_30:
                retVal = K_BAND_MUTE_30_USERBYTE;
                break;
            case K_BAND_MUTING_TIME_20:
                retVal = K_BAND_MUTE_20_USERBYTE;
                break;
            case K_BAND_MUTING_TIME_15:
                retVal = K_BAND_MUTE_15_USERBYTE;
                break;
            case K_BAND_MUTING_TIME_7:
                retVal = K_BAND_MUTE_7_USERBYTE;
                break;
            case K_BAND_MUTING_TIME_5:
                retVal = K_BAND_MUTE_5_USERBYTE;
                break;
            case K_BAND_MUTING_TIME_4:
                retVal = K_BAND_MUTE_4_USERBYTE;
                break;
            case K_BAND_MUTING_TIME_3:
                retVal = K_BAND_MUTE_3_USERBYTE;
                break;
            default:
                retVal = K_BAND_MUTE_10_USERBYTE;
        }
        return (byte) (retVal & 0x0E);
    }

    /**
     * Helper method for converting a byte containing the d,C,b bits, to a String indicating the K mute timeout.
     *
     * @param userByte  A byte containing the d,C,b bits.
     *
     * @return          Returns a String containing the K mute timeout.
     */
    public static String convertByteToKMuteString(byte userByte) {
        // This method ignore bits for in the lower nibble...
        switch (userByte) {
            case K_BAND_MUTE_10_USERBYTE:
                return K_BAND_MUTING_TIME_10;
            case K_BAND_MUTE_30_USERBYTE:
                return K_BAND_MUTING_TIME_30;
            case K_BAND_MUTE_20_USERBYTE:
                return K_BAND_MUTING_TIME_20;
            case K_BAND_MUTE_15_USERBYTE:
                return K_BAND_MUTING_TIME_15;
            case K_BAND_MUTE_7_USERBYTE:
                return K_BAND_MUTING_TIME_7;
            case K_BAND_MUTE_5_USERBYTE:
                return K_BAND_MUTING_TIME_5;
            case K_BAND_MUTE_4_USERBYTE:
                return K_BAND_MUTING_TIME_4;
            case K_BAND_MUTE_3_USERBYTE:
            default:
                return K_BAND_MUTING_TIME_3;
        }
    }

    /**
     * Returns the initial K muting timer.
     *
     * @return K muting timer
     */
    public String getKMuteTimer() {
        byte kMuteTimer = (byte) (mUserBytes[USER_BYTE_1] & 0x0E);
        return convertByteToKMuteString(kMuteTimer);
    }

    /**
     * Set's the initial K Muting timer.
     *
     * @param timer K muting timer; valid values: {"30", "20", "20", "15", "10", "7", "5", "4" 3"}
     *              
     * @see V18UserSettings#K_BAND_MUTING_TIME_10
     * @see V18UserSettings#K_BAND_MUTING_TIME_30
     * @see V18UserSettings#K_BAND_MUTING_TIME_20
     * @see V18UserSettings#K_BAND_MUTING_TIME_15
     * @see V18UserSettings#K_BAND_MUTING_TIME_7
     * @see V18UserSettings#K_BAND_MUTING_TIME_5
     * @see V18UserSettings#K_BAND_MUTING_TIME_4
     * @see V18UserSettings#K_BAND_MUTING_TIME_3
     */
    public void setKMuteTimer(String timer) {
        byte kMuteTimer = convertKMuteStringToByte(timer);
        byte b = mUserBytes[USER_BYTE_1];
        // Clear the existing K mute timer bits
        b &= (0xF1);
        // Set the K timer bits
        b |= (kMuteTimer);
        mUserBytes[USER_BYTE_1] = b;
    }

    /**
     * Indicates if initial K unmute at 4 lights is enable.
     *
     * @return True if enabled
     */
    public boolean isKIntialUnmute4LightsEnabled() {
        return isSet(USER_BYTE_1, K_INITIAL_UNMUTE_BIT_INDEX);
    }

    /**
     * Enables the initial K unmute at 4 light feature.
     *
     * @param enabled True to enable
     */
    public void setKInitialUnmute4LightsEnabled(boolean enabled) {
        setBit(USER_BYTE_1, K_INITIAL_UNMUTE_BIT_INDEX, enabled);
    }

    /**
     * Indicates if persistent K unmute at 6 lights is enabled.
     *
     * @return True if enabled
     */
    public boolean isKPersistentUnmute6LightsEnabled() {
        return isSet(USER_BYTE_1, K_PERSISTENT_UNMUTE_BIT_INDEX);
    }

    /**
     * Enables the persistent K unmute at 6 lights feature.
     *
     * @param enabled True to enable
     */
    public void setKPersistentUnmute6LightsEnabled(boolean enabled) {
        setBit(USER_BYTE_1, K_PERSISTENT_UNMUTE_BIT_INDEX, enabled);
    }

    /**
     * Indicates if K-Rear muting functionality is enabled.
     *
     * @return True if enable
     */
    public boolean isKRearMuteEnabled() {
        // K Rear mute is enabled when the 7th bit is cleared.
        return !isSet(USER_BYTE_1, K_REAR_MUTE_BIT_INDEX);
    }

    /**
     * Enables K-Rear mute functionality.
     * @param enable True to enabled K Rear Muting
     */
    public void setKRearMuteEnabled(boolean enable) {
        // K Rear mute is enabled when the 7th bit is cleared.
        setBit(USER_BYTE_1, K_REAR_MUTE_BIT_INDEX, !enable);
    }

    @Override
    public boolean isKuBandEnabled() {
        // K Band is enabled when the 8th bit is cleared.
        return !isSet(USER_BYTE_1, KU_BAND_BIT_INDEX);
    }

    @Override
    public void setKuBandEnabled(boolean enabled) {
        // K Band is enabled when the 8th bit is cleared.
        setBit(USER_BYTE_1, KU_BAND_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if POP functionality is enabled.
     *
     * @return True if enabled
     */
    public boolean isPopEnabled() {
        return isSet(USER_BYTE_2, POP_BIT_INDEX);
    }

    /**
     * Enables POP functionality.
     *
     * @param enabled True to enable
     */
    public void setPopEnabled(boolean enabled) {
        setBit(USER_BYTE_2, POP_BIT_INDEX, enabled);
    }

    /**
     * Indicates if Euro mode is enable in the provided user bytes.
     *
     * @param bytes source byte to check
     *
     * @return True if enabled
     */
    public static boolean isEuroEnabled(byte [] bytes) {
        return !ByteUtils.isSet(bytes[USER_BYTE_2], EURO_BIT_INDEX);
    }

    @Override
    public boolean isEuroEnabled() {
        // Euro mode is enabled when the 2nd bit is cleared.
        return !isSet(USER_BYTE_2, EURO_BIT_INDEX);
    }

    @Override
    public void setEuroEnabled(boolean enabled) {
        // Euro mode is enabled when the 2nd bit is cleared.
        setBit(USER_BYTE_2, EURO_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if Euro X band is enabled.
     *
     * @return True if the enabled.
     */
    public boolean isEuroXBandEnabled() {
        // Euro X band is enabled when the 3rd bit is cleared.
        return !isSet(USER_BYTE_2, EURO_X_BIT_INDEX);
    }

    /**
     * Enables Euro X band.
     *
     * @param enabled True to enable
     */
    public void setEuroXBandEnabled(boolean enabled) {
        // Euro X band is enabled when the 3rd bit is cleared.
        setBit(USER_BYTE_2, EURO_X_BIT_INDEX, !enabled);
    }

    @Override
    public boolean isTMFEnabled() {
        // TMF is enabled when the 4th bit is cleared.
        return !isSet(USER_BYTE_2, TMF_BIT_INDEX);
    }

    @Override
    public void setTMFEnabled(boolean enabled) {
        // TMF is enabled when the 4th bit is cleared.
        setBit(USER_BYTE_2, TMF_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if legacy mode is being forced.
     *
     * @return True if legacy mode is forced
     */
    public boolean isForceLegacy() {
        // Force legacy is set when the 5th bit is cleared.
        return !isSet(USER_BYTE_2, FORCE_LEGACY_BIT_INDEX);
    }

    /**
     * Enables forcing legacy mode.
     *
     * @param enabled True to for legacy mode.
     *
     * <p><b>Note:</b> **Once enabled, and written to the V1, this setting cannot be changed using ESP as the
     * V1 will not respond to any commands on the ESP bus. The V1 must manually enter programming
     * mode through the front display to disable this setting.</p>
     *
     */
    public void setForceLegacyEnabled(boolean enabled) {
        // Force legacy is set when the 5th bit is cleared.
        setBit(USER_BYTE_2, FORCE_LEGACY_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Mute Volume control is {@link VolumeControl#Lever}.
     *
     * @return  Returns true if the Muting Control is {@link VolumeControl#Lever}.
     */
    public boolean isMuteVolumeControlLever() {
        return isSet(USER_BYTE_0, MUTE_VOLUME_BIT_INDEX);
    }


    @Override
    public boolean isMutingAtMuteVolume() {
        return isMuteVolumeControlLever();
    }


    @Override
    public void setMuteToMuteVolume(boolean enabled) {
        setMuteControl(enabled ? VolumeControl.Lever : VolumeControl.Zero);
    }

    /**
     * Returns the mute control.
     *
     * @return Mute control; possible values: {@link VolumeControl#Lever},
     * {@link VolumeControl#Zero}
     */
    public VolumeControl getMuteVolumeControl() {
        if (isSet(USER_BYTE_0, MUTE_VOLUME_BIT_INDEX)) {
            return VolumeControl.Lever;
        }
        return VolumeControl.Zero;
    }

    /**
     * Sets the mute control logic.
     *
     * @param lever True to set the mute control to lever; false set the mute control to zero.
     */
    public void setMuteVolumeControl(boolean lever) {
        setBit(USER_BYTE_0, MUTE_VOLUME_BIT_INDEX, lever);
    }

    /**
     * Sets the mute control logic.
     *
     * @param control Mute control; valid values: {@link VolumeControl#Lever}, {@link VolumeControl#Zero}
     *
     * @see VolumeControl#Lever
     * @see VolumeControl#Zero
     */
    public void setMuteControl(VolumeControl control) {
        setMuteVolumeControl((control == VolumeControl.Lever));
    }

    /**
     * Sets the user setting bytes to the defaults for the specified V1 version.
     *
     * @param bytes     V1 user settings bytes.
     * @param v1Version Version of the V1 that bytes belong
     */
    public static void defaultBytesForV1Version(byte [] bytes, double v1Version) {
        // set all bits
        reset(bytes);
        // NOTE: VERSION GROUPING DOESN'T
        if(V1VersionInfo.isTMFDefaultOnForV1Version(v1Version)) {
            // On the V1.8, TMF is default off, so we need to clear that bit
            ByteUtils.setBit(bytes, USER_BYTE_2, TMF_BIT_INDEX, false);
        }
    }

    protected V18UserSettings(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<UserSettings> CREATOR = new Parcelable.Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel source) {
            return new V18UserSettings(source);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new V18UserSettings[size];
        }
    };
}
