package com.esplibrary.utilities;

/**
 * V1 Frequency utility class
 */
public class V1FrequencyInfo {
    //prevent this class from being instantiated
    private V1FrequencyInfo() {}

    /**
     * Maximum number of sweeps supported by V1's running version V3.8920 and above.
     */
    public static final int V3_8920_MAX_SWEEP_INDEX = 5;

    /* Band frequencies for V1's running version V3.9820 */
    public static final int V3_8920_BAND_X_LO = 10477;
    public static final int V3_8920_BAND_X_HI = 10566;

    public static final int V3_8920_BAND_KU_LO = 13394;
    public static final int V3_8920_BAND_KU_HI = 13512;

    public static final int V3_8920_BAND_K_LO = 24036;
    public static final int V3_8920_BAND_K_HI = 24272;

    public static final int V3_8920_BAND_KA_LO_LO = 33400;
    public static final int V3_8920_BAND_KA_LO_HI = 34300;
    public static final int V3_8920_BAND_KA_MID_LO = 34301;
    public static final int V3_8920_BAND_KA_MID_HI = 35100;
    public static final int V3_8920_BAND_KA_HI_LO = 35101;
    public static final int V3_8920_BAND_KA_HI_HI = 36000;

    public static final int V3_8920_BAND_POP_LO = 33700;
    public static final int V3_8920_BAND_POP_HI = 33900;

    /* Band Police (box) ranges for V1's running version V3.9820 */
    public static final int V3_8920_BAND_X_POLICE_LOWER_EDGE = 10500;
    public static final int V3_8920_BAND_X_POLICE_UPPER_EDGE = 10550;
    public static final int V3_8920_BAND_KU_POLICE_LOWER_EDGE = 13400;
    public static final int V3_8920_BAND_KU_POLICE_UPPER_EDGE = 13500;
    public static final int V3_8920_BAND_K_POLICE_LOWER_EDGE = 24050;
    public static final int V3_8920_BAND_K_POLICE_UPPER_EDGE = 24250;
    public static final int V3_8920_BAND_KA_LOW_POLICE_LOWER_EDGE = 33700;
    public static final int V3_8920_BAND_KA_LOW_POLICE_UPPER_EDGE = 33900;
    public static final int V3_8920_BAND_KA_MID_POLICE_LOWER_EDGE = 34600;
    public static final int V3_8920_BAND_KA_MID_POLICE_UPPER_EDGE = 34800;
    public static final int V3_8920_BAND_KA_HI_POLICE_LOWER_EDGE = 35400;
    public static final int V3_8920_BAND_KA_HI_POLICE_UPPER_EDGE = 35600;

    /* Default sweep range frequencies for V1's running version V3.9820 */
    public static final int V3_8920_SWEEP_SECTION_1_LOWER_EDGE = 33383;
    public static final int V3_8920_SWEEP_SECTION_1_UPPER_EDGE = 34770;
    public static final int V3_8920_SWEEP_SECTION_2_LOWER_EDGE = 34774;
    public static final int V3_8920_SWEEP_SECTION_2_UPPER_EDGE = 36072;

    public static final int V3_8920_KA_SWEEP_0_LOWER_EDGE = 33900;
    public static final int V3_8920_KA_SWEEP_0_UPPER_EDGE = 34106;
    public static final int V3_8920_KA_SWEEP_1_LOWER_EDGE = 34180;
    public static final int V3_8920_KA_SWEEP_1_UPPER_EDGE = 34475;
    public static final int V3_8920_KA_SWEEP_2_LOWER_EDGE = 34563;
    public static final int V3_8920_KA_SWEEP_2_UPPER_EDGE = 34652;
    public static final int V3_8920_KA_SWEEP_3_LOWER_EDGE = 35467;
    public static final int V3_8920_KA_SWEEP_3_UPPER_EDGE = 35526;

    public static final int V3_8952_SWEEP_SECTION_0_LOWER_EDGE = 33360;
    public static final int V3_8952_SWEEP_SECTION_0_UPPER_EDGE = 36051;

    public static final int V3_8952_KA_SWEEP_0_LOWER_EDGE = 33905;
    public static final int V3_8952_KA_SWEEP_0_UPPER_EDGE = 34112;
    public static final int V3_8952_KA_SWEEP_1_LOWER_EDGE = 34186;
    public static final int V3_8952_KA_SWEEP_1_UPPER_EDGE = 34480;
    public static final int V3_8952_KA_SWEEP_2_LOWER_EDGE = 34569;
    public static final int V3_8952_KA_SWEEP_2_UPPER_EDGE = 34657;
    public static final int V3_8952_KA_SWEEP_3_LOWER_EDGE = 35462;
    public static final int V3_8952_KA_SWEEP_3_UPPER_EDGE = 35535;

