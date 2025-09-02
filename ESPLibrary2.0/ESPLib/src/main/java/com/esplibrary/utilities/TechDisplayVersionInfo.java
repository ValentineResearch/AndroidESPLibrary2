package com.esplibrary.utilities;

public class TechDisplayVersionInfo {
    // Tech Display version that first supported resting display
    public final static double RESTING_DISPLAY_START_VERSION = 1.0001d;
    // Tech Display version that first supported extended frequency display
    public final static double EXTENDED_FREQUENCY_DISPLAY_START_VERSION = 1.0001d;
    /**
     * Indicates if the specified Tech Display version supports the resting display feature.
     *
     * @param version Tech Display version
     *
     * @return True if feature is available
     */
    public static boolean isRestingDisplayAvailable (double version) {
        return (version >= TechDisplayVersionInfo.RESTING_DISPLAY_START_VERSION);
    }

    /**
     * Indicates if the specified Tech Display version supports the extended frequency display feature.
     *
     * @param version Tech Display version
     *
     * @return True if feature is available
     */
    public static boolean isExtendedFrequencyDisplayAvailable (double version) {
        return (version >= TechDisplayVersionInfo.EXTENDED_FREQUENCY_DISPLAY_START_VERSION);
    }

}
