package com.esplibrary.client;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.annotation.Nullable;

import com.esplibrary.bluetooth.BTUtil;
import com.esplibrary.bluetooth.ConnectionListener;
import com.esplibrary.bluetooth.ConnectionType;
import com.esplibrary.bluetooth.V1connectionDemoWrapper;
import com.esplibrary.bluetooth.V1connectionLEWrapper;
import com.esplibrary.bluetooth.V1connectionWrapper;
import com.esplibrary.client.callbacks.ESPRequestListener;
import com.esplibrary.client.callbacks.ESPRequestedDataListener;
import com.esplibrary.client.callbacks.MalformedDataListener;
import com.esplibrary.client.callbacks.NoDataListener;
import com.esplibrary.client.callbacks.NotificationListener;
import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.V1Mode;
import com.esplibrary.data.SAVVYStatus;
import com.esplibrary.data.SweepData;
import com.esplibrary.data.SweepDefinition;
import com.esplibrary.data.SweepSection;
import com.esplibrary.data.UserSettings;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.InfDisplayData;
import com.esplibrary.packets.PacketFactory;

import java.util.List;

/**
 * Interface for interacting with a V1.
 */
public interface IESPClient {

    /**
     * Returns the recommended {@link ConnectionType} based on device capabilities.
     * For devices that support BLE, {@link ConnectionType#LE} is returned.
     *
     * @return Suggested {@link ConnectionType}
     */
    static ConnectionType suggestedConnectionType(Context context) {
        if(BTUtil.isLESupported(context)) {
            return ConnectionType.LE;
        }
        return ConnectionType.SPP;
    }

    /**
     * Returns a {@link IESPClient client} initialized for the specified {@link ConnectionType}.
     *
     * @param type          Connection typed this client will be using to communicate with a
     *                      V1connectionWrapper.
     * @param dataTimeout   Number of seconds the library will wait before indicating no ESP data
     *                      has been received.
     * @param listener      A listener to be invoked when ESP data has been received.
     *
     * @return  Initialized {@link IESPClient client}.
     *
     * @deprecated Use {@link #getClient(Context, ESPClientListener, ConnectionType, long)}
     */
    static IESPClient getClient(Context appContext, @Nullable ESPClientListener listener, ConnectionType type, int dataTimeout) {
        return IESPClient.getClient(appContext, listener, type, (long) dataTimeout * 1000L);
    }

    /**
     * Returns a {@link IESPClient client} initialized for the specified {@link ConnectionType} and no
     * data timeout.
     *
     * @param type          Connection typed this client will be using to communicate with a
     *                      V1connectionWrapper.
     * @param dataTimeout   Number of seconds the library will wait before indicating no ESP
     *                      data has been received.
     * @param listener      A listener to be invoked when ESP data has been received.
     *
     * @return  Initialized {@link IESPClient client}.
     */
    static IESPClient getClient(Context appContext, @Nullable ESPClientListener listener, ConnectionType type, long dataTimeout) {
        PacketFactory factory = new PacketFactory();
        IESPClient client;
        switch (type) {
            case LE:
                client = new ESPValentineClient(appContext, new V1connectionLEWrapper(listener, factory, dataTimeout));
                break;
            case SPP:
                client = new ESPValentineClient(appContext, new V1connectionWrapper(listener, factory, dataTimeout));
                break;
            default: // DEMO
                client = new ESPValentineClient(appContext, new V1connectionDemoWrapper(listener, factory, dataTimeout));
                break;
        }
        return client;
    }

    //region ESP state methods
    /**
     * Returns the ESP data timeout in seconds
     *
     * @return ESP data timeout
     *
     * @deprecated Use {@link #getDataTimeoutMillis()}
     */
    int getDataTimeout();

    /**
     * Set's the ESP data timeout
     * @param timeoutInSeconds Timeout in seconds
     *
     * @deprecated Use {@link #setDataTimeout(long)}
     */
    void setDataTimeout(int timeoutInSeconds);

    /**
     * Returns the ESP data timeout in milliseconds
     * @return ESP data timeout
     */
    long getDataTimeoutMillis();

