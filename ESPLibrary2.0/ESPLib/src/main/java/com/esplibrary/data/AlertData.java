/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.data;

import java.util.Arrays;

/**
 * Created by JDavis on 3/16/2016.
 *
 * AlertData represents alerts being displayed on the Valentine One front display, with additional data.
 * The additional data includes: Total number of alerts the Alert table that 'this' alert belongs, index
 * in the Alert table of 'this' alert, the band, frequency, and direction of 'this' alert, as well as
 * if it is the priority alert.
 */
public class AlertData {

    protected static final int ALERT_INDEX_COUNT_IDX = 0;
    protected static final int FREQUENCY_MSB_IDX = 1;
    protected static final int FREQUENCY_LSB_IDX = 2;
    protected static final int FRONT_SIGNAL_STRENGTH_IDX = 3;
    protected static final int REAR_SIGNAL_STRENGTH_IDX = 4;
    protected static final int BAND_ARROW_DEF_IDX = 5;
    protected static final int AUX_BYTE_IDX = 6;

    private byte [] mData;

    /**
     * Constructs an empty alert
     */
    public AlertData() {
        this((byte []) null);
    }

    /**
     * Construct a new alert containing the alert information in src.
     *
     * @param src Source alert to copy
     */
    public AlertData(AlertData src) {
        this(src.mData);
    }

    /**
     * Construct a new alert containing the alert information in src.
     *
     * @param src Source alert to copy
     */
    public AlertData(byte [] src) {
        this(src, 0, 7);
    }

    /**
     * Constructs a new alert containing the alert information in src.
     *
     * @param src Source array
     * @param srcPos starting position in the data array.
     * @param count the number of bytes to be copied.
     */
    public AlertData(byte [] src, int srcPos, int count) {
        mData = new byte[7];
        if (src != null) {
            System.arraycopy(src, srcPos, mData, 0, count);
        }
    }

    /**
     * Index in the alarm table this alert belongs to
     *
     * @return Index of this alert in the corresponding alarm table
     */
    public int getIndex() {
        byte mDatum = mData[ALERT_INDEX_COUNT_IDX];
        return (mDatum >> 4) & 0x0F;
    }

    /**
     * Number of alerts in the alarm table this alert belongs to
     *
     * @return Number of alerts in the alarm table corresponding to this alert
     */
    public int getCount() {
        byte mDatum = mData[ALERT_INDEX_COUNT_IDX];
        return mDatum & 0x0F;
    }

    /**
     * Return the alert's frequency
     *
     * @return Alert frequency
     */
    public int getFrequency() {
        byte msb = mData[FREQUENCY_MSB_IDX];
        byte lsb = mData[FREQUENCY_LSB_IDX];
        return ((msb & 0xff) << 8) | (lsb & 0x00FF);
    }

    /**
     * Returns the front signal strength of this alert.
     *
     * @return Front signal strength
     */
    public int getFrontSignalStrength() {
        return mData[FRONT_SIGNAL_STRENGTH_IDX] & 0xFF;
    }

    /**
     * Returns the rear signal strength of this alert.
     *
     * @return Rear signal strength
     */
    public int getRearSignalStrength() {
        return mData[REAR_SIGNAL_STRENGTH_IDX] & 0xFF;
    }

    /**
     * Returns the signal strength of this alert in the provided {@link Direction direction}.
     *
     * @param direction Direction of the desired signal strength
     *
     * @return The signal strength in direction
     */
    public int getSignalStrengthForDirection(Direction direction) {
        switch (direction) {
            case Front:
                return getFrontSignalStrength();
            case Side:
                // Return the greatest value.
                return Math.max(getFrontSignalStrength(), getRearSignalStrength());
            case Rear:
                return getRearSignalStrength();
            default:
                return 0;
        }
    }

    /**
     * The bargraph strength of this alert in the provided {@link Direction direction}.
     * The values in this method range from (0-8).
     *
     * @param direction Direction of the desired bargraph strength
     *
     * @return The bargraph strength in direction
     */
    public int getBargraphStrength(Direction direction) {
        switch (direction) {
            case Front:
                return getBargraphStrength(getBand(), getFrontSignalStrength());
            case Side:
                int maxStrength = Math.max(getFrontSignalStrength(), getRearSignalStrength());
                return getBargraphStrength(getBand(), maxStrength);
            case Rear:
                return getBargraphStrength(getBand(), getRearSignalStrength());
            default:
                return 0;
        }
    }

