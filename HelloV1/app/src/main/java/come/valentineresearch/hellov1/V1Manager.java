package come.valentineresearch.hellov1;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.esplibrary.bluetooth.BTUtil;
import com.esplibrary.bluetooth.ConnectionEvent;
import com.esplibrary.bluetooth.ConnectionListener;
import com.esplibrary.bluetooth.ConnectionType;
import com.esplibrary.client.ESPClientListener;
import com.esplibrary.client.IESPClient;
import com.esplibrary.client.callbacks.ESPRequestListener;
import com.esplibrary.client.callbacks.ESPRequestedDataListener;
import com.esplibrary.constants.DeviceId;
import com.esplibrary.data.AlertData;
import com.esplibrary.data.SweepDefinition;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.InfDisplayData;
import com.esplibrary.utilities.Range;

import java.util.List;

public class V1Manager implements ESPClientListener, ConnectionListener {

    /**
     * Number of seconds the ESP library should wait before notifying no data has been received.
     */
    public static final int NO_DATA_TIMEOUT = 10000;
    private static V1Manager mV1Manager;

    private IESPClient mClient;

    private V1ManagerDelegate mV1MngrDelegate;
    private Handler mHandler;

    private V1Manager() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Returns a Global {@link V1Manager} instance.
     *
     * @return Global V1Manager
     */
    public static synchronized V1Manager getV1Manager() {
        if (mV1Manager == null) {
            mV1Manager = new V1Manager();
        }
        return mV1Manager;
    }

    /**
     * Indicates if there is a connection established with a V1.
     *
     * @return True if there is a connect established.
     */
    public boolean isConnected() {
        return mClient != null && mClient.isConnected();
    }

    /**
     * Attempts to connect to device using the specified bluetooth technology.
     */
    public boolean connect(Context ctx, BluetoothDevice device, ConnectionType cType) {
        // Should check that the devices supports bluetooth before performing the connection
        if (!BTUtil.isBluetoothSupported(ctx)) {
            Log.e("DEBUG", "Connection failed, bluetooth not supported!!!");
            return false;
        }
        else if(isConnected()) {
            Log.e("DEBUG", "Already connected, don't try to establish a new connection while one is already established!");
            return false;
        }
        mClient = IESPClient.getClient(ctx, this, cType, NO_DATA_TIMEOUT);
        return mClient.connect(device, cType,this);
    }

    /**
     * Disconnects the current ESP
     */
    public void disconnect() {
        if (mClient != null) {
            mClient.disconnect();
        }
    }

    /**
     * Register a {@link V1ManagerDelegate delegate} that will get invoked when ESP events occur.
     *
     * @param delegate Delegate that will be invoked for ESP related events.
     */
    public void setV1ManagerDelegate(V1ManagerDelegate delegate) {
        mV1MngrDelegate = delegate;
    }

    @Override
    public void onPacketReceived(ESPPacket packet) {}

    @Override
    public void onDisplayDataReceived(InfDisplayData displayData) {
        // Because Inf. display data is received several times a second,
        // this callback is never ever called on (MAIN) UI Thread

        // Call the V1Manager from a runnable posted to a Main thread bound handler
        mHandler.post(() -> {
            if (mV1MngrDelegate != null) {
                mV1MngrDelegate.onDisplayData(displayData);
            }
        });
    }

    @Override
    public void onAlertTableReceived(List<AlertData> table) {
        // Because AlertTables can be received several times a second,
        // this callback is never ever called on (MAIN) UI Thread
        // Call the V1Manager from a runnable posted to a Main thread bound handler
        mHandler.post(() -> {
            if (mV1MngrDelegate != null) {
                mV1MngrDelegate.onAlertTableReceived(table);
            }
        });
    }

    @Override
    public void onConnectionEvent(ConnectionEvent event, boolean demo) {
        // ALWAYS called on (MAIN) UI Thread... SAFE TO perform UI operations on this thread
        mHandler.post(() -> {
            if (mV1MngrDelegate != null) {
                mV1MngrDelegate.onConnectionEvent(event);
            }
        });
    }

    /**
     * Request the version from device.
     *
     * @param device    ESP Device whose version you'd like
     * @param callback Listener that will be invoked once the device's version has been read, or if
     *                 an error occurs
     */
    public void requestVersion(DeviceId device, ESPRequestedDataListener<String> callback) {
        if (mClient != null) {
            mClient.requestVersion(device, callback);
        }
    }

    /**
     * Request the V1 to start sending alert tables if it already isn't.
     *
     * @param callback Listener that will be invoked once the request has been completed or an error
     *                occurs
     */
    public void startAlertTables(ESPRequestListener callback) {
        if (mClient != null) {
            mClient.requestStartAlertData(callback);
        }
    }

    /**
     * Request the V1 to stop sending alert tables.
     *
     * @param callback Listener that will be invoked once the request has been completed or an error
     *                occurs
     */
    public void stopAlertTables(ESPRequestListener callback) {
        if (mClient != null) {
            mClient.requestStopAlertData(callback);
        }
    }

    /**
     * Request the V1's custom sweeps/frequencies.
     *
     * @param callback Listener that will be invoked once the request has been completed or an error
     *                occurs.
     */
    public void requestSweeps(ESPRequestedDataListener<List<SweepDefinition>> callback) {
        if (mClient != null) {
            mClient.requestAllSweepDefinitions(callback);
        }
    }

    public static interface V1ManagerDelegate {
        void onConnectionEvent(ConnectionEvent event);
        void onDisplayData(InfDisplayData displayData);
        void onAlertTableReceived(List<AlertData> alerts);
    }
}