    /**
     * Set's the ESP data timeout
     * @param timeoutMillis Timeout in milliseconds
     */
    void setDataTimeout(long timeoutMillis);

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
    void repeatDemoData(boolean repeat);

    /**
     * Controls if ESP data should be sent while operating in Legacy mode.
     * @param protect True if ESP data shouldn't be sent while in legacy mode.
     */
    void protectLegacyMode(boolean protect);
    //endregion

    //region Callback Registration methods
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
     * Add a listener to be invoked when a connection events occurs.
     * <br><br><b>Note: This callback will be invoked on main (UI) thread.</b>
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
     * Register a listener to be invoked when bad ESP data has been received.
     * <br><br><b>Note: This callback will be invoked OFF of the main(UI) thread.</b>
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
     * <br><br><b>Note: This callback will be invoked on main (UI) thread.</b>
     *
     * @param listener The {@link NoDataListener listener} that will be invoked.
     *
     * @see #getDataTimeoutMillis()
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
    //endregion

    //region Connection methods
    /**
     * Starts a demo connection.
     *
     * @param demoESPData stringified ESP data
     * @param listener An optional {@link ConnectionListener listener} to register for connection
     *                 events.
     *
     * @return True if a connection was attempted.
     */
    boolean connectDemo(String demoESPData, @Nullable ConnectionListener listener);

    /**
     * Attempts to make a connection with {@link BluetoothDevice device}.
     * <p>If Bluetooth isn't supported, or an invalid connection type, of attempting to connect with
     * a LE device without device support, this method immediately returns false and no connection
     * is attempted.</p>
     *
     * @param device   Device to connect
     * @param connType Bluetooth type of device
     * @param listener Listener that will be invoked when connection events occur.
     *
     * @return True if a connection is attempted.
     */
    boolean connect(BluetoothDevice device, ConnectionType connType, @Nullable ConnectionListener listener);

    /**
     * Disconnects from the currently connected V1.
     */
    void disconnect();

    /**
     * Disconnects from the currently connected V1.
     * @param notifyOnDisconnect True if the registered {@link ConnectionListener} should be
     *                           invoked when disconnected.
     */
    void disconnect(boolean notifyOnDisconnect);

    /**
     * Indicates if there is a connection.
     * @return True if connected.
     */
    boolean isConnected();

    /**
     * Indicates if the library is operating in demo mode.
     *
     * @return True if operating in demo mode.
     */
    boolean isDemoMode();

    /**
     * Indicate if a connection is being established.
     * @return True if connection is established. If connected, this value returns false.
     */
    boolean isConnecting();

    /**
     * Returns current {@link ConnectionType type} of the client.
     *
     * @return Connection type
     */
    ConnectionType getConnectionType();

    /**
     * Returns the currently connected device. If disconnected, but a connection was established
     * previously this returns the last connected device.
     *
     * @return Connected device.
     */
    BluetoothDevice getConnectedDevice();
    //endregion

    //region V1 Info methods
    /**
     * Indicate if the V1 supports Default {@link SweepDefinition Sweep definitions}.
     *
     * @see <a href="https://github.com/ValentineResearch/AndroidESPLibrary/blob/master/Specification/ESP%20Specification%203_003.pdf">ESP Specification</a>
     * @return True if the V1 supports reading Default
     */
    boolean areDefaultSweepDefinitionsAvailableForV1Version(double v1Version);

    /**
     * Returns the {@link DeviceId type} of the attached V1.
     *
     * If disconnected, or no V1 attached this value will be {@link DeviceId#UNKNOWN_DEVICE}.
     *
     * @return The type of the attached V1.
     */
    DeviceId getValentineType();
    //endregion

    //region ESP Data Request methods
    //region Device Information
    /**
     * Request the firmware version of the provided {@link DeviceId device}.
     *
     * @param device    The {DeviceId device} to packet the version from
     * @param callback  A {@link ESPRequestedDataListener callback} that will be invoked when the
     *                  version is received or if an error occurs.
     *
     * @see com.esplibrary.packets.response.ResponseVersion#getVersionDouble(String)
     */
    void requestVersion(DeviceId device, ESPRequestedDataListener<String> callback);

