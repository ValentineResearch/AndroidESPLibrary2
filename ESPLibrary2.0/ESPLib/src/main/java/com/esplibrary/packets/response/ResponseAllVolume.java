package com.esplibrary.packets.response;

import com.esplibrary.packets.ESPPacket;

public class ResponseAllVolume extends ESPPacket {
    /**
     * Index of the Main Volume setting in {@link #getAllVolume()}.
     */
    public static final int V1_MAIN_VOLUME_IDX = 0;
    /**
     * Index of the Muted Volume setting in {@link #getAllVolume()}.
     */
    public static final int V1_MUTED_VOLUME_IDX = 1;

    /**
     * Index of the Saved Main Volume setting in {@link #getAllVolume()}.
     */
    public static final int V1_SAVED_MAIN_VOLUME_IDX = 2;
    /**
     * Index of the Muted Volume setting in {@link #getAllVolume()}.
     */
    public static final int V1_SAVED_MUTED_VOLUME_IDX = 3;

    private byte [] mVolumeSettings;

    public ResponseAllVolume(int packetLength) {
        super(packetLength);
    }

    /**
     * A {@code byte} array containing the main volume (valid values are 0-9) at
     * index {@link #V1_MAIN_VOLUME_IDX} and the muted volume (valid values are 0-9) at index
     * {@link #V1_MUTED_VOLUME_IDX}.
     *
     * @return Byte array that contains the connected Valentine One's current volume settings.
     */
    public byte[] getAllVolume() {
        if (mVolumeSettings == null) {
            mVolumeSettings = getPayloadData();
        }
        return mVolumeSettings;
    }

    /**
     * The main volume of the connected Valentine One.
     * @return Valentine One's current main volume.
     */
    public byte getCurrentMainVolume() {
        return getAllVolume()[V1_MAIN_VOLUME_IDX];
    }
    /**
     * The muted volume of the connected Valentine One.
     * @return Valentine One's current muted volume.
     */
    public byte getCurrentMutedVolume() {
        return getAllVolume()[V1_MUTED_VOLUME_IDX];
    }

    /**
     * The main volume of the connected Valentine One.
     * @return Valentine One's saved main volume.
     */
    public byte getSavedMainVolume() {
        return getAllVolume()[V1_SAVED_MAIN_VOLUME_IDX];
    }
    /**
     * The muted volume of the connected Valentine One.
     * @return Valentine One's saved muted volume.
     */
    public byte getSavedMutedVolume() {
        return getAllVolume()[V1_SAVED_MUTED_VOLUME_IDX];
    }
}
