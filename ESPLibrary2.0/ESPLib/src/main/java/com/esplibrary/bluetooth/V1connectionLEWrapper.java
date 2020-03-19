package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.esplibrary.client.ESPClientListener;
import com.esplibrary.client.ESPRequest;
import com.esplibrary.client.ResponseHandler;
import com.esplibrary.client.callbacks.NoDataListener;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketFactory;
import com.esplibrary.packets.PacketUtils;
import com.esplibrary.utilities.ESPLogger;

import java.util.concurrent.atomic.AtomicBoolean;

public class V1connectionLEWrapper extends V1connectionBaseWrapper implements GattCallback {

    private final static String LOG_TAG = "LEV1cWrpr";

    private final V1cGattCallback mGattCallback;
    protected BluetoothGatt mGatt;
    protected BluetoothGattCharacteristic mClientOut;
    private Handler mHandler;
    private boolean mNotifyOnDisconnection = false;

    protected final AtomicBoolean mCanWrite = new AtomicBoolean(false);

    /**
     * Constructs a Bluetooth Low-Energy {@link IV1connectionWrapper} instance.
     * @param listener The {@link ESPClientListener callback} that will be invoked when ESP data is
     *                 received.
     * @param factory           {@link PacketFactory} used to construct ESP packets.
     * @param timeoutInMillis   Number of milliseconds before the
     * {@link NoDataListener#onNoDataDetected()} is invoked.
     */
    public V1connectionLEWrapper(@Nullable ESPClientListener listener, PacketFactory factory, long timeoutInMillis) {
        super(listener, factory, timeoutInMillis);
        // Create a V1GattCallback and pass a reference to our self so it pass along the GATT callbacks to us.
        // We do this because BluetoothGattCallback is an abstract class and we cannot have duel inheritance in java.
        mGattCallback = new V1cGattCallback(this);
        mHandler = new Handler();
        mCanWrite.set(false);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.LE;
    }

    @Override
    public void connect(Context ctx, BluetoothDevice v1Device) {
        super.connect(ctx, v1Device);
        // Always reset the notify on disconnection
        mNotifyOnDisconnection = true;
        // Check to see if we are already connecting or connected and return.
        if(mState.get() == STATE_CONNECTING || mState.get() == STATE_CONNECTED) {
            return;
        }
        // Set the state to connecting.
        if(mState.compareAndSet(STATE_DISCONNECTED, STATE_CONNECTING)) {
            getHandler().obtainMessage(WHAT_CONNECTION_EVENT, ConnectionEvent.Connecting.ordinal(), 0).sendToTarget();
            // We need to keep a reference to mGatt right here so we can abort the connection
            // attempt if need be...
            synchronized (this) {
                ESPLogger.d(LOG_TAG, "gatt connect called!");
                mGatt = connectGatt(ctx, v1Device, mGattCallback);
            }
        }
        else{
            // We were in the disconnected state while attempting to connect so force it to
            // DISCONNECTED and indicate the connection failed because we don't know why we weren't
            // in an expected state while connecting and the user should probably perform a
            // disconnect first.
            mState.set(STATE_DISCONNECTED);
            // We are in an unexpected state so indicate the connection failed.
            getHandler().obtainMessage(WHAT_CONNECTION_EVENT, ConnectionEvent.ConnectionFailed.ordinal(), 0).sendToTarget();
        }
    }

