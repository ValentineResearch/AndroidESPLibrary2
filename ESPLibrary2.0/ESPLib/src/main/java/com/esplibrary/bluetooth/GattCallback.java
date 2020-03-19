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
public interface GattCallback {
    /**
     * Callback indicating when GATT client has connected/disconnected to/from a remote
     * GATT server.
     *
     * @param gatt GATT client
     * @param status Status of the connect or disconnect operation. {@link
     * BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     * @param newState Returns the new connection state. Can be one of {@link
     * BluetoothProfile#STATE_DISCONNECTED} or {@link BluetoothProfile#STATE_CONNECTED}
     */
    void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

    /**
     * Callback invoked when the list of remote services, characteristics and descriptors
     * for the remote device have been updated, ie new services have been discovered.
     *
     * @param gatt GATT client invoked {@link BluetoothGatt#discoverServices}
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device has been explored
     * successfully.
     */
    void onServicesDiscovered(BluetoothGatt gatt, int status);

    /**
     * Callback indicating the result of a descriptor write operation.
     *
     * @param gatt GATT client invoked {@link BluetoothGatt#writeDescriptor}
     * @param descriptor Descriptor that was writte to the associated remote device.
     * @param status The result of the write operation {@link BluetoothGatt#GATT_SUCCESS} if the
     * operation succeeds.
     */
    void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);

    /**
     * Callback indicating the result of a characteristic write operation.
     *
     * <p>If this callback is invoked while a reliable write transaction is
     * in progress, the value of the characteristic represents the value
     * reported by the remote device. An application should compare this
     * value to the desired value to be written. If the values don't match,
     * the application must abort the reliable write transaction.
     *
     * @param gatt GATT client invoked {@link BluetoothGatt#writeCharacteristic}
     * @param characteristic Characteristic that was written to the associated remote device.
     * @param status The result of the write operation {@link BluetoothGatt#GATT_SUCCESS} if the
     * operation succeeds.
     */
    void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

    /**
     * Callback triggered as a result of a remote characteristic notification.
     *
     * @param gatt GATT client the characteristic is associated with
     * @param characteristic Characteristic that has been updated as a result of a remote
     * notification event.
     */
    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
}