    /**
     * Request the firmware version of the provided {@link DeviceId device} as {@link Double}.
     *
     * @param device    The {DeviceId device} to packet the version from
     * @param callback  A {@link ESPRequestedDataListener callback} that will be invoked when the
     *                  version is received or if an error occurs.
     */
    void requestVersionAsDouble(DeviceId device, ESPRequestedDataListener<Double> callback);

    /**
     * Request the serial number of the provided {@link DeviceId device}.
     *
     * @param device    The {@link DeviceId device} to packet the serial number from
     * @param callback  The {@link ESPRequestedDataListener callback} that will be invoked when the
     *                  serial number is received or if an error occurs.
     */
    void requestSerialNumber(DeviceId device, ESPRequestedDataListener<String> callback);
    //endregion

    //region User Setup Options
    /**
     * Request the current user modifiable {@link UserSettings settings} in the V1.
     *
     * @param v1Version V1 version used to interpret the user settings. Versions greater than
     *                  V4.0000 will result in a {@link com.esplibrary.data.V19UserSettings} to be
     *                  passed to callback.
     * @param callback  The {@link ESPRequestedDataListener callback} that will be invoked when the
     *                  {@link UserSettings settings} are received or if an error occurs.
     */
    void requestUserSettings(double v1Version, ESPRequestedDataListener<UserSettings> callback);

    /**
     * Request the current user modifiable settings in the V1.
     * @param callback  The {@link ESPRequestedDataListener callback} that will be invoked when the
     *                  settings are received or if an error occurs.
     */
    void requestUserBytes(ESPRequestedDataListener<byte []> callback);

    /**
     * Request to update the user configuration @link UserSettings settings} inside the V1.
     *
     * <br><br> Note: This operation makes no guarantee that the V1's settings have been successfully
     * updated with the provided user bytes, simply that the packet was sent and no errors were
     * received.
     *
     * @param userBytes The new settings to update the V1's settings.
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the
     *                 userBytes are sent or if an error occurs.
     */
    void requestWriteUserBytes(byte [] userBytes, ESPRequestListener callback);

    /**
     * Request to reset the provided {@link DeviceId device's} factory settings.
     *
     * <br><br> The effect of this packet will vary according to the target
     * {@link DeviceId device's}. Refer to the table 6.1 for the packet's effect on each device that
     * complies with this specification.
     *
     * @see <a href="https://github.com/ValentineResearch/AndroidESPLibrary/blob/master/Specification/ESP%20Specification%203_003.pdf">ESP Specification</a>
     *
     * @param callback  The {@link ESPRequestListener callback} that will be invoked when the
     *                  packet is sent or if an error occurs.
     */
    void requestFactoryDefault(DeviceId device, ESPRequestListener callback);
    //endregion

    //region Custom Sweep
    /**
     * Request for the V1 to use the List of {@link SweepDefinition custom sweeps} instead of the
     * factory sweeps when in Euro Mode.
     *
     * <br>
     * <br>
     * <p>
     *     The number of supported sweeps is V1 version specific and can be obtained using
     *     {@link #requestMaxSweepIndex(ESPRequestedDataListener)}. The valid sweep sections can be
     *     read using {@link #requestSweepSections(ESPRequestedDataListener)}. If a
     *     {@link SweepDefinition sweep defintion} is invalid, such as when a sweep definition
     *     crosses a sweep range boundary, the sweep will not be used. Refer to the Custom Sweep
     *     range for more details.
     * </p>
     *
     * @param sweeps    List of {@link SweepDefinition custom sweeps} the V1 should use.
     * @param callback  The {@link ESPRequestedDataListener callback} that will be invoked when
     *                  all sweeps have been written or if an error occurs.
     *                  <ul>Callback accepts an integer as it's first param. This integer value is
     *                      the sweep write result.
     *                      <li>{@code 0 ==} Sweep Write Successful.</li>
     *                      <li>
     *                          Any Other Value equals the number of the first sweep with
     *                          invalid parameters. (The error number returned will be the sweep
     *                          index + 1, where sweep index is the index from the
     *                          {@link #requestWriteSweepDefinitions(List, ESPRequestedDataListener)}).
     *                      </li>
     *                  </ul>
     */
    void requestWriteSweepDefinitions(List<SweepDefinition> sweeps, ESPRequestedDataListener<Integer> callback);

