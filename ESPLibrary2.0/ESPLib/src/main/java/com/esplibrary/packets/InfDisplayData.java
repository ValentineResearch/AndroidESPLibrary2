/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets;

import androidx.annotation.IntRange;

import com.esplibrary.constants.V1Mode;

/**
 * Created by JDavis on 3/13/2016.
 *
 * InfDisplayData packet represents the all of the necessary information needed to rebuild the Valentine One's
 * front display.
 * The data includes: the bogey counter 7 segment image 1 & image 2, the signal strength bar graph image, band
 * and arrow indicator image 1 & 2, as well as three aux bytes.
 */
public class InfDisplayData extends ESPPacket {
    /*Constants for accessing the BOGEY COUNTER IMAGE 1 byte inside of the payload array.*/
    private static final int BOGEY_COUNTER_IMAGE_IDX = PacketUtils.PAYLOAD_START_IDX + 0;
    /*Constants for accessing the BOGEY COUNTER IMAGE 2 byte inside of the payload array.*/
    private static final int BOGEY_COUNTER_IMAGE2_IDX = PacketUtils.PAYLOAD_START_IDX + 1;
    /*Constants for accessing the BARGRAPH SIGNAL STRENGTH byte inside of the payload array.*/
    private static final int BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX = PacketUtils.PAYLOAD_START_IDX + 2;
    /*Constants for accessing the BAND ARROW IMAGE 1 byte inside of the payload array.*/
    private static final int BAND_ARROW_IND_IMAGE_IDX = PacketUtils.PAYLOAD_START_IDX + 3;
    /*Constants for accessing the BAND ARROW IMAGE 2 byte inside of the payload array.*/
    private static final int BAND_ARROW_IND_IMAGE2_IDX = PacketUtils.PAYLOAD_START_IDX + 4;
    /*Constants for accessing the AUX 0 byte inside of the payload array.*/
    private static final int AUX_0_IDX = PacketUtils.PAYLOAD_START_IDX + 5;
    /*Constants for accessing the AUX 1 byte inside of the payload array.*/
    private static final int AUX_1_IDX = PacketUtils.PAYLOAD_START_IDX + 6;
    /*Constants for accessing the AUX 2 byte inside of the payload array.*/
    private static final int AUX_2_IDX = PacketUtils.PAYLOAD_START_IDX + 7;

    private static final int CAPITAL_C = 0x39;
    private static final int CAPITAL_U = 0x3E;
    private static final int LOWER_CASE_U = 0x1C;
    private static final int LOWER_CASE_C = 0x58;
    private static final int CAPITAL_L = 0x38;
    private static final int LOWER_CASE_L = 0x18;
    private static final int CAPITAL_A = 0x77;

    /*Bit Mask for accessing the Mute Indicator in both Band and Arrow Image 1 & Image 2*/
    private static final int MUTE_INDICATOR_MASK = 0x10;
    /*Bit Mask for accessing the Bluetooth indicator image 1*/
    private static final int BLUETOOTH_INDICATOR_IMG_1_MASK = 0x40;
    /*Bit Mask for accessing the Bluetooth indicator image 2*/
    private static final int BLUETOOTH_INDICATOR_IMG_2_MASK = 0x80;

    /**
     * Invalid V1 mode
     */
    public final static byte V1MODE_UNKNOWN = 0x00;
    /**
     * 'All Bogey Mode' if V1 in USA mode. 'U' mode or 'C' Mode if V1 in Euro Mode.
     *
     * <ul> Usa Operation (Default):
     *      <li>All Bogeys Mode
     * </ul>
     *
     * <ul> European Operation:
     *      <li>'U' Mode - K and Ka(Photo)
     *      <li>'C' Mode - K and Custom Sweeps
     * </ul>
     */
    public final static byte V1MODE_ALL_BOGEY_OR_K_AND_KA = 0x01;
    /**
     *
     * 'Logic' Mode if V1 in USA mode. 'u' mode or 'c' Mode if V1 in Euro Mode.
     *
     * <ul> Usa Operation (Default):
     *      *      <li>Logic Mode
     *      * </ul>
     *      *
     *      * <ul> European Operation:
     *      *      <li>'u' Mode - K and Ka(Photo)
     *      *      <li>'c' Mode - K and Custom Sweeps
     *      * </ul>
     *
     */
    public final static byte V1MODE_LOGIC_OR_KA = 0x02;
    /**
     * 'Advanced Logic' Mode. Invalid in Euro Mode.
     */
    public final static byte V1MODE_ADVANCED_LOGIC = 0x03;

