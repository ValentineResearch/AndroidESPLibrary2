package com.esplibrary.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.esplibrary.utilities.ByteUtils;

public class V19UserSettings extends UserSettings {

    // User byte 0 bit indices (zero-based)
    public static final int MUTE_TO_MUTEVOLUME_BIT_INDEX = 4;
    public static final int MEMO_LOUD_BIT_INDEX = 5;
    public static final int X_K_REAR_BIT_INDEX = 6;
    public static final int KU_BAND_BIT_INDEX = 7;
    // User byte 1 bit indices (zero-based)
    public static final int EURO_BIT_INDEX = 0;
    public static final int TMF_BIT_INDEX = 1;
    public static final int LASER_REAR_BIT_INDEX = 2;
    public static final int CUSTOM_FREQUENCIES_BIT_INDEX = 3;
    public static final int KA_ALWAYS_RADAR_PRIORITY_BIT_INDEX = 4;
    public static final int FAST_LASER_DETECT_BIT_INDEX = 5;
    public static final int KA_SENSITIVITY_B0 = 6;
    public static final int KA_SENSITIVITY_B1 = 7;
    // User byte 2 bit indices (zero-based)
    public static final int STARTUP_SEQUENCE_BIT_INDEX = 0;
    public static final int RESTING_DISPLAY_BIT_INDEX = 1;
    public static final int BSM_PLUS_BIT_INDEX = 2;
    public static final int AUTO_MUTE_B0 = 3;
    public static final int AUTO_MUTE_B1 = 4;
    public static final int K_SENSITIVITY_B0 = 5;
    public static final int K_SENSITIVITY_B1 = 6;
    public static final int MRCT_BIT_INDEX = 7;
    // User byte 3 bit indices (zero-based)
    public static final int X_SENSITIVITY_B0 = 0;
    public static final int X_SENSITIVITY_B1 = 1;
    public static final int DRIVESAFE_3D_BIT_INDEX = 2;
    public static final int DRIVESAFE_3DHD_BIT_INDEX = 3;
    public static final int REDFLEX_HALO_BIT_INDEX = 4;
    public static final int REDFLEX_NK7_BIT_INDEX = 5;
    public static final int EKIN_BIT_INDEX = 6;
    public static final int PHOTO_VERIFIER_BIT_INDEX = 7;
    /**
     * Ka Sensitivity Setting
     */
    public enum KaSensitivity {
        /**
         * Full Ka Sensitivity
         */
        Full,
        /**
         * Original Valentine One Gen2 Ka Sensitivity
         */
        Original,
        /**
         * Relaxed Ka Sensitivity
         */
        Relaxed
    }
    /**
     * Auto Mute Setting
     */
    public enum AutoMute {
        /**
         * Auto Mute Off
         */
        Off,
        /**
         * Auto Mute On, no unmuting
         */
        On,
        /**
         * Auto Mute On, Unmute is allowed
         */
        Advanced
    }
    public enum KSensitivity{
        /**
         * Full K Sensitivity
         */
        Full,
        /**
         * Original Valentine One Gen2 K Sensitivity
         */
        Original,
        /**
         * Relaxed K Sensitivity
         */
        Relaxed
    }
    public enum XSensitivity{
        /**
         * Full X Sensitivity
         */
        Full,
        /**
         * Original Valentine One Gen2 X Sensitivity
         */
        Original,
        /**
         * Relaxed X Sensitivity
         */
        Relaxed
    }


    /**
     * Constructs a new user V1.9 settings instance backed with the provided userBytes.
     *
     * @param userBytes Backing array containing user settings. A deep copy is made
     */
    public V19UserSettings(byte[] userBytes) {
        super(userBytes);
    }

    @Override
    public boolean isMutingAtMuteVolume() {
        return isSet(USER_BYTE_0, MUTE_TO_MUTEVOLUME_BIT_INDEX);
    }