    /**
     * Request all custom custom sweep {@link SweepDefinition definitions}.
     *
     * @param callback The {@link ESPRequestedDataListener} that will be invoked when all sweeps are
     *                received or an error occurs.
     */
    void requestAllSweepDefinitions(ESPRequestedDataListener<List<SweepDefinition>> callback);

    /**
     * Request that the V1 reset all custom sweep {@link SweepDefinition definitions} back to their
     * default. When operating in Euro Mode, this will change the display from 'C' to 'U' or 'c' to
     * 'u' depending on the user's mode selection.
     *
     * <br><br> Note: This operation makes no guarantee that the V1's sweeps have been reset
     * simply that the packet was sent and no errors were received. (see {@link #requestAllSweepDefinitions(ESPRequestedDataListener)})
     *
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the
     *                 default sweeps packet is sent or if an error occurs.
     */
    void requestDefaultSweeps(ESPRequestListener callback);

    /**
     * Request to determine how many sweeps the current V1 version supports.
     *
     * @param callback The {@link ESPRequestedDataListener callback} will be invoked when the max
     *                 sweep value is received or if an error occurs.
     */
    void requestMaxSweepIndex(ESPRequestedDataListener<Integer> callback);

    /**
     * Request the V1's available custom sweep {@link SweepSection sections}.
     *
     * @param callback The {@link ESPRequestedDataListener callback} will be invoked when the sweep
     *                 sections are received or if an error occurs.
     */
    void requestSweepSections(ESPRequestedDataListener<List<SweepSection>> callback);

    /**
     * Request all default custom sweep {@link SweepDefinition definitions}.
     *
     * @param callback The {@link ESPRequestedDataListener} that will be invoked when all sweeps are
     *                received or an error occurs.
     */
    void requestDefaultSweepDefinitions(ESPRequestedDataListener<List<SweepDefinition>> callback);

    /**
     * Request all sweep data (max sweep index, sweep {@link SweepSection sections} custom sweep
     * {@link SweepDefinition definitions}, default sweep {@link SweepDefinition definitions}) from
     * the V1.
     *
     * @param v1Version Version of the currently attached V1.
     * @param callback The {@link ESPRequestedDataListener callback} will be invoked when the sweep
     *                 data has been received or if an error occurs.
     */
    void requestSweepData(double v1Version, ESPRequestedDataListener<SweepData> callback);
    //endregion

    //region Display and Audio
    /**
     * Indicates if the ESP client is currently operating in Legacy mode.
     *
     * @return True if the client is operating in demo mode.
     */
    boolean isLegacy();

    /**
     * Request to force the V1's display on or off.
     * @param on        The new display state.
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the
     *                 display packet is sent or if an error occurs.
     */
    void requestDisplayOn(boolean on, ESPRequestListener callback);

    /**
     * Request to mute all alerts in the V1. The results of this packet can be verified using the
     * {@link InfDisplayData#getAuxData()}. The V1 treats this packet as a mute button press.
     * Therefore, this command is only in effect until all alerts are no longer being tracked byvthe
     * V1.
     *
     * <br><br><b>Note: The requestMute(false) will not unmute a laser alert</b>
     *
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the V1's
     *                 has been muted/unmuted or if an error occurs.
     */
    void requestMute(boolean mute, ESPRequestListener callback);

    /**
     * Request to change the current mode on the V1.
     *
     * @param mode      The new mode the V1 should operate in
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the V1's
     *                 mode has changed or if an error occurs.
     */
    void requestChangeMode(V1Mode mode, ESPRequestListener callback);
    //endregion

