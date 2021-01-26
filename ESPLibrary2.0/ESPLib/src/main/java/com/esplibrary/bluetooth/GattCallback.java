package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;

/**
 * A special interface that mirrors a subset of methods from the BluetoothGattCallback abstract
 * class. This interface allows an implementor to inherit basic Bluetooth Gatt callbacks without
 * extending BluetoothGattCallback.
 */
interface GattCallback {
    /**
     * Callback that will get invoked once the connection state with a remote BLE device changes.
     *
     * @param gatt Interface for communicating with the connected remote BLE device using Bluetooth
     *             Gatt functionality.
     * @param status Status of the connection event.
     * @param newState New connection state. Possible values are:
     * {@link BluetoothProfile#STATE_DISCONNECTED},{@link BluetoothProfile#STATE_CONNECTED}
     */
    void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

    /**
     * Callback that will get invoked once GATT services for the connected remote BLE device have
     * been discovered.
     *
     * @param gatt Interface for communicating with the connected remote BLE device using Bluetooth
     *             Gatt functionality.
     * @param status Status of the service discovery request.
     *
     * @see {@link BluetoothGatt#discoverServices()}
     */
    void onServicesDiscovered(BluetoothGatt gatt, int status);

    /**
     * Callback that will get invoked after a local write to a remote BLE descriptor has completed.
     *
     * @param gatt Interface for communicating with the connected remote BLE device using Bluetooth
     *             Gatt functionality.
     * @param descriptor The remote BLE device descriptor that was written to
     * @param status Status of the descriptor write.
     */
    void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    /**
     * Callback that will get invoked whenever a characteristic has been locally written to the
     * connected remote BLE device.
     *
     * @param gatt Interface for communicating with the connected remote BLE device using Bluetooth
     *             Gatt functionality.
     * @param characteristic The remote BLE device characteristic that was written to
     * @param status Status of the characteristic write.
     */
    void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                               int status);

    /**
     * Callback that will get invoked whenever the connected remote BLE device writes to a
     * characteristic whose notifications have be enabled.
     *
     * @param gatt Interface for communicating with the connected remote BLE device using Bluetooth
     *             Gatt functionality.
     * @param characteristic The characteristic the connected remote BLE device has changed.
     *                       See {@link BluetoothGattCharacteristic#getValue()} for the result of
     *                       the change.
     */
    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    /**
     * Callback triggered as a result of an RSSI read operation.
     *
     * @param gatt Interface for communicating with the connected remote BLE device using Bluetooth
     *             Gatt functionality.
     * @param rssi The RSSI value for the remote device
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the RSSI was read successfully
     */
    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
}