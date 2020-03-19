package com.esplibrary.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.UUID;

public class BTUtil {

    private final static char[]     HEX_CHARS = "0123456789ABCDEF".toCharArray();
    public final static String     SPP_UUID_STR = "00001101-0000-1000-8000-00805f9b34fb";
    public final static String     V1CONNECTION_LE_SERVICE_UUID_STR = "92A0AFF4-9E05-11E2-AA59-F23C91AEC05E";
    public final static String     CLIENT_OUT_V1_IN_SHORT_CHAR_UUID_STR = "92A0B6D4-9E05-11E2-AA59-F23C91AEC05E";
    public final static String     CLIENT_OUT_V1_IN_LONG_CHAR_UUID_STR = "92A0B8D2-9E05-11E2-AA59-F23C91AEC05E";
    public final static String     V1_OUT_CLIENT_IN_SHORT_UUID_STR = "92A0B2CE-9E05-11E2-AA59-F23C91AEC05E";

    public final static String     V1_OUT_CLIENT_IN_LONG_UUID_STR = "92A0B4E0-9E05-11E2-AA59-F23C91AEC05E";
    public final static String     GAP_DEVICE_NAME_UUID_STR = "00002a00-0000-1000-8000-00805f9b34fb";
    public final static String     CLIENT_CHARACTERISTIC_CONFIG_STR = "00002902-0000-1000-8000-00805f9b34fb";