    @Override
    public void setMuteToMuteVolume(boolean useMuteVolume) {
        setBit(USER_BYTE_0, MUTE_TO_MUTEVOLUME_BIT_INDEX, useMuteVolume);
    }

    /**
     * Indicates if bogey lock tone is loud post mute.
     *
     * @return True if the bogey lock tone is load.
     */
    public boolean isBogeyLockToneLoudAfterMuting() {
        return isSet(USER_BYTE_0, MEMO_LOUD_BIT_INDEX);
    }

    /**
     * Enables the post mute bogey lock tone load setting.
     *
     * @param enabled True to enable
     */
    public void setBogeyLockToneLoudAfterMute(boolean enabled) {
        setBit(USER_BYTE_0, MEMO_LOUD_BIT_INDEX, enabled);
    }

    /**
     * Indicates if the X &amp; K Rear muting functionality is enabled.
     *
     * @return True if enable
     */
    public boolean isMuteXAndKRearEnabled() {
        // Mute X and K rear is enabled when the 7th bit is cleared.
        return !isSet(USER_BYTE_0, X_K_REAR_BIT_INDEX);
    }

    /**
     * Enables the K &amp; K Rear muting functionality.
     *
     * @param enabled True to enable X &amp; K Rear muting.
     */
    public void setMuteXAndKRearEnabled(boolean enabled) {
        // Mute X and K rear is enabled when the 7th bit is cleared.
        setBit(USER_BYTE_0, X_K_REAR_BIT_INDEX, !enabled);
    }

    @Override
    public boolean isKuBandEnabled() {
        // Ku band is enabled when the 8th bit is cleared.
        return !isSet(USER_BYTE_0, KU_BAND_BIT_INDEX);
    }

