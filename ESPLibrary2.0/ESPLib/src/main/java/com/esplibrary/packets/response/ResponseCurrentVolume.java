package com.esplibrary.packets.response;

import com.esplibrary.packets.ESPPacket;

/**
 * Created by JDavis on 05/04/2021.
 */
public class ResponseCurrentVolume extends ESPPacket {

    /**
     * Number of volume settings contained in {@link #getCurrentVolume()}.
     */
    public static final int V1_VOLUME_SETTINGS_CNT = 2;
    /**
     * Index of the Main Volume setting in {@link #getCurrentVolume()}.
     */
    public static final int V1_MAIN_VOLUME_IDX = 0;
    /**
     * Index of the Muted Volume setting in {@link #getCurrentVolume()}.
     */
    public static final int V1_MUTED_VOLUME_IDX = 1;

    private byte [] mVolumeSettings;

    public ResponseCurrentVolume(int packetLength) {
        super(packetLength);
    }

    /**
     * A {@code byte} array containing the main volume (valid values are 0-9) at
     * index {@link #V1_MAIN_VOLUME_IDX} and the muted volume (valid values are 0-9) at index
     * {@link #V1_MUTED_VOLUME_IDX}.
     *
     * @return Byte array that contains the connected Valentine One's current volume settings.
     */
    public byte[] getCurrentVolume() {
        if (mVolumeSettings == null) {
            mVolumeSettings = getPayloadData();
        }
        return mVolumeSettings;
    }

    /**
     * The main volume of the connected Valentine One.
     * @return Valentine One's current main volume.
     */
    public byte getMainVolume() {
        return getCurrentVolume()[V1_MAIN_VOLUME_IDX];
    }


    /**
     * The muted volume of the connected Valentine One.
     * @return Valentine One's current muted volume.
     */
    public byte getMutedVolume() {
        return getCurrentVolume()[V1_MUTED_VOLUME_IDX];
    }
}