    public final static UUID        SPP_UUID = UUID.fromString(SPP_UUID_STR);
    public final static UUID        V1CONNECTION_LE_SERVICE_UUID = UUID.fromString(V1CONNECTION_LE_SERVICE_UUID_STR);
    public final static UUID        CLIENT_OUT_V1_IN_SHORT_CHARACTERISTIC_UUID = UUID.fromString(CLIENT_OUT_V1_IN_SHORT_CHAR_UUID_STR);
    public final static UUID        CLIENT_OUT_V1_IN_LONG_CHARACTERISTIC_UUID = UUID.fromString(CLIENT_OUT_V1_IN_LONG_CHAR_UUID_STR);
    public final static UUID        V1_OUT_CLIENT_IN_SHORT_CHARACTERISTIC_UUID = UUID.fromString(V1_OUT_CLIENT_IN_SHORT_UUID_STR);
    public final static UUID        V1_OUT_CLIENT_IN_LONG_CHARACTERISTIC_UUID = UUID.fromString(V1_OUT_CLIENT_IN_LONG_UUID_STR);
    public final static UUID        GAP_DEVICE_NAME_CHARACTERISTIC_UUID = UUID.fromString(GAP_DEVICE_NAME_UUID_STR);
    public final static UUID        CLIENT_CHARACTERISTIC_CONFIG_CHARACTERISTIC_UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG_STR);


    /**
     * Helper method for converting a space delimited hex string into a byte array.
     *
     * @param hexString Space delimited hex string.
     *
     * @return Byte array matching the hex data inside of hexString.
     */
    public static byte [] toByteArray(String hexString) {
        String[] hexPairs = hexString.split(" ");
        byte[] bytes = new byte[hexPairs.length];
        int i = 0;
        for (String hexPair : hexPairs) {
            bytes[i++] = (byte) Integer.parseInt(hexPair, 16);
        }
        return bytes;
    }

    /**
     * Helper method for converting a byte array into a byte string.
     * @param data  The byte array that will have it's content converted into a hex string.
     *
     * @return  A string containing the hex representation of the data byte array.
     */
    public static String toHexString(byte [] data) {
        if (data == null) {
            return "null";
        }
        else if(data.length == 0) {
            return "[]";
        }
        // The SB's initial capacity is equal to the bytes in packet data multiplied by 3, then
        // subtract one from the result (Two chars for every byte in packetData and one char
        // for spaces in between every byte except for the last).
        final int capacity = (3 *  data.length) - 1;
        char [] chars = new char[capacity];
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & 0xFF;
            chars[i * 3] = HEX_CHARS[v >>> 4];
            chars[(i * 3) + 1] = HEX_CHARS[v & 0x0F];
            if(i < data.length - 1)
                chars[(i * 3) + 2] = ' ';
        }
        return new String(chars).trim();
    }

    /**
     * Helper method for converting a byte array into a byte string.
     * @param data  The byte array that will have it's content converted into a hex string.
     *
     * @return  A string containing the hex representation of the data byte array.
     */
    public static String toHexString(byte [] data, int start,  int count) {
        // The SB's initial capacity is equal to the bytes in packet data multiplied by 3, then
        // subtract one from the result (Two chars for every byte in packetData and one char
        // for spaces in between every byte except for the last).
        final int capacity = (3 *  data.length) - 1;
        char [] chars = new char[capacity];
        for (int i = start; i < count; i++) {
            int v = data[i] & 0xFF;
            chars[i * 3] = HEX_CHARS[v >>> 4];
            chars[(i * 3) + 1] = HEX_CHARS[v & 0x0F];
            if(i < data.length - 1)
                chars[(i * 3) + 2] = ' ';
        }
        return new String(chars).trim();
    }

    /**
     * Utility method for converting Bluetooth connection states to a string.
     *
     * @param newState BT connection state
     *
     * @return String equivalent to the Bluetooth connection state.
     *
     * @see BluetoothProfile#STATE_CONNECTING
     * @see BluetoothProfile#STATE_CONNECTED
     * @see BluetoothProfile#STATE_DISCONNECTED
     * @see BluetoothProfile#STATE_DISCONNECTING
     */
    public final static String gattNewStateToString(int newState) {
        switch (newState) {
            case BluetoothProfile.STATE_CONNECTING:
                return "STATE_CONNECTING";
            case BluetoothProfile
                    .STATE_CONNECTED:
                return "STATE_CONNECTED";
            case BluetoothProfile
                    .STATE_DISCONNECTED:
                    return "STATE_DISCONNECTED";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "STATE_DISCONNECTING";
            default:
                return "UNKNOWN_STATE: " + newState;
        }
    }

    /**
     * Utility method for converting Bluetooth GATT status to a string.
     *
     * @param status Gatt Status
     *
     * @return Equivalent string of status
     */
    public final static String gattOperationToString(int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS: return "GATT_SUCCESS";
            case BluetoothGatt.GATT_READ_NOT_PERMITTED: return "GATT_READ_NOT_PERMITTED";
            case BluetoothGatt.GATT_WRITE_NOT_PERMITTED: return "GATT_WRITE_NOT_PERMITTED";
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION: return "GATT_INSUFFICIENT_AUTHENTICATION";
            case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED: return "GATT_REQUEST_NOT_SUPPORTED";
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION: return "GATT_INSUFFICIENT_ENCRYPTION";
            case BluetoothGatt.GATT_INVALID_OFFSET: return "GATT_INVALID_OFFSET";
            case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH: return "GATT_INVALID_ATTRIBUTE_LENGTH";
            case BluetoothGatt.GATT_CONNECTION_CONGESTED: return "GATT_CONNECTION_CONGESTED";
            case BluetoothGatt.GATT_FAILURE: return "GATT_FAILURE";
            default:
                return  "UNKNOWN_STATUS: " + status;
        }
    }

    /**
     * Utility method for delimiting data.This method is necessary for sending ESP data using
     * Bluetooth SPP.
     *
     * @param data Byte array containing ESP data to be delimited
     *
     * @return Delimited byte data
     */
    public final static byte [] getV1cDelimitedData(byte [] data) {
        int size = data.length + 4;
        final byte [] delimitedData = new byte [size];
        delimitedData[0] = 0x7F;
        delimitedData[1] = (byte) data.length;
        System.arraycopy(data, 0, delimitedData, 2, data.length);
        // Packet Checksum includes the 'Packet Length' byte.
        delimitedData[size - 2] = (byte) (calculateChecksum(data) + delimitedData[1]);
        delimitedData[size - 1] = 0x7F;
        return delimitedData;
    }

    /**
     * Utility method for calculating a checksum from data
     *
     * @param data Source array
     *
     * @return Checksum sub calculated by summing all bytes in data
     */
    public final static byte calculateChecksum(byte [] data) {
        byte cksum = 0x00;
        for (byte datum : data) {
            cksum += datum;
        }
        return cksum;
    }

    /**
     * Utility method for escaping the ESP byte data in buffer. This method is necessary for sending
     * ESP data using Bluetooth SPP.
     *
     * @param buffer Source array
     *
     * @return Escaped ESP data
     */
    public final static byte [] escapeV1cData(byte [] buffer) {
        int escapeCount = 0;
        // We skip the first and the last bytes because
        for(int i = 1, size = buffer.length - 1; i < size; i++) {
            byte b = buffer[i];
            if(b == (byte) 0x7D || b == (byte) 0x7F) {
                escapeCount++;
            }
        }
        if(escapeCount == 0) {
            return buffer;
        }
        byte [] escapedBuffer = new byte[buffer.length + escapeCount];
        escapedBuffer[0] = buffer[0];
        int escapedIndex = 1;
        for(int i = 1, size = (buffer.length - 1); i < size; i++) {
            byte b = buffer[i];
            if(b == (byte) 0x7D) {
                escapedBuffer[escapedIndex++] = 0x7D;
                escapedBuffer[escapedIndex] = 0x5D;
            }
            else if(b == (byte) 0x7F) {
                escapedBuffer[escapedIndex++] = 0x7D;
                escapedBuffer[escapedIndex] = 0x5F;
            }
            else {
                escapedBuffer[escapedIndex] = buffer[i];
            }
            escapedIndex++;
        }
        // Grab the last frame delimiter
        escapedBuffer[escapedIndex] = buffer[buffer.length - 1];
        return escapedBuffer;
    }

    /**
     * Indicates if Bluetooth LE is supported by the current devices. Returns false immediately if the Android build version is below API Level 18.
     *
     * @return  Returns true of the current devices supports Bluetooth LE otherwise, false is returned.
     */
    public final static boolean isLESupported(Context context) {
        // Quick fail if the current Build version is below Android API Level 18 (Version Code: JELLY_BEAN_MR2).
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                // Query the PackageManager for the Bluetooth LE feature.
                context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Indicates if the Bluetooth supported by the device.
     *
     * @return  Returns true if Bluetooth is supported by the devices otherwise, false is returned.
     */
    public final static boolean isBluetoothSupported(Context context) {
        return getBluetoothAdapterCompat(context) != null;
    }

    /**
     * Helper method for retrieving the {@link BluetoothAdapter} for the appropriate API level version.
     *
     * @param context   Context used to retrieve the the {@link BluetoothAdapter} on API level
     *                  {@link Build.VERSION_CODES#JELLY_BEAN_MR2} and above.
     * @return Bluetooth Adapter if the device supports Bluetooth
     */
    public final static BluetoothAdapter getBluetoothAdapterCompat(Context context) {
        // Retrieve a reference to the BluetoothAdapter based on the Android SDK Version.
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return BluetoothAdapter.getDefaultAdapter();
        }
        else {
            BluetoothManager btService = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            return (btService != null) ? btService.getAdapter() : null;
        }
    }

    /**
     * Utility function for checking an object for nullness. If toCheck is null this function will throwing an {@link IllegalArgumentException}.
     * @param toCheck   The object to check for nullness.
     * @param msg       The string message to be passed into {@link IllegalArgumentException IllegalArgumentException's} constructor.
     */
    public final static void nullCheck(Object toCheck, String msg) {
        if (toCheck == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Returns a user friendly name that reflects the passed in {@link BluetoothDevice}
     *
     * @param device Remote Bluetooth device for which you'd like the user friendly name
     *
     * @return User friendly name for device
     */
    public static String getFriendlyName(BluetoothDevice device) {
        if (device == null) {
            return "Unknown Device";
        }
        String name = "";
        if(device != null) {
            @SuppressLint("MissingPermission") String tempName = device.getName();
            name  = (tempName != null) ? tempName : (device.getAddress() != null ? device.getAddress() : "Unknown Device");
        }
        return name;
    }
}