    @Override
    public void setKuBandEnabled(boolean enabled) {
        // Ku band is enabled when the 8th bit is cleared.
        setBit(USER_BYTE_0, KU_BAND_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if Euro mode is enable in the provided user bytes.
     *
     * @param bytes source byte to check
     *
     * @return True if enabled
     */
    public static boolean isEuroEnabled(byte [] bytes) {
        return !ByteUtils.isSet(bytes[USER_BYTE_1], EURO_BIT_INDEX);
    }

    @Override
    public boolean isEuroEnabled() {
        // Euro mode is enabled when the 1st bit is cleared.
        return isEuroEnabled(mUserBytes);
    }

    @Override
    public void setEuroEnabled(boolean enabled) {
        // Euro mode is enabled when the 1st bit is cleared.
        setBit(USER_BYTE_1, EURO_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if K-Verifier feature is enable.
     *
     * @return True if enable
     */
    @Override
    public boolean isTMFEnabled() {
        return isSet(USER_BYTE_1, TMF_BIT_INDEX);
    }

    /**
     * Enables the K-Verifier feature.
     *
     * @param enabled True to enable
     */
    @Override
    public void setTMFEnabled(boolean enabled) {
        setBit(USER_BYTE_1, TMF_BIT_INDEX, enabled);
    }

    /**
     * Indicates if K-Verifier feature is enable.
     *
     * @return True if enable
     */
    public boolean isKVerifierEnabled() {
        return isTMFEnabled();
    }

    /**
     * Enables the K-Verifier feature.
     *
     * @param enabled True to enable
     */
    public void setKVerifierEnabled(boolean enabled) {
        this.setTMFEnabled(enabled);
    }

    /**
     * Indicates if rear Laser is enabled.
     *
     * @return True if enabled
     */
    public boolean isLaserRearEnabled() {
        return isSet(USER_BYTE_1, LASER_REAR_BIT_INDEX);
    }

    /**
     * Enables rear Laser setting.
     *
     * @param enabled True to enable
     */
    public void setLaserRearEnabled(boolean enabled) {
        setBit(USER_BYTE_1, LASER_REAR_BIT_INDEX, enabled);
    }

    /**
     * Indicates if the Custom Frequencies feature is enabled.
     *
     * @return True if enable
     */
    public boolean areCustomFrequenciesEnabled() {
        return !isSet(USER_BYTE_1, CUSTOM_FREQUENCIES_BIT_INDEX);
    }

    /**
     * Enables the Custom Frequencies feature.
     *
     * @param enabled True to enable
     */
    public void setCustomFrequenciesEnabled(boolean enabled) {
        setBit(USER_BYTE_1, CUSTOM_FREQUENCIES_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Ka Always Radar Priority feature is enabled.
     *
     * @return True if enable
     */
    public boolean isKaAlwaysPriorityEnabled() {
        return !isSet(USER_BYTE_1, KA_ALWAYS_RADAR_PRIORITY_BIT_INDEX);
    }

    /**
     * Enables the Ka Always Radar Priority feature.
     *
     * @param enabled True to enable
     */
    public void setKaAlwaysRadarPriorityEnabled(boolean enabled) {
        setBit(USER_BYTE_1, KA_ALWAYS_RADAR_PRIORITY_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Fast Laser Detection feature is enabled.
     *
     * @return True if enable
     */
    public boolean isFastLaserDetectEnabled() {
        return isSet(USER_BYTE_1, FAST_LASER_DETECT_BIT_INDEX);
    }

    /**
     * Enables the Fast Laser Detection feature.
     *
     * @param enabled True to enable
     */
    public void setFastLaserDetectEnabled(boolean enabled) {
        setBit(USER_BYTE_1, FAST_LASER_DETECT_BIT_INDEX, enabled);
    }

    /**
     * Find Ka Sensitivity setting
     * @return Ka Sensitivity setting
     */
    public KaSensitivity getKaSensitivity() {
        byte bitVal = (byte)((mUserBytes[1] & 0xFF) >> 6);
        switch ( bitVal ){
            default:
            case 3: return KaSensitivity.Full;
            case 2: return KaSensitivity.Original;
            case 1: return KaSensitivity.Relaxed;
        }
    }

    /**
     * Set the Ka Sensitivity
     * @param kaSensitivity original, relaxed or full
     */
    public void setKaSensitivity(KaSensitivity kaSensitivity) {
        switch ( kaSensitivity ){
            default:
            case Full: // Gen2 value = 3
                setBit(USER_BYTE_1, KA_SENSITIVITY_B0, true);
                setBit(USER_BYTE_1, KA_SENSITIVITY_B1, true);
                break;
            case Original:  // Gen2 value = 2
                setBit(USER_BYTE_1, KA_SENSITIVITY_B0, false);
                setBit(USER_BYTE_1, KA_SENSITIVITY_B1, true);
                break;
            case Relaxed: // Gen2 value = 1
                setBit(USER_BYTE_1, KA_SENSITIVITY_B0, true);
                setBit(USER_BYTE_1, KA_SENSITIVITY_B1, false);
                break;
        }
    }

    /**
     * Find K Sensitivity setting
     * @return K Sensitivity setting
     */
    public KSensitivity getKSensitivity() {
        byte bitVal = (byte)((mUserBytes[2] & 0x7F) >> 5);
        switch ( bitVal ){
            default:
            case 3: return KSensitivity.Original;
            case 1: return KSensitivity.Relaxed;
            case 2: return KSensitivity.Full;
        }
    }

    /**
     * Set the K Sensitivity
     * @param kSensitivity original, relaxed or full
     */
    public void setKSensitivity(KSensitivity kSensitivity) {
        switch ( kSensitivity ){
            default:
            case Full: // Gen2 value = 2
                setBit(USER_BYTE_2, K_SENSITIVITY_B0, false);
                setBit(USER_BYTE_2, K_SENSITIVITY_B1, true);
                break;
            case Original:  // Gen2 value = 3
                setBit(USER_BYTE_2, K_SENSITIVITY_B0, true);
                setBit(USER_BYTE_2, K_SENSITIVITY_B1, true);
                break;
            case Relaxed: // Gen2 value = 1
                setBit(USER_BYTE_2, K_SENSITIVITY_B0, true);
                setBit(USER_BYTE_2, K_SENSITIVITY_B1, false);
                break;
        }
    }

    /**
     * Indicates if the MRCT feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isMRCTEnabled() {
        return !isSet(USER_BYTE_2, MRCT_BIT_INDEX);
    }

    /**
     * Enables the MRCT feature.
     *
     * @param enabled True to enable
     */
    public void setMRCTEnabled(boolean enabled) {
        setBit(USER_BYTE_2, MRCT_BIT_INDEX, !enabled);
    }

    /**
     * Find X Sensitivity setting
     * @return X Sensitivity setting
     */
    public XSensitivity getXSensitivity() {
        byte bitVal = (byte)(mUserBytes[3] & 0x03);
        switch ( bitVal ){
            default:
            case 3: return XSensitivity.Original;
            case 1: return XSensitivity.Relaxed;
            case 2: return XSensitivity.Full;
        }
    }

    /**
     * Set the XSensitivity
     * @param xSensitivity original, relaxed or full
     */
    public void setXSensitivity(XSensitivity xSensitivity) {
        switch ( xSensitivity ){
            default:
            case Full: // Gen2 value = 2
                setBit(USER_BYTE_3, X_SENSITIVITY_B0, false);
                setBit(USER_BYTE_3, X_SENSITIVITY_B1, true);
                break;
            case Original:  // Gen2 value = 3
                setBit(USER_BYTE_3, X_SENSITIVITY_B0, true);
                setBit(USER_BYTE_3, X_SENSITIVITY_B1, true);
                break;
            case Relaxed: // Gen2 value = 1
                setBit(USER_BYTE_3, X_SENSITIVITY_B0, true);
                setBit(USER_BYTE_3, X_SENSITIVITY_B1, false);
                break;
        }
    }

    /**
     * Indicates if the DriveSafe 3D feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isDriveSafe3DEnabled() {
        return !isSet(USER_BYTE_3, DRIVESAFE_3D_BIT_INDEX);
    }

    /**
     * Enables the DriveSafe 3D feature.
     *
     * @param enabled True to enable
     */
    public void setDriveSafe3DEnabled(boolean enabled) {
        setBit(USER_BYTE_3, DRIVESAFE_3D_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the DriveSafe 3DHD feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isDriveSafe3DHDEnabled() {
        return !isSet(USER_BYTE_3, DRIVESAFE_3DHD_BIT_INDEX);
    }

    /**
     * Enables the DriveSafe 3DHD feature.
     *
     * @param enabled True to enable
     */
    public void setDriveSafe3DHDEnabled(boolean enabled) {
        setBit(USER_BYTE_3, DRIVESAFE_3DHD_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Redflex Halo feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isRedflexHaloEnabled() {
        return !isSet(USER_BYTE_3, REDFLEX_HALO_BIT_INDEX);
    }

    /**
     * Enables the Redflex Halo feature.
     *
     * @param enabled True to enable
     */
    public void setRedflexHaloEnabled(boolean enabled) {
        setBit(USER_BYTE_3, REDFLEX_HALO_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Redflex NK7 feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isRedflexNK7Enabled() {
        return !isSet(USER_BYTE_3, REDFLEX_NK7_BIT_INDEX);
    }

    /**
     * Enables the Redflex NK7 feature.
     *
     * @param enabled True to enable
     */
    public void setRedflexNK7Enabled(boolean enabled) {
        setBit(USER_BYTE_3, REDFLEX_NK7_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Ekin feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isEkinEnabled() {
        return !isSet(USER_BYTE_3, EKIN_BIT_INDEX);
    }

    /**
     * Enables the Ekin feature.
     *
     * @param enabled True to enable
     */
    public void setEkinEnabled(boolean enabled) {
        setBit(USER_BYTE_3, EKIN_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Photo Verifier feature is enabled.
     *
     * @return True if enabled
     */
    public boolean isPhotoVerifierEnabled() {
        return !isSet(USER_BYTE_3, PHOTO_VERIFIER_BIT_INDEX);
    }

    /**
     * Enables the Photo Verifier feature.
     *
     * @param enabled True to enable
     */
    public void setPhotoVerifierEnabled(boolean enabled) {
        setBit(USER_BYTE_3, PHOTO_VERIFIER_BIT_INDEX, !enabled);
    }

    /**
     * Indicates if the Startup Sequence feature is enabled.
     *
     * @return True if enable
     */
    public boolean isStartupSequenceEnabled() {
        return isSet(USER_BYTE_2, STARTUP_SEQUENCE_BIT_INDEX);
    }

    /**
     * Enables the Startup Sequence feature.
     *
     * @param enabled True to enable
     */
    public void setStartupSequenceEnabled(boolean enabled) {
        setBit(USER_BYTE_2, STARTUP_SEQUENCE_BIT_INDEX, enabled);
    }

    /**
     * Indicates if the Startup Sequence feature is enabled.
     *
     * @return True if enable
     */
    public boolean isRestingDisplayEnabled() {
        return isSet(USER_BYTE_2, RESTING_DISPLAY_BIT_INDEX);
    }

    /**
     * Enables the Startup Sequence feature.
     *
     * @param enabled True to enable
     */
    public void setRestingDisplayEnabled(boolean enabled) {
        setBit(USER_BYTE_2, RESTING_DISPLAY_BIT_INDEX, enabled);
    }

    /**
     * Indicates if the BSM PLUS feature is enabled.
     *
     * @return True if enable
     */
    public boolean isBSMPlusEnabled() {
        return !isSet(USER_BYTE_2, BSM_PLUS_BIT_INDEX);
    }

    /**
     * Enables the Startup Sequence feature.
     *
     * @param enabled True to enable
     */
    public void setBSMPlusEnabled(boolean enabled) {
        setBit(USER_BYTE_2, BSM_PLUS_BIT_INDEX, !enabled);
    }

    public AutoMute getAutoMute() {
        byte bitVal = (byte)((mUserBytes[2] & 0x18) >> 3);
        switch ( bitVal ){
            default:
            case 3: return AutoMute.Off;
            case 2: return AutoMute.On;
            case 1: return AutoMute.Advanced;
        }
    }

    public void setAutoMute(AutoMute autoMute) {
        switch ( autoMute ){
            default:
            case Off: // Gen2 value = 3
                setBit(USER_BYTE_2, AUTO_MUTE_B0, true);
                setBit(USER_BYTE_2, AUTO_MUTE_B1, true);
                break;
            case On:  // Gen2 value = 2
                setBit(USER_BYTE_2, AUTO_MUTE_B0, false);
                setBit(USER_BYTE_2, AUTO_MUTE_B1, true);
                break;
            case Advanced: // Gen2 value = 1
                setBit(USER_BYTE_2, AUTO_MUTE_B0, true);
                setBit(USER_BYTE_2, AUTO_MUTE_B1, false);
                break;
        }
    }

    /**
     * Sets the user setting bytes to their defaults according to the specified V1 version.
     *
     * @param bytes     V1 user settings bytes.
     * @param v1Version Version of the V1 that bytes belong
     */
    public static void defaultBytesForV1Version(byte [] bytes, double v1Version) {
        // set all bits
        reset(bytes);
    }

    protected V19UserSettings(Parcel in) {
        super(in);
    }

    public static final Parcelable.Creator<UserSettings> CREATOR = new Parcelable.Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel source) {
            return new V19UserSettings(source);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new V19UserSettings[size];
        }
    };
}