    /**
     * Connect to GATT Server hosted by this device. Caller acts as GATT client. The callback is used to deliver results to Caller,
     * such as connection status as well as any further GATT client operations. The method returns a BluetoothGatt instance.
     * You can use BluetoothGatt to conduct GATT client operations.
     *
     * If the current device running API level is below 18 returns null.
     *
     * @param context
     * @param callback  GATT callback respHandler that will receive asynchronous callbacks.
     *
     * @throws IllegalArgumentException if callback is null.
     *
     * @return  Returns a BluetoothGatt instance if an exception is not raised. Returns null if the current device's API level is below 18 (JELLY_BEAN_MR2).
     */
    private static BluetoothGatt connectGatt(Context context, BluetoothDevice device, BluetoothGattCallback callback) {
        // ADD OREO SUPPORT IN SOON
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // TODO: 8/25/2017 ADD OREO SUPPORT INTO THE LIBRARY
            return device.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_AUTO, BluetoothDevice.PHY_LE_1M);
        }
        // Call the appropriate connGatt method based on the API level.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return device.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_LE);
        }
        else {
            return device.connectGatt(context, false, callback);
        }
    }

    @Override
    public void disconnect(boolean notifyDisconnect) {
        // If we are in the connecting state transition to the disconnected state and close the
        // BluetoothGatt object... we need to do this because calling disconnect before a connection
        // has been established doesn't guarantee onConnectionStateChange(BluetoothGatt, int int)
        // will be called thus allowing us to traditionally transition into the disconnected state.
        if(mState.get() == STATE_CONNECTING) {
            synchronized (this) {
                if(mGatt != null) {
                    mGatt.disconnect();
                    mGatt.close();
                }
            }
            // Since the onConnectionStateChange won't be called, we must manually call
            // onDisconnected.
            onDisconnected(notifyDisconnect);
        }
        else {
            // The disconnect is asynchronous, so store the notify disconnect flag.
            mNotifyOnDisconnection = notifyDisconnect;
            // Call disconnect on the gatt object and
            synchronized (this) {
                if(mGatt != null) {
                    mGatt.disconnect();
                }
            }
        }
        super.disconnect(notifyDisconnect);
    }

    /**
     * Attempts to enable/disable notifications on both locally and on a remote device for the specified
     * characteristic.
     * @param gatt      Bluetooth connection to a remote device.
     * @param charac    Characteristic on which to enable notifications
     * @param enabled   True to enable notification
     *
     * @return  True if the enable/disable notifications was initiated.
     */
    protected boolean enableCharacteristicNotifications(BluetoothGatt gatt, BluetoothGattCharacteristic charac, boolean enabled) {
        // If the Gatt object or the desired characteristic to be enabled/disabled, is null return false.
        if (gatt == null || charac == null) {
            return false;
        }
        gatt.setCharacteristicNotification(charac, enabled);
        BluetoothGattDescriptor descriptor = charac.getDescriptor(BTUtil.CLIENT_CHARACTERISTIC_CONFIG_CHARACTERISTIC_UUID);
        descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        return gatt.writeDescriptor(descriptor);
    }

    @Override
    public boolean canPerformBTWrite() {
        return mCanWrite.get();
    }

    @Override
    public void setCanPerformBTWrite(boolean canWrite) {
        mCanWrite.set(canWrite);
    }

    @Override
    protected boolean write(byte[] data) {
        BluetoothGatt gatt;
        synchronized (this) {
            gatt = mGatt;
        }
        if(gatt == null) {
            return false;
        }
        if(data == null) {
            return false;
        }
        setCanPerformBTWrite(false);
        mClientOut.setValue(data);
        return gatt.writeCharacteristic(mClientOut);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        // We wanna log the onConnectionStateChange call.
        ESPLogger.d(LOG_TAG, new StringBuilder("onConnectionState(")
                .append(BTUtil.gattOperationToString(status))
                .append(", ")
                .append(BTUtil.gattNewStateToString(newState))
                .append(")")
                .toString());

        if(status == BluetoothGatt.GATT_SUCCESS) {
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                if(isConnecting()) {
                    ESPLogger.d(LOG_TAG, "Bluetooth Gatt connected");

                    BluetoothGattService leService = gatt.getService(BTUtil.V1CONNECTION_LE_SERVICE_UUID);
                    if (leService == null) {
                        ESPLogger.d(LOG_TAG, String.format("V1connection LE Service is null after connecting to %s", BTUtil.getFriendlyName(gatt.getDevice())));
                    }
                    else {
                        ESPLogger.d(LOG_TAG, String.format("V1connection LE Service none-null after connecting to %s", BTUtil.getFriendlyName(gatt.getDevice())));
                    }

                    // We are in the correct state, we now wanna discover the devices services.
                    ESPLogger.d(LOG_TAG, "Discovering BluetoothGatt services...");
                    gatt.discoverServices();

                    mHandler.postDelayed(() -> {
                        ESPLogger.d(LOG_TAG, "Failed to discover device services, disconnecting.");
                        gatt.disconnect();
                    }, 8000);
                    return;
                }
                ESPLogger.w(LOG_TAG, "Incorrect state: we weren't anticipating a connection, disconnecting.");
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Check to see if we were expecting the disconnecting state.
                if(mState.get() == STATE_DISCONNECTING) {
                    ESPLogger.d(LOG_TAG, "Successfully disconnected.");
                    gatt.close();
                    synchronized (this) {
                        mGatt = null;
                    }

                    onDisconnected(mNotifyOnDisconnection);
                    return;
                }
                ESPLogger.w(LOG_TAG, "Incorrect state: we weren't anticipating a disconnection!");
            }
        }

        // Remember to call close(...) instead of disconnect() here. This is necessary because
        // calling  disconnect(...) will trigger a temporary disconnection but a few seconds later a
        // new connection is created. To prevent this we wanna close the BluetoothGatt object.
        synchronized (this) {
            mGatt = null;
        }
        gatt.close();

        // If we made it to this point, and we're connecting, that means we've failed to establish
        // a successful connection.
        if(isConnecting()) {
            onConnectionFailed();
            return;
        }
        else if(isConnected()) {
            onConnectionLost();
            return;
        }
        // Whether failing to connect, or a connection loss transition into the disconnected state.
        onDisconnected(mNotifyOnDisconnection);
        mNotifyOnDisconnection = true;
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        // Clear the service discovery timeout.
        mHandler.removeCallbacksAndMessages(null);
        ESPLogger.d(LOG_TAG, new StringBuilder("onServicesDiscovered(")
                .append(BTUtil.gattOperationToString(status))
                .append(")")
                .toString());
        // If the service discovery was successful, enable notifications for the V1-out, client-in characteristic.
        if(status == BluetoothGatt.GATT_SUCCESS) {
            // Make sure we were in the correct state when services were discovered.
            if(mState.get() == STATE_CONNECTING) {
                ESPLogger.d(LOG_TAG, "Services discovered, enabling notifications for characteristic");
                BluetoothGattService service = gatt.getService(BTUtil.V1CONNECTION_LE_SERVICE_UUID);
                mClientOut = service.getCharacteristic(BTUtil.CLIENT_OUT_V1_IN_SHORT_CHARACTERISTIC_UUID);
                BluetoothGattCharacteristic v1OutClient = service.getCharacteristic(BTUtil.V1_OUT_CLIENT_IN_SHORT_CHARACTERISTIC_UUID);
                enableCharacteristicNotifications(gatt, v1OutClient, true);
                return;
            }
        }
        else {
            // If we failed to discover services while in the connecting state, disconnect because we aren't able to find the V1connectionWrapper LE service.
            if (mState.get() == STATE_CONNECTING) {
                ESPLogger.d(LOG_TAG, "Failed to discover V1connection LE service.");
                gatt.disconnect();
            }
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        ESPLogger.d(LOG_TAG, new StringBuilder("onDescriptorWrite(")
                .append(BTUtil.gattOperationToString(status))
                .append(", Descriptor UUID:")
                .append(descriptor.getUuid().toString())
                .append(")")
                .toString());

        // As of right now, the only descriptor write the V1connectionLEWrapper performs is enabling
        // notifications for the V1-out, client-in characteristic, during the connection process,
        // so if the status is successful,
        if(descriptor.getCharacteristic().getUuid().equals(BTUtil.V1_OUT_CLIENT_IN_SHORT_CHARACTERISTIC_UUID)) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                onConnected();
                return;
            }
            // If we reached this point, that means we failed to enable notifications for the V1-Out
            // Short characteristics and we should disconnect because data communications aren't
            // possible.
            ESPLogger.d(LOG_TAG, "Failed to enable notifications for the V1-out, client-in short characteristic.");
            // Fall-through to the code below, and disconnect.
            gatt.disconnect();
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        // Whenever, we receive this callback, we are able to to write again to the V1-in, client-out characteristic.
        setCanPerformBTWrite(true);

        if(status != BluetoothGatt.GATT_SUCCESS) {
            byte [] value = characteristic.getValue();
            ESPLogger.e(LOG_TAG, String.format("%s failed to write: %s", characteristic.getUuid().toString(), BTUtil.toHexString(value)));

            final ResponseHandler respHndlr = getResponseProcessor().removeResponseHandlerForData(value);
            // Remove queued request that has the same response expector
            synchronized (mRequestQueue) {
                for (int i = mRequestQueue.size() - 1; i >= 0; i--) {
                    final ESPRequest espRequest = mRequestQueue.get(i);
                    if (espRequest.respHandler == respHndlr) {
                        mRequestQueue.remove(i);
                    }
                }
            }
            // Fail the response handler
            if (respHndlr != null) {
                if (respHndlr.failureCallback != null) {
                    respHndlr.failureCallback.onFailure("BTError: Failed to send ESPPacket");
                }
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        // Only process Characteristics on the V1-out, client-in short BTGatt characteristic.
        if(characteristic.getUuid().equals(BTUtil.V1_OUT_CLIENT_IN_SHORT_CHARACTERISTIC_UUID)) {
            byte [] data = characteristic.getValue();
            mBuffer.addAll(data);
            ESPPacket packet = PacketUtils.makeFromBufferLE(mFactory, mBuffer, mLastV1Type);
            // If the packet is null perform send a malformed data msg.
            if(packet == null) {
                malformedData(data);
                return;
            }
            // Ignore echo packets.
            if(checkForEchos(packet)) {
                return;
            }
            // Perform ESP processing.
            processESPPacket(packet);
        }
        else {
            ESPLogger.d(LOG_TAG, "Unsupported characteristic. UUID: " + characteristic.getUuid().toString());
        }
    }
}