    /* Band edges for the V1 gen 2 starting at version V4.1000 */
    public static final int V4_1000_X_LOWER_EDGE = 10500;
    public static final int V4_1000_X_UPPER_EDGE = 10550;
    public static final int V4_1000_KU_LOWER_EDGE = 13400;
    public static final int V4_1000_KU_UPPER_EDGE = 13500;
    public static final int V4_1000_K_LOWER_EDGE = 23900;
    public static final int V4_1000_K_UPPER_EDGE = 24250;

    /* Default Sweep Section edges for the V1 gen 2 starting at version V4.1000 */
    /**
     * V4.0000 and above Sweep Section index 9 (K Band) lower edge
     */
    public static final int V4_1000_K_SWEEP_SECTION_LOWER_EDGE = 23908;
    /**
     * V4.0000 and above Sweep Section index 1 (Ka band) lower edge
     */
    public static final int V4_1000_KA_SWEEP_SECTION_LOWER_EDGE = 33398;
    /**
     * V4.0000 and above Sweep Section index 0 (K Band) upper edge
     */
    public static final int V4_1000_K_SWEEP_SECTION_UPPER_EDGE = 24252;
    /**
     * V4.0000 and above Sweep Section index 1 (Ka band) upper edge
     */
    public static final int V4_1000_KA_SWEEP_SECTION_UPPER_EDGE = 36002;

    /* Default Euro Custom Frequency edges for the V1 gen 2 starting at version V4.1000 */
    /**
     * V4.0000 and above Euro Ka band Sweep index 0 lower edge
     */
    public static final int V4_1000_EURO_SWEEP_0_LOWER_EDGE = 33905;
    /**
     * V4.0000 and above Euro Ka band Sweep index 1 lower edge
     */
    public static final int V4_1000_EURO_SWEEP_1_LOWER_EDGE = 34186;
    /**
     * V4.0000 and above Euro Ka band Sweep index 2 lower edge
     */
    public static final int V4_1000_EURO_SWEEP_2_LOWER_EDGE = 34569;
    /**
     * V4.0000 and above Euro Ka band Sweep index 3 lower edge
     */
    public static final int V4_1000_EURO_SWEEP_3_LOWER_EDGE = 35462;
    /**
     * V4.0000 and above Euro K band Sweep index 4 lower edge
     */
    public static final int V4_1000_EURO_SWEEP_4_LOWER_EDGE = 23910;
    /**
     * V4.0000 and above Euro Ka band Sweep index 0 upper edge
     */
    public static final int V4_1000_EURO_SWEEP_0_UPPER_EDGE = 34112;
    /**
     * V4.0000 and above Euro Ka band Sweep index 1 upper edge
     */
    public static final int V4_1000_EURO_SWEEP_1_UPPER_EDGE = 34480;
    /**
     * V4.0000 and above Euro Ka band Sweep index 2 upper edge
     */
    public static final int V4_1000_EURO_SWEEP_2_UPPER_EDGE = 34657;
    /**
     * V4.0000 and above Euro Ka band Sweep index 3 upper edge
     */
    public static final int V4_1000_EURO_SWEEP_3_UPPER_EDGE = 35535;
    /**
     * V4.0000 and above Euro Ka band Sweep index 4 upper edge
     */
    public static final int V4_1000_EURO_SWEEP_4_UPPER_EDGE = 24250;

    /* Default USA Custom Frequency edges for the V1 gen 2 starting at version V4.1000 */
    /**
     * V4.0000 and above USA K band Sweep index 0 lower edge
     */
    public static final int V4_1000_DEF_USA_SWEEP_0_LOWER_EDGE = 33400;
    /**
     * V4.0000 and above USA K band Sweep index 1 lower edge
     */
    public static final int V4_1000_DEF_USA_SWEEP_1_LOWER_EDGE = 23910;
    /**
     * V4.0000 and above USA K band Sweep index 0 upper edge
     */
    public static final int V4_1000_DEF_USA_SWEEP_0_UPPER_EDGE = 36002;
    /**
     * V4.0000 and above USA K band Sweep index 1 upper edge
     */
    public static final int V4_1000_DEF_USA_SWEEP_1_UPPER_EDGE = 24250;