    /**
     * Returns the equivalent bargraph strength for the provider a signal in {@link AlertBand band}
     * at the provided signal strength.
     *
     * @param band {@link AlertBand} of an alert
     * @param strength Raw signal strength of an alert
     *
     * @see #getBargraphStrength(AlertBand, int) ()
     * @see #getFrontSignalStrength()
     * @see #getRearSignalStrength()
     * @see #getSignalStrengthForDirection(Direction)
     *
     * @return Bargraph value of an alert in band with strength
     */
    private static int getBargraphStrength(AlertBand band, int strength) {
        switch (band) {
            case Invalid:
            case Laser:
                return 8;
            case Ka:
                if(strength >= 0xBA) {
                    return 8;
                }
                else if(strength >= 0xB3) {
                    return 7;
                }
                else if(strength >= 0xAC) {
                    return 6;
                }
                else if(strength >= 0xA5) {
                    return 5;
                }
                else if(strength >= 0x9E) {
                    return 4;
                }
                else if(strength >= 0x97) {
                    return 3;
                }
                else if(strength >= 0x90) {
                    return 2;
                }
                else if(strength >= 0x01){
                    return 1;
                }
                break;
            case X:
                if(strength >= 0xD0) {
                    return 8;
                }
                else if(strength >= 0xC5) {
                    return 7;
                }
                else if(strength >= 0xbd) {
                    return 6;
                }
                else if(strength >= 0xB4) {
                    return 5;
                }
                else if(strength >= 0xAA) {
                    return 4;
                }
                else if(strength >= 0xA0) {
                    return 3;
                }
                else if(strength >= 0x96) {
                    return 2;
                }
                else if(strength >= 0x01) {
                    return 1;
                }
                break;
            case K:
            case Ku:
                if(strength >= 0xC2) {
                    return 8;
                }
                else if(strength >= 0xB8) {
                    return 7;
                }
                else if(strength >= 0xAE) {
                    return 6;
                }
                else if(strength >= 0xA4) {
                    return 5;
                }
                else if(strength >= 0x9A) {
                    return 4;
                }
                else if(strength >= 0x90) {
                    return 3;
                }
                else if(strength >= 0x88) {
                    return 2;
                }
                else if(strength >= 0x01){
                    return 1;
                }
                break;
        }
        return 0;
    }

    /**
     * Returns the {@link AlertBand Band} of the alert.
     *
     * @return Alert's {@link AlertBand band}
     */
    public AlertBand getBand() {
        byte band = mData[BAND_ARROW_DEF_IDX];
        return AlertBand.get(band & 0x1F);
    }

    /**
     * Indicates if alert is on the {@link AlertBand#Laser}
     *
     * @return True if the band of the alert is {@link AlertBand#Laser}
     */
    public boolean isLaser() {
        return getBand() == AlertBand.Laser;
    }

    /**
     * Indicates if alert is on the {@link AlertBand#Ka}
     *
     * @return True if the band of the alert is {@link AlertBand#Ka}
     */
    public boolean isKa() {
        return getBand() == AlertBand.Ka;
    }

    /**
     * Indicates if alert is on the {@link AlertBand#K}
     *
     * @return True if the band of the alert is {@link AlertBand#K}
     */
    public boolean isK() {
        return getBand() == AlertBand.K;
    }

    /**
     * Indicates if alert is on the {@link AlertBand#X}
     *
     * @return True if the band of the alert is {@link AlertBand#X}
     */
    public boolean isX() {
        return getBand() == AlertBand.X;
    }

    /**
     * Indicates if alert is on the {@link AlertBand#Ku}
     *
     * @return True if the band of the alert is {@link AlertBand#Ku}
     */
    public boolean isKu() {
        return getBand() == AlertBand.Ku;
    }

    /**
     * Indicates the direction of the alert.
     *
     * @return Alert direction
     */
    public Direction getDirection() {
        byte dir = mData[BAND_ARROW_DEF_IDX];
        return Direction.get((dir & 0xE0));
    }

    /**
     * Indicates if the alert is priority.
     *
     * Note: There should be only one alert valid for per alert table.
     *
     * @return True if the alert is priority.
     */
    public boolean isPriority() {
        byte aux0 = mData[AUX_BYTE_IDX];
        return (aux0 & 0x80) != 0;
    }

    /**
     * Indicates if the alert has been junked out.
     *
     *
     * @return True if the alert has been junked out.
     */
    public boolean isJunkAlert() {
        byte aux0 = mData[AUX_BYTE_IDX];
        return (aux0 & 0x40) != 0;
    }

    /**
     * Clears the alert data.
     */
    public void reset() {
        Arrays.fill(mData, (byte) 0x00);
    }

    /**
     * Copies the data inside alert.
     *
     * @param alert Alert to copy
     */
    public void copy(AlertData alert) {
        System.arraycopy(alert.mData, 0, mData, 0, mData.length);
    }

    /**
     * Indicates if there are not information on an alert being displayed on the Valentine One
     * front panel contained inside of this {@link AlertData instance}.
     * @return True if there is alert information contained in this {@link AlertData instance}.
     */
    public boolean isEmpty() {
        return mData[0] == 0x00;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Pri: ").append(isPriority());
        builder.append(" Freq= ").append(getFrequency());
        builder.append(" F= ").append(String.format("%02X", getFrontSignalStrength()));
        builder.append(" R= ").append(String.format("%02X", getRearSignalStrength()));
        builder.append(" Band: ").append(getBand().toString());
        builder.append(" Dir: ").append(getDirection().toString().substring(0, 1));
        return builder.toString();
    }
}
