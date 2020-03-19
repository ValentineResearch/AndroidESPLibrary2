package com.esplibrary.data;

/**
 * Savvy configuration data structure.
 */
public class SAVVYStatus {
    /**
     * Current speed threshold in KPH
     */
    int speedThreshold;
    /**
     * Indicates if the speed threshold is overridden by the user.
     */
    boolean userThresholdOverride;
    /**
     * Indicates if Savvy unmuting is enabled.
     */
    boolean unMuteEnabled;

    public SAVVYStatus(int speedThreshold, boolean userOverride, boolean unmuteEnabled) {
        this.speedThreshold = speedThreshold;
        this.userThresholdOverride = userOverride;
        this.unMuteEnabled = unmuteEnabled;
    }

    /**
     * Returns the current speed threshold in KPH.
     * @return Speed threshold
     */
    public int getSpeedThreshold() {
        return speedThreshold;
    }

    /**
     * Indicates if the speed threshold is overridden by the user.
     * @return  True if the speed threshold is user overridden.
     */
    public boolean isUserThresholdOverride() {
        return userThresholdOverride;
    }

    /**
     * Indicates if savvy unmuting is allowed.
     *
     * @return  True if the savvy is allowed to un-mute.
     */
    public boolean isUnmuteEnabled() {
        return unMuteEnabled;
    }
}
