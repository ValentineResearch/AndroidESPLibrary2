package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

import com.esplibrary.client.ESPClientListener;
import com.esplibrary.client.ESPRequest;
import com.esplibrary.client.callbacks.ESPWriteListener;
import com.esplibrary.client.callbacks.MalformedDataListener;
import com.esplibrary.client.callbacks.NoDataListener;
import com.esplibrary.client.callbacks.NotificationListener;
import com.esplibrary.constants.DeviceId;

/**
 * Interface to operate a V1 Bluetooth device.
 */
public interface IV1connectionWrapper {

    /**
     * Returns the ESP data timeout in milliseconds
     * @return ESP data timeout
     */
    long getDataTimeout();

    /**
     * Set's the ESP data timeout
     * @param timeoutMillis Timeout in milliseconds
     */
    void setDataTimeout(long timeoutMillis);

    /**
     * Returns the {@link DeviceId type} of the attached V1.
     *
     * If disconnected, or no V1 attached this value will be {@link DeviceId#UNKNOWN_DEVICE}.
     *
     * @return The type of the attached V1.
     */
    DeviceId getValentineType();

    /**
     * {@link ConnectionType} of the wrapper
     *
     * @return Connection type
     */
    ConnectionType getConnectionType();

    /**
     * {@link BluetoothDevice} this wrapper will establish a connection with
     *
     * @return Connection device
     */
    BluetoothDevice getDevice();

    /**
     * Returns the last read RSSI for the connected remote BLE device. If no connection a value of
     * '-127' will always be returned.
     * 
     * @return The last read RSSI value.
     * @see #readRemoteRSSI(RSSICallback)
     */
    int getCachedRSSI();

    /**
     * Asynchronously reads the connected remote BLE devices RSSI and invokes callback once the
     * value has been read.
     * @param callback the callback that will be invoked with the read back RSSI value.
     *
     * @return True if an Asynchronously read has taken place. If false is returned, check the log
     * output for a hint as to why the call failed.
     */
    boolean readRemoteRSSI(RSSICallback callback);

    /**
     * Asynchronosly attempts to establish a connection with device.
     *
     * @param device the target Bluetooth device to connect.
     */
    void connect(Context ctx, BluetoothDevice device);

    /**
     * Disconnects from the currently connected V1.
     *
     * @param notifyDisconnect
     */
    void disconnect(boolean notifyDisconnect);

    /**
     * Indicates if there is a connection.
     * @return True if connected.
     */
    boolean isConnected();

    /**
     * Indicate if a connection is being established.
     * @return True if connection is established. If connected, this value returns false.
     */
    boolean isConnecting();

    /**
     * Controls if ESP data should be sent while operating in Legacy mode.
     * @param protect True if ESP data shouldn't be sent while in legacy mode.
     */
    void protectLegacyMode(boolean protect);

    /**
     * Set's the dummy ESP data to be used while operating in Demo mode.
     *
     * @param demoData  String ESP data
     */
    void setDemoData(String demoData);

    /**
     * Controls whether demo data should be repeated from start once the end is reached.
     *
     * @param repeat True if the data should be repeated.
     */
    void repeatDemoMode(boolean repeat);

    /**
     * Add an {@link ESPRequest packet} to be sent
     *
     * @param request The packet to be sent.
     */
    void addRequest(ESPRequest request);

    /**
     * Add an {@link ESPRequest packet} to be sent.
     *
     * @param request The packet to be sent.
     * @param nextToSend True if the packet should be pushed to the front of the send queue so that
     *                  it's the very next packet sent.
     */
    void addRequest(ESPRequest request, boolean nextToSend);

    /**
     * Register a listener to be invoked when ESP data has been received.
     *
     * @param listener The {@link ESPClientListener listener} that will be invoked.
     */
    void setESPClientListener(ESPClientListener listener);

    /**
     * Removes the registered {@link ESPClientListener listener}.
     */
    void clearESPClientListener();

    /**
     * Add a listener for connection events.
     *
     * @param listener The {@link ConnectionListener listener} that will be invoked.
     */
    void addConnectionListener(ConnectionListener listener);

    /**
     * Remove a listener for connection events. The listener will receive no further notifications
     * of connection events.
     *
     * @param listener The {@link ConnectionListener listener} to remove.
     */
    void removeConnectionListener(ConnectionListener listener);

    /**
     * Removes all connection event listeners.
     */
    void clearConnectionListeners();

    /**
     * Register a listener to be invoked when bad ESP data has been received.
     *
     * @param listener The {@link MalformedDataListener listener} that will be invoked.
     */
    void setMalformedListener(MalformedDataListener listener);

    /**
     * Clears the registered {@link MalformedDataListener listener}. The listener will receive no
     * further notifications of bad ESP data.
     */
    void clearMalformedListener();

    /**
     * Register a listener to be invoked when ESP data hasn't been detected.
     *
     * @param listener The {@link NoDataListener listener} that will be invoked.
     *
     * @see #getDataTimeout()
     */
    void setNoDataListener(NoDataListener listener);

    /**
     * Clears the registered {@link NoDataListener listener}. The listener will receive no
     * further notifications of no ESP data detection.
     */
    void clearNoDataListener();

    /**
     * Register a listener to be invoked when Notification data has been received.
     * <br><br><b>Note: This callback will be invoked on main (UI) thread.</b>
     *
     * @param listener The {@link NotificationListener listener} that will be invoked.
     */
    void setNotificationListener(NotificationListener listener);

    /**
     * Clears the registered {@link NotificationListener listener}. The listener will receive no
     * further notification data.
     */
    void clearNotificationListener();

    /**
     * Register a listener to be invoked when ESP data has been sent.
     *
     * @param writeListener The {@link ESPWriteListener listener} that will be invoked when ESP data is written
     */
    void setWriteListener(ESPWriteListener writeListener);

    /**
     * Clears the registered {@link ESPWriteListener}.
     */
    void clearWriteListener();
}