    //region Alert Output
    /**
     * Request to receive information on all alerts being displayed on the V1's front panel.
     *
     * <br><br>
     * <p>
     *     Alert information is represented by {@link com.esplibrary.data.AlertData}. Registering a
     *     {@link ESPClientListener} allows you to receive individual
     *     {@link com.esplibrary.packets.response.ResponseAlertData alerts} by implementing
     *     {@link ESPClientListener#onPacketReceived(ESPPacket)}. To receive a full alert table,
     *     implement {@link ESPClientListener#onAlertTableReceived(List)}.
     * </p>
     *
     * @param callback The {@link ESPRequestListener callback} that will be invoked when alert
     *                 information has started to be received or if an error occurs.
     */
    void requestStartAlertData(ESPRequestListener callback);

    /**
     * Request to stop the V1 from sending alert information.
     * @param callback The {@link ESPRequestListener callback} that will be invoked when stop alert
     *                 data packet is sent or if an error occurs.
     */
    void requestStopAlertData(ESPRequestListener callback);

    /**
     * Requests the V1 to stop sending alert information as fast as possible.
     *
     * @param callback  The {@link ESPRequestListener callback} that will be invoked when stop alert
     *                  data packet is sent or if an error occurs.
     */
    void requestStopAlertDataImmediately(ESPRequestListener callback);
    //endregion

    //region Miscellaneous
    /**
     * Request the battery voltage at the connection to the V1.
     *
     * @param callback The {@link ESPRequestedDataListener callback} that will be invoked when the
     *                 battery voltage is received or if an error occurs.
     */
    void requestBatteryVoltage(ESPRequestedDataListener<String> callback);
    //endregion

    //region SAVVY Specific
    /**
     * Request the current status from the SAVVY.
     *
     * @param callback The {@link ESPRequestedDataListener callback} that will be invoked when the
     *                 {@link SAVVYStatus} is received or if an error occurs.
     */
    void requestSAVVYStatus(ESPRequestedDataListener<SAVVYStatus> callback);

    /**
     * Request the current status from the SAVVY.
     *
     * @param callback The {@link ESPRequestedDataListener callback} that will be invoked when the
     * {@link SAVVYStatus} is received or if an error occurs.
     *
     * @param requestTimeout Number of milliseconds this request will wait for a response before
     *                       timing out
     */
    void requestSAVVYStatus(ESPRequestedDataListener<SAVVYStatus> callback, long requestTimeout);

    /**
     * Request the current vehicle speed measured by the SAVVY.
     *
     * @param callback The {@link ESPRequestedDataListener callback} that will be invoked when the
     *                 vehicle speed is received or if an error occurs.
     */
    void requestVehicleSpeed(ESPRequestedDataListener<Integer> callback);

    /**
     * Request to override the SAVVY mute threshold speed. The new speed setting will be used until
     * the SAVVY is unplugged from the device or the thumbwheel is changed.
     *
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the
     *                 override thumbwheel packet is sent or if an error occurs.
     */
    void requestOverrideThumbwheel(byte speed, ESPRequestListener callback);

    /**
     * Request to disable SAVVY muting at any speed.
     *
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the
     *                 override thumbwheel packet is sent or if an error occurs.
     */
    void requestOverrideThumbwheelToNone(ESPRequestListener callback);

    /**
     * Request to mute at all speeds ("Auto").
     *
     * @param callback The {@link ESPRequestListener callback} that will be invoked when the
     *                 override thumbwheel packet is sent or if an error occurs.
     */
    void requestOverrideThumbwheelToAuto(ESPRequestListener callback);

    /**
     * Request to enable or disable the unmute functionality in the SAVVY.
     *
     * @param muteEnabled   Controls if unmuting is enabled
     * @param callback      The {@link ESPRequestListener callback} that will be invoked when the
     *                      SAVVY unmute packet is sent or if an error occurs.
     */
    void requestSAVVYUnmute(boolean muteEnabled, ESPRequestListener callback);
    //endregion
    //endregion

    /**
     * Destroys the ESP client and clears all registered callbacks.
     */
    void destroy();
}
