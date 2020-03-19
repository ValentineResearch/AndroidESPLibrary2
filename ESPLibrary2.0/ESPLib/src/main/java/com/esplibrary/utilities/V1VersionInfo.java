/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.utilities;

import com.esplibrary.data.SweepSection;

import java.util.ArrayList;
import java.util.List;
import com.esplibrary.data.AlertBand;
import com.esplibrary.packets.response.ResponseDefaultSweepDefinition;

/**
 * Created by JDavis on 3/14/2016.
 */
public final class V1VersionInfo {
    // Use version V4.1007 as the default version for this app.
    public final static double V1_GEN_2_PLATFORM_BASELINE_VERSION = 4.1000d;
    // V1 version that first had support for K band support
    private final static double K_BAND_SWEEP_SUPPORT = V1_GEN_2_PLATFORM_BASELINE_VERSION;
    // The ability to read the default sweeps form the V1 was added in version V3.8950.
    private final static double READ_SWEEP_DEFAULTS_START_VER = 3.8950d;
    // The TMF & Junk-K Fighter feature was turned on by default in version 3.8945
    public final static double START_TMF_ON_BY_DEFAULT = 3.8945d;
    // V1 version that offered a single sweep section
    public static final double SINGLE_SWEEP_SECTION_VERSION = 3.8952d;

    private static V1VersionInfo mVersionInfo;

    /**
     * Return's the global instance of the V1 version utility.
     *
     * @return Singleton {@link V1VersionInfo} instance
     */
    public static synchronized V1VersionInfo getInstance() {
        if(mVersionInfo == null) {
            mVersionInfo = new V1VersionInfo();
        }
        return mVersionInfo;
    }

    private V1VersionInfo() {}

    /**
     * Returns the {@link SweepSection sweep sections} that best match the specified V1 version.
     *
     * @param version V1 version
     *
     * @return List of {@link SweepSection sweep sections} best matching the specified version
     */
    public static List<SweepSection> getSweepSections(double version) {
        List<SweepSection> sweepSections = new ArrayList<>();
        Range [] swpSections;

        if(version >= V1_GEN_2_PLATFORM_BASELINE_VERSION) {
            swpSections = V1FrequencyInfo.V4_1000_SWEEP_SECTIONS;
        }
        else if(version >= SINGLE_SWEEP_SECTION_VERSION) {
            swpSections = V1FrequencyInfo.V3_8952_SWEEP_SECTIONS;
        }
        else {
            // Use the V3.8920 defaults when not in demo mode
            swpSections = V1FrequencyInfo.V3_8920_SWEEP_SECTIONS;
        }
        // Add the Sweep sections into a list.
        for (int i = 0; i < swpSections.length; i++) {
            sweepSections.add(new SweepSection(swpSections[i].clone(), (i + 1), swpSections.length));
        }
        return sweepSections;
    }

    /**
     * Returns the {@link Range sweep sections} that best match the specified V1 version.
     *
     * @param version V1 version
     *
     * @return List of {@link Range sweep sections} best matching the specified version
     */
    public static List<Range> getSweepSectionsRange(double version) {
        Range [] swpSections;
        if(version >= V1_GEN_2_PLATFORM_BASELINE_VERSION) {
            swpSections = V1FrequencyInfo.V4_1000_SWEEP_SECTIONS;
        }
        else if(version >= SINGLE_SWEEP_SECTION_VERSION) {
            swpSections = V1FrequencyInfo.V3_8952_SWEEP_SECTIONS;
        }
        else {
            // Use the V3.8920 defaults when not in demo mode
            swpSections = V1FrequencyInfo.V3_8920_SWEEP_SECTIONS;
        }
        List<Range> sweepSections = new ArrayList<>();
        // Clone the custom sweeps then return them.
        for (Range swpSection : swpSections) {
            sweepSections.add(swpSection.clone());
        }
        return sweepSections;
    }