    /**
     * Array of Sweep {@link Range sections} for the Valentine One running firmware version
     * V4.0000 and above
     */
    public static final Range[] V4_1000_SWEEP_SECTIONS = {
            new Range(V4_1000_K_SWEEP_SECTION_LOWER_EDGE, V4_1000_K_SWEEP_SECTION_UPPER_EDGE),
            new Range(V4_1000_KA_SWEEP_SECTION_LOWER_EDGE, V4_1000_KA_SWEEP_SECTION_UPPER_EDGE)
    };

    /**
     * Array of Sweep {@link Range sections} for the Valentine One running firmware version
     * "V3.8952" and above
     */
    public static final Range[] V3_8952_SWEEP_SECTIONS = {
            new Range(V3_8952_SWEEP_SECTION_0_LOWER_EDGE, V3_8952_SWEEP_SECTION_0_UPPER_EDGE)
    };

    /**
     * Array of Sweep {@link Range sections} for the Valentine One running firmware version
     * "V3.8920" and above
     */
    public static final Range[] V3_8920_SWEEP_SECTIONS = {
            new Range(V3_8920_SWEEP_SECTION_1_LOWER_EDGE, V3_8920_SWEEP_SECTION_1_UPPER_EDGE),
            new Range(V3_8920_SWEEP_SECTION_2_LOWER_EDGE, V3_8920_SWEEP_SECTION_2_UPPER_EDGE)
    };

    /**
     * Array of default Custom USA mode {@link Range sweeps} for the Valentine One running firmware version
     * V4.000 and above
     */
    public static final Range[] V4_1000_CUSTOM_FREQUENCIES_USA = {
            new Range(V4_1000_DEF_USA_SWEEP_0_LOWER_EDGE, V4_1000_DEF_USA_SWEEP_0_UPPER_EDGE),
            new Range(V4_1000_DEF_USA_SWEEP_1_LOWER_EDGE, V4_1000_DEF_USA_SWEEP_1_UPPER_EDGE)
    };

    /**
     * Array of default Custom Euro mode {@link Range sweeps} for the Valentine One running firmware version
     * V4.000 and above
     */
    public static final Range[] V4_1000_CUSTOM_FREQUENCIES_EURO = {
            new Range(V4_1000_EURO_SWEEP_0_LOWER_EDGE, V4_1000_EURO_SWEEP_0_UPPER_EDGE),
            new Range(V4_1000_EURO_SWEEP_1_LOWER_EDGE, V4_1000_EURO_SWEEP_1_UPPER_EDGE),
            new Range(V4_1000_EURO_SWEEP_2_LOWER_EDGE, V4_1000_EURO_SWEEP_2_UPPER_EDGE),
            new Range(V4_1000_EURO_SWEEP_3_LOWER_EDGE, V4_1000_EURO_SWEEP_3_UPPER_EDGE),
            new Range(V4_1000_EURO_SWEEP_4_LOWER_EDGE, V4_1000_EURO_SWEEP_4_UPPER_EDGE)
    };

    /**
     * Array of default Custom {@link Range sweeps} for the Valentine One running firmware version
     * V3.8952 and above
     */
    public static final Range[] V3_8952_CUSTOM_SWEEPS = {
            new Range(V3_8952_KA_SWEEP_0_LOWER_EDGE, V3_8952_KA_SWEEP_0_UPPER_EDGE),
            new Range(V3_8952_KA_SWEEP_1_LOWER_EDGE, V3_8952_KA_SWEEP_1_UPPER_EDGE),
            new Range(V3_8952_KA_SWEEP_2_LOWER_EDGE, V3_8952_KA_SWEEP_2_UPPER_EDGE),
            new Range(V3_8952_KA_SWEEP_3_LOWER_EDGE, V3_8952_KA_SWEEP_3_UPPER_EDGE)
    };

    /**
     * Array of default Custom {@link Range sweeps} for the Valentine One running firmware version
     * V3.8920 and above
     */
    public static final Range[] V3_8920_CUSTOM_SWEEPS = {
            new Range(V3_8920_KA_SWEEP_0_LOWER_EDGE, V3_8920_KA_SWEEP_0_UPPER_EDGE),
            new Range(V3_8920_KA_SWEEP_1_LOWER_EDGE, V3_8920_KA_SWEEP_1_UPPER_EDGE),
            new Range(V3_8920_KA_SWEEP_2_LOWER_EDGE, V3_8920_KA_SWEEP_2_UPPER_EDGE),
            new Range(V3_8920_KA_SWEEP_3_LOWER_EDGE, V3_8920_KA_SWEEP_3_UPPER_EDGE)
    };
}
