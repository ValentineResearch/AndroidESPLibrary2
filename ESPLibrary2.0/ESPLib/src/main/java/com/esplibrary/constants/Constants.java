package com.esplibrary.constants;

public class Constants {
    // This class should never be instantiated
    private Constants() {}

    /**Concealed Display (CD) byte value.*/
    public final static byte CONCEALED_DISPLAY_BYTE = (byte) 0x00;
    /**Tech Display byte value is the same as the Concealed Display.*/
    public final static byte TECH_DISPLAY_BYTE = CONCEALED_DISPLAY_BYTE;
    /**Remote Audio byte value.*/
    public final static byte REMOTE_AUDIO_BYTE = (byte) 0x01;
    /**SAVVY byte value.*/
    public final static byte SAVVY_BYTE = (byte) 0x02;
    /**Third Party Device #1 byte value.*/
    public final static byte THIRD_PARTY_1_BYTE = (byte) 0x03;
    /**Third Party Device #2 byte value.*/
    public final static byte THIRD_PARTY_2_BYTE = (byte) 0x04;
    /**Third Party Device #3 byte value.*/
    public final static byte THIRD_PARTY_3_BYTE = (byte) 0x05;
    /**V1connection byte value.*/
    public final static byte V1CONNECTION_BYTE = (byte) 0x06;
    /**Reserved ESP device byte value.*/
    public final static byte RESERVED_BYTE = (byte) 0x07;
    /**ESP bus General broadcast byte value.*/
    public final static byte GENERAL_BROADCAST_BYTE = (byte) 0x08;
    /**Valentine One W/o Checksum byte value.*/
    public final static byte VALENTINE_ONE_NO_CHECKSUM_BYTE = (byte) 0x09;
    /**Valentine One W/ Checksum byte value.*/
    public final static byte VALENTINE_ONE_BYTE = (byte) 0x0A;
    /**Legacy Valentine One byte value.*/
    public final static byte VALENTINE_ONE_LEGACY_BYTE = (byte) 0x98;
    /**Unknown ESP device byte value.*/
    public final static byte UNKNOWN_DEVICE_BYTE = (byte) 0x99;
}