    /**
     * Returns the default {@link Range custom sweeps} that best match the specified V1 version.
     *
     * @param version V1 version
     *
     * @return List of default {@link Range sweeps} best matching the specified version
     */
    public static List<Range> getDefaultCustomSweepsForV1Version(double version, boolean euroMode) {
        List<Range> sweeps = new ArrayList<>();
        Range [] cs;
        if(version >= V1_GEN_2_PLATFORM_BASELINE_VERSION) {
            cs = euroMode ? V1FrequencyInfo.V4_1000_CUSTOM_FREQUENCIES_EURO : V1FrequencyInfo.V4_1000_CUSTOM_FREQUENCIES_USA;
        }
        else if(version >= SINGLE_SWEEP_SECTION_VERSION) {
            cs = V1FrequencyInfo.V3_8952_CUSTOM_SWEEPS;
        }
        else {
            cs = V1FrequencyInfo.V3_8920_CUSTOM_SWEEPS;
        }
        // Clone the custom sweeps then return them.
        for (Range c : cs) {
            sweeps.add(c.clone());
        }
        return sweeps;
    }

    /**
     * Returns the police/box range for the specified band.
     *
     * @param band	The band to retrieve the range for.
     *
     * @return A range containing the correct frequency for the supplied band.
     */
    public static Range getDefaultPoliceRange(V1Band band) {
        // Police ranges are the same for V1.8 and V1.9
        switch(band){
            case K:
                return new Range(V1FrequencyInfo.V3_8920_BAND_K_POLICE_LOWER_EDGE, V1FrequencyInfo.V3_8920_BAND_K_POLICE_UPPER_EDGE);
            case Ka_Hi:
                return new Range(V1FrequencyInfo.V3_8920_BAND_KA_HI_POLICE_LOWER_EDGE, V1FrequencyInfo.V3_8920_BAND_KA_HI_POLICE_UPPER_EDGE);
            case Ka_Mid:
                return new Range(V1FrequencyInfo.V3_8920_BAND_KA_MID_POLICE_LOWER_EDGE, V1FrequencyInfo.V3_8920_BAND_KA_MID_POLICE_UPPER_EDGE);
            case Ka_Lo:
                return new Range(V1FrequencyInfo.V3_8920_BAND_KA_LOW_POLICE_LOWER_EDGE, V1FrequencyInfo.V3_8920_BAND_KA_LOW_POLICE_UPPER_EDGE);
            case Ku:
                return new Range(V1FrequencyInfo.V3_8920_BAND_KU_POLICE_LOWER_EDGE, V1FrequencyInfo.V3_8920_BAND_KU_POLICE_UPPER_EDGE);
            case X:
                return new Range(V1FrequencyInfo.V3_8920_BAND_X_POLICE_LOWER_EDGE, V1FrequencyInfo.V3_8920_BAND_X_POLICE_UPPER_EDGE);
            case POP:
            case Ka:
            case No_Band:
            default:
                return new Range();
        }
    }

    /**
     * Returns the Max Sweep Index for specified V1 version.
     *
     * @param version V1 version (Unused, always returns 5)
     *
     * @return Max Sweep index of the V1 matching the version
     */
    public static int getDefaultMaxSweepIndexForV1Version(@SuppressWarnings("unused") double version) {
        // Use the index for the initial ESP release
        return V1FrequencyInfo.V3_8920_MAX_SWEEP_INDEX;
    }

    /**
     * Indicates if TMF (Traffic Monitor Filter) is enabled by default for the specified V1 version.
     *
     * @param version V1 version
     *
     * @return True if TMF is enabled by default
     */
    public static boolean isTMFDefaultOnForV1Version(double version) {
        return (version >= START_TMF_ON_BY_DEFAULT);
    }

    /**
     * Indicates if {@link AlertBand#K} band sweeps are available for the
     * specified V1 version.
     *
     * @param version V1 version
     *
     * @return True if K band sweeps are available
     */
    @SuppressWarnings("unused")
    public static boolean areKBandSweepAvailable(double version) {
        return (version >= K_BAND_SWEEP_SUPPORT);
    }

    /**
     * Indicates if default
     * {@link ResponseDefaultSweepDefinition sweep definitions} are
     * available for the specified V1 version.
     *
     * @param version V1 version
     *
     * @return True if default sweep definitions are available
     */
    public static boolean areDefaultSweepDefsAvailable(double version) {
        return (version >= READ_SWEEP_DEFAULTS_START_VER);
    }
}