    protected byte[] packetData = getPacketData();

    /**
     * Returns the Bogey Counter image 1 byte.
     *
     * @return  Returns a byte value that represents the Bogey Counter image 1.
     */
    public byte getBogeyCounterImage1() {
        return packetData[BOGEY_COUNTER_IMAGE_IDX];
    }

    /**
     * Returns the Bogey Counter image 2 byte.
     *
     * @return  Returns a byte value that represents the Bogey Counter image 2.
     */
    public byte getBogeyCounterImage2() {
        return packetData[BOGEY_COUNTER_IMAGE2_IDX];
    }

    /**
     * Returns a Signal Strength Bar Graph value byte value.
     *
     * @return  Returns a byte value that represents the Signal Strength Bar graph.
     */
    public byte getSignalStrengthImage() {
        return packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX];
    }

    /**
     * Returns a Band and Arrow Indicator image 1 byte.
     *
     * @return  Returns a byte value that represents the Band and Arrow Indicator image 1.
     */
    public byte getBandArrowIndicatorImage1() {
        return packetData[BAND_ARROW_IND_IMAGE_IDX];
    }

    /**
     * Returns a Band and Arrow Indicator image 2 byte.
     *
     * @return  Returns a byte value that represents the Band and Arrow Indicator image 2.
     */
    public byte getBandArrowIndicatorImage2() {
        return packetData[BAND_ARROW_IND_IMAGE2_IDX];
    }

    /**
     * Checks if the Dp bit is set in the Bogey Counter 7 Segment Image 1 byte.
     *
     * @return  Returns true if the Dp bit is set otherwise, returns false.
     */
    public boolean isDpImage1() {
        return ((packetData[BOGEY_COUNTER_IMAGE_IDX] & 0x80) != 0);
    }

    /**
     * Checks if the Dp bit is set in the Bogey Counter 7 Segment Image 2 byte.
     *
     * @return  Returns true if the Dp bit is set otherwise, returns false.
     */
    public boolean isDpImage2() {
        return ((packetData[BOGEY_COUNTER_IMAGE2_IDX] & 0x80) != 0);
    }

    /**
     * Checks if the Laser bit is set in the Band and Arrow Indicator Image 1. This methods value
     * is dependent on the system status bit being set.
     *
     * @return  Returns true if the laser bit and the system status bit is set otherwise, returns false.
     */
    public boolean isLaser() {
        return (isSystemStatus() && (packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x01) != 0);
    }

    /**
     * Checks if the Ka bit is set in the Band and Arrow Indicator Image 1. This methods value
     * is dependent on the system status bit being set.
     *
     * @return  Returns true if the Ka bit and the system status bit is set otherwise, returns false.
     */
    public boolean isKa() {
        return (isSystemStatus() && (packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x02) != 0);
    }

    /**
     * Checks if the K bit is set in the Band and Arrow Indicator Image 1. This methods value
     * is dependent on the system status bit being set.
     *
     * @return  Returns true if the K bit and the system status bit is set otherwise, returns false.
     */
    public boolean isK() {
        return (isSystemStatus() && (packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x04) != 0);
    }

    /**
     * Checks if the X bit is set in the Band and Arrow Indicator Image 1. This methods value
     * is dependent on the system status bit being set.
     *
     * @return  Returns true if the X bit and the system status bit is set otherwise, returns false.
     */
    public boolean isX() {
        return (isSystemStatus() && (packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x08) != 0);
    }

    /**
     * Checks if the Laser bit is set in the Band and Arrow Indicator Image 1.
     * Note: DOESN'T CHECK IF SYSTEM STATUS BIT IS SET. BEWARE!!!!
     *
     * @return  Returns true if the laser bit is set otherwise, returns false.
     */
    public boolean isLaserImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x01) != 0);
    }

    /**
     * Indicates if there are any alerts present on the V1's display.
     *
     * @return  Returns true if there are any alerts present on the V1's display otherwise, returns
     * false.
     * @see #hasActiveAlerts()
     */
    @Deprecated
    public boolean isAlertPresent() {
        return hasActiveAlerts();
    }

    /**
     * Gets if the Ka bit is set in the Band and Arrow Indicator Image 1.
     *
     * @return  Returns true if the ka b bit is set otherwise, returns false.
     */
    public boolean isKaImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x02) != 0);
    }

    /**
     * Checks if the K bit is set in the Band and Arrow Indicator Image 1.
     *
     * @return  Returns true if the K bit is set otherwise, returns false.
     */
    public boolean isKImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x04) != 0);
    }

    /**
     * Checks if the X bit is set in the Band and Arrow Indicator Image 1.
     *
     * @return  Returns true if the X bit is set otherwise, returns false.
     */
    public boolean isXImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x08) != 0);
    }

    /**
     * Checks if the 5 bit is set in the Band and Arrow Indicator Image 1.
     *
     * @return  Returns true if the 5 bit is set otherwise, returns false.
     */
    public boolean isReservedBitImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x10) != 0);
    }

    /**
     * Checks if the Front arrow bit is set in the Band and Arrow Indicator Image 1.
     *
     * @return  Returns true if the front arrow is set otherwise, returns false.
     */
    public boolean isFrontImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x20) != 0);
    }

    /**
     * Checks if the side arrow bit is set in the Band and Arrow Indicator Image 1.
     *
     * @return  Returns true if the side arrow bit is set otherwise, returns false.
     */
    public boolean isSideImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x40) != 0);
    }

    /**
     * Checks if the rear arrow bit is set in the Band and Arrow Indicator Image 1.
     *
     * @return  Returns true if the rear arrow bit is set otherwise, returns false.
     */
    public boolean isRearImage1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & 0x80) != 0);
    }

    /**
     * Checks if the Laser bit is set in the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the laser bit is set otherwise, returns false.
     */
    public boolean isLaserImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x01) != 0);
    }

    /**
     * Checks if the Ka bit is set in the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the Ka bit is set otherwise, returns false.
     */
    public boolean isKaImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x02) != 0);
    }

    /**
     * Checks if the K bit is set in the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the K bit is set otherwise, returns false.
     */
    public boolean isKImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x04) != 0);
    }

    /**
     * Checks if the X bit is set in the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the X bit is set otherwise, returns false.
     */
    public boolean isXImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x08) != 0);
    }

    /**
     * Checks if the 5 bit is set in the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the 5 bit is set otherwise, returns false.
     */
    public boolean isReservedBitImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x10) != 0);
    }

    /**
     * Checks if the front arrow bit is the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the front arrow bit is set otherwise, returns false.
     */
    public boolean isFrontImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x20) != 0);
    }

    /**
     * Checks if the side arrow bit is set in the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the side arrow bit is set otherwise, returns false.
     */
    public boolean isSideImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x40) != 0);
    }

    /**
     * Checks if the rear arrow bit is set in the Band and Arrow Indicator Image 2.
     *
     * @return  Returns true if the rear arrow bit is set otherwise, returns false.
     */
    public boolean isRearImage2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & 0x80) != 0);
    }
    /**
     * Checks if the Soft mute bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if the audio is muted, otherwise false is returned.
     */
    public boolean isSoft() {
        return ((packetData[AUX_0_IDX] & 0x01) != 0);
    }

    /**
     * Checks if the Time-Slicing Holdoff bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if time slicing is not allowed, otherwise false is returned.
     */
    public boolean isTSHoldOff() {
        return ((packetData[AUX_0_IDX] & 0x02) != 0);
    }

    /**
     * Checks if the System Status bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if the V1 is actively searching for Alerts, otherwise false is returned.
     */
    public boolean isSystemStatus() {
        return ((packetData[AUX_0_IDX] & 0x04) != 0);
    }

    /**
     * Checks if the Display on bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if the V1's display is turned on, otherwise false is returned.
     */
    public boolean isDisplayOn() {
        return ((packetData[AUX_0_IDX] & 0x08) != 0);
    }

    /**
     * Checks if the Euro bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if the V1 is operating in Euro mode, otherwise false is returned.
     */
    public boolean isEuroMode() {
        return ((packetData[AUX_0_IDX] & 0x10) != 0);
    }

    /**
     * Checks if the Custom Sweep bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if the V1 has custom sweeps defined, otherwise false is returned.
     */
    public boolean isCustomSweep() {
        return ((packetData[AUX_0_IDX] & 0x20) != 0);
    }

    /**
     * Checks if the Legacy bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if the V1 is operating in Legacy Mode, otherwise false is returned.
     */
    public boolean isLegacyMode() {
        return ((packetData[AUX_0_IDX] & 0x40) != 0);
    }

    /**
     * Checks if the reserved bit inside of Aux byte 1 is set.
     *
     * @return  Returns true if the reserved bit is set, otherwise false is returned.
     */
    public boolean isReservedAuxSet() {
        return ((packetData[AUX_0_IDX] & 0x80) != 0);
    }

    /**
     * Helper method for determining if the V1 is detecting any active alerts.
     *
     * @return  Returns true if there are any active alerts, otherwise false is returned.
     */
    public boolean hasActiveAlerts() {
        // If any of the three arrows are lit, this usually means that there is an active alert
        // present.
        return (isSystemStatus() &&
                (isFrontImage1() ||
                        isSideImage1() ||
                        isRearImage1()));
    }

    /**
     * Indicates if the display data represent an empty V1 display. (Not alerting)
     *
     * @return True if the display is clear
     */
    public boolean isDisplayClear() {
        return (!isFrontImage1() &&
                !isSideImage1() &&
                !isRearImage1() &&
                packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] == 0);
    }

    /**
     * Checks if the V1 is currently experiencing an error.
     *
     * @return  True if the V1 is displaying an error, otherwise false is returned.
     */
    public boolean isError() {
        // If the bogey counter indicator bytes are equal the 'E' value and the other display data bytes are zero
        // return true.
        return ((packetData[BOGEY_COUNTER_IMAGE_IDX] == PacketUtils.SEVEN_SEG_VALUE_E)
                && (packetData[BOGEY_COUNTER_IMAGE2_IDX] == PacketUtils.SEVEN_SEG_VALUE_E)
                && (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] == 0x00)
                && (packetData[BAND_ARROW_IND_IMAGE_IDX] == 0x00)
                && (packetData[BAND_ARROW_IND_IMAGE2_IDX] == 0x00));
    }

    /**
     * Checks if the V1 is currently experiencing a Junk out.
     *
     * @return  True if the V1 is junking out, otherwise false is returned.
     */
    public boolean isJunk() {
        // Make sure the system status bit is set
        if(isSystemStatus()) {
            byte bogeyImg = packetData[BOGEY_COUNTER_IMAGE_IDX];
            byte bogeyImg2 = packetData[BOGEY_COUNTER_IMAGE2_IDX];
            return bogeyImg == PacketUtils.SEVEN_SEG_VALUE_J &&
                    bogeyImg2 != PacketUtils.SEVEN_SEG_VALUE_J;

        }
        return false;
    }

    /**
     * Checks if the V1 is currently experiencing a sign-on.
     *
     * @return  True if the V1 is experiencing a sign-on, otherwise false is returned.
     */
    public boolean isSigningOn() {
        // Make sure all of the bits inside of the display data are active excluding reserved bits because their value is unpredictable.
        return !isSystemStatus() &&
                (getBogeyCounterImage1() == (byte) 0xFF) &&
                (getBogeyCounterImage2() == (byte) 0xFF) &&
                (getSignalStrengthImage() == (byte) 0xFF) &&
                ((getBandArrowIndicatorImage1() & 0xEF) == 0xEF) &&
                ((getBandArrowIndicatorImage2() & 0xEF) == 0xEF);
    }

    /**
     * Indicates if the Mute Indicator image 1 is set.
     *
     * @apiNote The result of this method is only meaningful on V1 versions V4.1018 and higher.
     *
     * @return True if set
     */
    public boolean isMuteIndicatorLitImg1() {
        return ((packetData[BAND_ARROW_IND_IMAGE_IDX] & MUTE_INDICATOR_MASK) != 0);
    }

    /**
     * Indicates if the Mute Indicator image 2 is set.
     *
     * @apiNote The result of this method is only meaningful on V1 versions V4.1018 and higher.
     *
     * @return true if set
     */
    public boolean isMuteIndicatorLitImg2() {
        return ((packetData[BAND_ARROW_IND_IMAGE2_IDX] & MUTE_INDICATOR_MASK) != 0);
    }

    /**
     * Indicates if the Bluetooth Indicator image 1 bit is set.
     *
     * @apiNote The result of this method is only meaningful on V1 versions V4.1018 and higher.
     *
     * @return True if set
     */
    public boolean isBluetoothIndicatorLitImg1() {
        return (getAuxData1() & BLUETOOTH_INDICATOR_IMG_1_MASK) != 0;
    }

    /**
     * Indicates if the Bluetooth Indicator image 2 bit is set.
     *
     * @apiNote The result of this method is only meaningful on V1 versions V4.1018 and higher.
     *
     * @return True if set
     */
    public boolean isBluetoothIndicatorLitImg2() {
        return (getAuxData1() & BLUETOOTH_INDICATOR_IMG_2_MASK) != 0;
    }

    /**
     * Gets the AuxiliaryData byte 0.
     *
     * @return  Auxiliary byte 0.
     */
    public byte getAuxData() {
        return packetData[AUX_0_IDX];
    }

    /**
     * Gets the AuxiliaryData byte 1.
     *
     * @return  Auxiliary byte 1.
     */
    public byte getAuxData1() {
        return packetData[AUX_1_IDX];
    }

    /**
     * Gets the AuxiliaryData byte 2.
     *
     * @return  Auxiliary byte 2.
     */
    public byte getAuxData2() {
        return packetData[AUX_2_IDX];
    }

    /**
     * Return the current mode of the V1.
     * @return Current V1 mode
     *
     * @see #V1MODE_UNKNOWN
     * @see #V1MODE_ALL_BOGEY_OR_K_AND_KA
     * @see #V1MODE_LOGIC_OR_KA
     * @see #V1MODE_ADVANCED_LOGIC
     */
    public int getModeInt() {
        return getMode().getValue();
    }

    /**
     * Attempts to return the V1 mode stored in the display data Bogey Counter data. If the V1 is
     * alerting mode detection isn't possible because the Bogey Counter bytes indicate the number
     * of detected alerts.
     *
     * @return V1 Mode; {@link V1Mode#Unknown} if mode cannot be determined.
     */
    public V1Mode getMode() {
        final int bogeyCounterNoDp = packetData[BOGEY_COUNTER_IMAGE_IDX] & 0x7F;
        final int minusDp = (bogeyCounterNoDp & 0x7F);
        switch (minusDp) {
            case CAPITAL_A: // A, All Bogeys Mode
            case CAPITAL_C: // C, K and Custom Sweeps
            case CAPITAL_U: // U, Euro Mode, Ka and Ka (Photo)
                return V1Mode.AllBogeysKKa;
            case LOWER_CASE_L: // Little L, Logic Mode
            case LOWER_CASE_U: // u, Euro Mode, Ka only
            case LOWER_CASE_C: // c, Custom Sweeps
                return V1Mode.LogicKa;
            case CAPITAL_L: // Big L, Advanced Logic Mode.
                return V1Mode.AdvancedLogic;
            default:
                return V1Mode.Unknown;
        }
    }

    /**
     * Returns whether or not the Bar Graph indicator at indicatorIndex is lit.
     *
     * @param indicatorIndex    The index of the Bar Graph indicator on the V1 is lit.
     *
     * @return      True if the indicator at indicatorIndex is lit otherwise, returns false.
     */
    public boolean getBargraphIndicator(@IntRange(from=1,to=8) int indicatorIndex) {
        if(packetData == null) {
            return false;
        }
        switch (indicatorIndex) {
            case 1:
                // The first bit (val = 1) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x01) != 0;
            case 2:
                // The second bit (val = 2) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x02) != 0;
            case 3:
                // The third bit (val = 4) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x04) != 0;
            case 4:
                // The forth bit (val = 8) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x08) != 0;
            case 5:
                // The fifth bit (val = 16) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x10) != 0;
            case 6:
                // The sixth bit (val = 32) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x20) != 0;
            case 7:
                // The seventh bit (val = 64) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x40) != 0;
            default:
                // The eighth bit (val = 128) is is set return true;
                return (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX] & 0x80) != 0;
        }
    }

    /**
     * Returns the number of LEDS lit on the V1 display.
     *
     * @return  The number of lit LEDs on the V1 BarGraph display.
     */
    public int getNumberOfLEDS() {
        if(packetData == null) {
            return 0;
        }
        switch (packetData[BAR_GRAPH_SIGNAL_STRENGTH_IMAGE_IDX]) {
            case 0x01:
                // If mBarGrphSignalStrength equals 1 then there is 1 light lit.
                return 1;
            case 0x03:
                // If mBarGrphSignalStrength equals 3 then there is 2 lights lit.
                return 2;
            case 0x07:
                // If mBarGrphSignalStrength equals 7 then there is 3 lights lit.
                return 3;
            case 0x0F:
                // If mBarGrphSignalStrength equals 15 then there is 4 lights lit.
                return 4;
            case 0x1F:
                // If mBarGrphSignalStrength equals 31 then there is 5 lights lit.
                return 5;
            case 0x3F:
                // If mBarGrphSignalStrength equals 63 then there is 6 lights lit.
                return 6;
            case 0x7F:
                // If mBarGrphSignalStrength equals 127 then there is 7 lights lit.
                return 7;
            default:
                return 8;
        }
    }
    /**
     *  Returns the String representation of the seven segment image 1.
     *
     * @return  A String representation that a Seven Segment is capable of displaying.
     */
    public String convertToStringImage1() {
        return convertByteToSevenSegmentString(packetData[BOGEY_COUNTER_IMAGE_IDX]);
    }
    /**
     *  Returns the String representation of the seven segment image 2.
     *
     * @return  A String representation that a Seven Segment is capable of displaying.
     */
    public String convertToStringImage2() {
        return convertByteToSevenSegmentString(packetData[BOGEY_COUNTER_IMAGE2_IDX]);
    }

    /**
     * Convenience method for converting a packed byte containing Seven Segment display segments into a String representation.
     *
     * @param bogeyCounterImage  A packed byte that represents a Seven Segment Display.
     *
     * @return              A String representation of a Seven Segment Display.
     */
    private static String convertByteToSevenSegmentString(byte bogeyCounterImage) {
        String retVal;
        // Check the seven segment image data, minus the last bit which is the decimal point.
        switch (bogeyCounterImage & 0x7F) {
            case PacketUtils.SEVEN_SEG_VALUE_0:
                retVal =  "0";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_1:
                retVal = "1";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_2:
                retVal = "2";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_3:
                retVal = "3";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_4:
                retVal = "4";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_5:
                retVal = "5";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_6:
                retVal = "6";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_7:
                retVal = "7";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_8:
                retVal = "8";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_9:
                retVal = "9";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_A:
                retVal = "A";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_b:
                retVal = "b";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_C:
                retVal = "C";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_d:
                retVal = "d";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_E:
                retVal = "E";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_F:
                retVal = "F";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_POUND:
                retVal = "#";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_AMP:
                retVal = "&";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_L:
                retVal = "L";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_J:
                retVal = "J";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_c:
                retVal = "c";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_U:
                retVal = "U";
                break;
            case PacketUtils.SEVEN_SEG_VALUE_u:
                retVal = "u";
                break;
            default :
                // If none of the other cases, default to a whitespace string.
                retVal = " ";
                break;
        }
        // check to see if the decimal point it active.
        if((bogeyCounterImage & 0x80) != -0) {
            retVal += ".";
        }
        return retVal;
    }

    public InfDisplayData(int packetLength) {
        super(packetLength);
    }

    /**
     * Creates an identical copy of this instance.
     *
     * @return Identical copy
     */
    public InfDisplayData clone() {
        InfDisplayData clone = new InfDisplayData(packetData.length);
        System.arraycopy(packetData, 0, clone.packetData, 0, packetData.length);
        return clone;
    }
}
