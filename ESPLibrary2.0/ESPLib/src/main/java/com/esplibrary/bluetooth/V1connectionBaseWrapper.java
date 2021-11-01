package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.esplibrary.client.AlertDataProcessor;
import com.esplibrary.client.ESPClientListener;
import com.esplibrary.client.ESPRequest;
import com.esplibrary.client.ResponseHandler;
import com.esplibrary.client.ResponseProcessor;
import com.esplibrary.client.callbacks.ESPWriteListener;
import com.esplibrary.client.callbacks.MalformedDataListener;
import com.esplibrary.client.callbacks.NoDataListener;
import com.esplibrary.client.callbacks.NotificationListener;
import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.data.AlertData;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.InfDisplayData;
import com.esplibrary.packets.PacketFactory;
import com.esplibrary.packets.response.ResponseRequestNotProcessed;
import com.esplibrary.utilities.ByteList;
import com.esplibrary.utilities.ESPLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class V1connectionBaseWrapper implements IV1connectionWrapper, Handler.Callback, Runnable {

    private final static String LOG_TAG = "V1cWrpr";

    /**
     * Echo {@link ESPPacket packet} timeout.
     */
    private final static long ECHO_TIMEOUT = 1000;
    /**
     * Maximum number of {@link ESPPacket packets} allowed in the echo queue.
     */
    private final static int MAXPACKETECHOS = 4;
    /**
     * Disconnected connection state-machine constants
     */
    protected final static int STATE_DISCONNECTED = 1;
    /**
     * Disconnecting connection state-machine constants
     */
    protected final static int STATE_CONNECTING = STATE_DISCONNECTED + 1;
    /**
     * Connecting connection state-machine constants
     */
    protected final static int STATE_CONNECTED = STATE_CONNECTING + 1;
    /**
     * Disconnecting connection state-machine constants
     */
    protected final static int STATE_DISCONNECTING = STATE_CONNECTED + 1;
    /**
     * {@link Message#what} constant for sending a no data message.
     */
    protected final static int WHAT_NO_DATA = 444;
    /**
     * {@link Message#what} constant for sending a connection event message.
     */
    protected final static int WHAT_CONNECTION_EVENT = WHAT_NO_DATA + 1;
    /**
     * {@link Message#what} constant for sending a Malformed Data message.
     */
    protected final static int WHAT_MALFORMED_DATA = WHAT_CONNECTION_EVENT + 1;
    /**
     * Display Data counter reset value.
     */
    private static final int V1_BUSY_RESET_VAL = 0;
    /**
     * Number of consecutive Display Data packets must be received before the V1 is no
     * longer considered busy.
     */
    private static final int V1_NOT_BUSY_THRESH = 2;
    /**
     * Number of times the Display data packet counter variable should be incremented.
     */
    private static final int BUSY_INCREMENT_THRESH = V1_NOT_BUSY_THRESH + 1;
    /**
     * The number of consecutive {@link InfDisplayData} packets before the library switches the type
     * of the V1.
     */
    private final static int V1_TYPE_SWITCH_THRESHOLD = 10;
    /**
     * Default {@link ResponseHandler} timeout.
     */
    public static final int RESPONSE_TIMEOUT = 5000;
    /**
     * Handler for performing various actions on the Main (UI) thread.
     */
    private final Handler mHandler;
    /**
     * Handler used for checking the ResponseHandlers for expiration.
     */
    private Handler mExpirationH;
    /**
     * Thread used for writing ESP data to the ESP bus.
     */
    private Thread mWriterThread;
    /**
     *
     */
    private HandlerThread mRequestExpiryThread;
    /**
     * Callbacks that will be invoked when a connection event occurs.
     */
    private List<ConnectionListener> mConnections;
    /**
     * Callback that will be invoked when bad ESP data has been received.
     */
    private MalformedDataListener mMalformedCB;
    /**
     * Callback that will be invoked when no data has been received in {@link #getDataTimeout()}
     * milliseconds.
     */
    private NoDataListener mNoDataCB;
    /**
     * Queue of {@link ESPRequest} that will be sent on the ESP bus.
     */
    protected final List<ESPRequest> mRequestQueue;
    /**
     * Echo queue of {@link ESPPacket packets}
     */
    private final List<ESPPacket> mEchoQueue;
    /**
     * List of ESP packet ID's that the V1 is busy processing.
     */
    private List<Integer> mBusyPacketIDs;
    /**
     * Contains the connection state.
     */
    protected AtomicInteger mState;
    /**
     * Indicates the last V1 type received from an ESP packet.
     */
    protected DeviceId mLastV1Type = DeviceId.UNKNOWN_DEVICE;
    /**
     * Flag that controls of ESP should be protected while in Legacy mode. Setting this to true will prevent packets from being put on the ESP bus, while it is in <i>Legacy</i>mode.
     */
    private boolean mProtectLegacy;
    /**
     * Temporarily store the device type fo the attached V1.
     */
    private DeviceId mValentineTypeTmp = DeviceId.UNKNOWN_DEVICE;
    /**
     * Indicates {@link DeviceId} of the attached V1.
     */
    private DeviceId mRealValentineType = DeviceId.UNKNOWN_DEVICE;
    /**
     * Number of display data packets that's been received since a different V1 type was detected.
     */
    private int mV1TypeSwitchCounter = 0;
    /**
     * Number of display data packets that's been received since the V1 sent an
     * {@link com.esplibrary.packets.InfV1Busy} packet.
     */
    private int mDisplayCount = 0;
    /**
     * Number of milliseconds before the {@link NoDataListener#onNoDataDetected()} callback is
     * invoked.
     */
    private long mDataTimeoutMillis;
    /**
     * Flag that controls if packet echoing is used.
     */
    private volatile boolean mUseEchoQ;
    /**
     * Indicate if Time Slicing is off on the ESP bus.
     */
    private AtomicBoolean mTSHoldoff;
    /**
     * Factory used for creating {@link ESPPacket packets} received on the wire.
     */
    protected PacketFactory mFactory;
    /**
     * Buffer used to store bytes received from the BT connect.
     */
    protected ByteList mBuffer;
    /**
     * Used to complete in-flight {@link ResponseHandler resp. handlers}
     */
    private final ResponseProcessor mResponseProcessor;
    /**
     * Processor for constructing a full table of {@link AlertData}.
     */
    private AlertDataProcessor mAlertProcessor;
    /**
     * Callback that will be invoked when ESP data has been received.
     */
    private ESPClientListener mESPListener;

    private ESPWriteListener mWriteListener;

    private BluetoothDevice mV1BTDevice;

    /**
     *
     * @param listener
     * @param factory
     * @param timeoutInMillis
     */
    public V1connectionBaseWrapper(ESPClientListener listener, PacketFactory factory, long timeoutInMillis) {
        mHandler = new Handler(Looper.getMainLooper(), this);
        mResponseProcessor = new ResponseProcessor(RESPONSE_TIMEOUT);
        mRequestQueue = new ArrayList<>(6);
        mConnections = new ArrayList<>(4);
        mEchoQueue = new ArrayList<>(5);
        mBusyPacketIDs = new ArrayList<>();
        mBuffer = new ByteList(22);

        mFactory = factory;
        setESPClientListener(listener);
        mUseEchoQ = true;
        mProtectLegacy = true;
        mDataTimeoutMillis = timeoutInMillis;
        mState = new AtomicInteger(STATE_DISCONNECTED);
        mTSHoldoff = new AtomicBoolean(true);
    }

    /**
     * Returns the response processor used for completing ESP packet.
     *
     * @return Response processor
     */
    protected ResponseProcessor getResponseProcessor() {
        return mResponseProcessor;
    }

    @Override
    public long getDataTimeout() {
        return mDataTimeoutMillis;
    }

    @Override
    public void setDataTimeout(long timeoutMillis) {
        mDataTimeoutMillis = timeoutMillis;
        mHandler.removeMessages(WHAT_NO_DATA);
        // If we are connected, requeue a delay no data message.
        if(isConnected()) {
            mHandler.sendEmptyMessageDelayed(WHAT_NO_DATA,
                    mDataTimeoutMillis);
        }
    }

    /**
     * Check if any of the queue {@link ESPRequest packet} or in flight packet have expired.
     */
    public void checkRequestForExpiration() {
        if (mResponseProcessor != null) {
            mResponseProcessor.checkForExpiry(mRequestQueue);
        }
        // As long as we are connected, we should check for expired packet and response handlers
        if (isConnected()) {
            if (mExpirationH != null) {
                mExpirationH.postDelayed(this::checkRequestForExpiration, 80);
            }
        }
    }

    @Override
    public void protectLegacyMode(boolean protect) {
        mProtectLegacy = protect;
    }

    @Override
    public void setESPClientListener(ESPClientListener listener) {
        synchronized (this) {
            mESPListener = listener;
        }
    }

    @Override
    public void clearESPClientListener() {
        setESPClientListener(null);
    }

    /**
     *
     */
    @Override
    public void setWriteListener(ESPWriteListener writeListener) {
        synchronized (this) {
            mWriteListener = writeListener;
        }
    }

    /**
     *
     */
    @Override
    public void clearWriteListener() {
        setWriteListener(null);
    }

    /**
     * Returns a Main thread (UI) bound handler.
     *
     * @return Main thread Handler
     */
    protected Handler getHandler() { return mHandler; }

    @Override
    public final DeviceId getValentineType() {
        synchronized (mRealValentineType) {
            return mRealValentineType;
        }
    }

    /**
     * Synchronously set the device type of the attached V1.
     */
    void setValentineType(DeviceId newType) {
        synchronized (mRealValentineType) {
            mRealValentineType = newType;
        }
    }

    @Override
    public BluetoothDevice getDevice() {
        return mV1BTDevice;
    }

    @Override
    public int getCachedRSSI() {
        return -127;
    }

    @Override
    public boolean readRemoteRSSI(RSSICallback callback) {
        // By default reading RSSI isn't supported so return false
        return false;
    }

    @Override
    public boolean isConnected() {
        return mState.get() == STATE_CONNECTED;
    }

    @Override
    public boolean isConnecting() {
        return mState.get() == STATE_CONNECTING;
    }

    @Override
    public void setDemoData(String demoData) {}

    @Override
    public void repeatDemoMode(boolean repeat) {}

    @Override
    public void connect(Context ctx, BluetoothDevice v1Device) {
        ESPLogger.d(LOG_TAG, "connect called!");
        mV1BTDevice = v1Device;
    }

    @Override
    public void disconnect(boolean notifyDisconnect) {
        // Clear any pending packet.
        clearRequests();
        // When disconnecting, we wanna shutdown the writer thread.
        stopWriter();
        // If we aren't currently disconnected, enter the disconnecting state.
        if(mState.get() != STATE_DISCONNECTED) {
            mState.set(STATE_DISCONNECTING);
            boolean isDemo = getConnectionType() == ConnectionType.Demo;
            Message.obtain(getHandler(), WHAT_CONNECTION_EVENT, ConnectionEvent.Disconnecting.ordinal(), isDemo ? 1 : 0 ).sendToTarget();
        }
    }

    /**
     * Finalizes the connection process and notifies the registered listener that the library is
     * connected.
     */
    protected void onConnected() {
        ESPLogger.d(LOG_TAG, "ESPLibrary connected!");
        // When we connect we want to set ts hold off to true.
        mTSHoldoff.set(true);
        // Whenever we connect, set can write to true.
        setCanPerformBTWrite(true);
        mDisplayCount = 0;
        mBusyPacketIDs.clear();

        // Whenever we connect, we want to enable echoing.
        mUseEchoQ = true;
        // Stop the previously running writer thread.
        stopWriter();
        mWriterThread = new Thread(this, "WriterThread");
        mWriterThread.start();

        // Create a worker thread that will wake up every 100 milliseconds and check for expired
        // packets.
        mRequestExpiryThread = new HandlerThread("Expiration Thread");
        mRequestExpiryThread.start();
        mExpirationH = new Handler(mRequestExpiryThread.getLooper());
        // Every 100 milliseconds check to see if any packet have expired.
        mExpirationH.postDelayed(this::checkRequestForExpiration, 80);

        boolean isDemo = getConnectionType() == ConnectionType.Demo;
        // Set the state to connected only if we are currently in the connecting state otherwise we
        // are in an invalid state an should disconnect.
        if(mState.compareAndSet(STATE_CONNECTING, STATE_CONNECTED)) {
            Message.obtain(mHandler, WHAT_CONNECTION_EVENT, ConnectionEvent.Connected.ordinal(), isDemo ? 1 : 0).sendToTarget();
        }
        else {
            Message.obtain(mHandler, WHAT_CONNECTION_EVENT, ConnectionEvent.ConnectionFailed.ordinal(), isDemo ? 1 : 0).sendToTarget();
            disconnect(true);
        }
    }

    /**
     * Transitions the wrapper into the disconnected state and fires the
     * {@link ConnectionEvent#ConnectionLost} event.
     */
    protected void onConnectionLost() {
        ESPLogger.d(LOG_TAG, "Connection lost!");
        onDisconnected(false);
        boolean isDemo = getConnectionType() == ConnectionType.Demo;
        // Send a message indicating that we lost the connection.
        getHandler().obtainMessage(WHAT_CONNECTION_EVENT, ConnectionEvent.ConnectionLost.ordinal(), isDemo ? 1 : 0).sendToTarget();
    }

    /**
     * Transitions the wrapper into the disconnected state and fires the
     * {@link ConnectionEvent#ConnectionFailed} event.
     */
    protected void onConnectionFailed() {
        ESPLogger.d(LOG_TAG, "Connection failed!");
        onDisconnected(false);
        boolean isDemo = getConnectionType() == ConnectionType.Demo;
        // Send a message indicating that we failed to connect.
        getHandler().obtainMessage(WHAT_CONNECTION_EVENT, ConnectionEvent.ConnectionFailed.ordinal(), isDemo ? 1 : 0).sendToTarget();
    }


    /**
     * Finalizes the disconnection process and notifies the registered listener that the library is
     * disconnected.
     */
    protected void onDisconnected() {
        onDisconnected(true);
    }

    /**
     * Finalizes the disconnection process and notifies the registered listener that the library is
     * disconnected.
     *
     * @param notify Controls whether the {@link ConnectionEvent#Disconnected} event is fired.
     */
    protected void onDisconnected(boolean notify) {
        ESPLogger.d(LOG_TAG, "ESPLibrary disconnected!");
        stopWriter();
        // Remove any queue no data messages.
        mHandler.removeMessages(WHAT_NO_DATA);
        mState.set(STATE_DISCONNECTED);

        if (notify) {
            boolean isDemo = getConnectionType() == ConnectionType.Demo;
            // Whenever we disconnect, we wanna notify that we've disconnected
            mHandler.obtainMessage(WHAT_CONNECTION_EVENT, ConnectionEvent.Disconnected.ordinal(), isDemo ? 1 : 0).sendToTarget();
        }

        setValentineType(DeviceId.UNKNOWN_DEVICE);
        clearEchoQueue();
        // Expire all packet currently awaiting a response.
        mResponseProcessor.expireRequestsForDisconnection();

        // Stop the handler thread.
        if (mRequestExpiryThread != null) {
            mRequestExpiryThread.quit();
            mRequestExpiryThread = null;
        }
    }

    //region Request code
    @Override
    public void addRequest(ESPRequest request) {
        addRequest(request, false);
    }

    @Override
    public void addRequest(ESPRequest request, boolean nextToSend) {
        // Check to see if we should auto fail the packet...
        // As of right now, we only auto fail when a connection hasn't been established. Since
        // packet are given a finite time to be sent and if a connection being made the packet(s)
        // may timeout before a connection has been established, so its better to fail fast and
        // establish waiting for a connection prior to sending ESP packet.
        if(shouldAutoFailRequest(request)) {
            ResponseHandler respHandler = request.respHandler;
            if (respHandler != null) {
                if (respHandler.failureCallback != null) {
                    String error;
                    if(!isConnected()) {
                        error = "Request failed to send because library isn't connected!";
                    }
                    else {
                        error = "Request failed to send for unknown reason";
                    }
                    respHandler.failureCallback.onFailure(error);
                }
            }
            return;
        }

        synchronized (mRequestQueue) {
            // If next to send is true, we want to add the packet next in the queue
            if (nextToSend) {
                mRequestQueue.add(0, request);
            }
            else {
                mRequestQueue.add(request);
            }
            // Notify the potentially waiting writer thread.
            mRequestQueue.notify();
        }
    }

    /**
     * Removes all pending packet in the queue.
     */
    protected void clearRequests() {
        synchronized (mRequestQueue) {
            mRequestQueue.clear();
        }
    }

    /**
     * Indicates if a {@link ESPRequest} should automatically be failed.
     * @return True if there isn't a connection present.
     */
    private boolean shouldAutoFailRequest(ESPRequest req) {
        // We wanna fail requests when we aren't connected.
        return !isConnected();
    }

    //endregion

    //region Callback registration
    @Override
    public void addConnectionListener(ConnectionListener listener) {
        synchronized(mConnections) {
            if(!mConnections.contains(listener)) {
                mConnections.add(listener);
            }
        }
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        synchronized(mConnections) {
            mConnections.remove(listener);
        }
    }

    @Override
    public void clearConnectionListeners() {
        synchronized(mConnections) {
            mConnections.clear();
        }
    }

    @Override
    public void setMalformedListener(MalformedDataListener listener) {
        synchronized(this) {
            mMalformedCB = listener;
        }
    }

    @Override
    public void clearMalformedListener() {
        setMalformedListener(null);
    }

    @Override
    public void setNoDataListener(NoDataListener listener) {
        synchronized (this) {
            mNoDataCB = listener;
        }
    }

    @Override
    public void clearNoDataListener() {
        setNoDataListener(null);
    }

    @Override
    public void setNotificationListener(NotificationListener listener) {/*INTENTIONALLY LEFT BLANK*/}

    @Override
    public void clearNotificationListener() {/*INTENTIONALLY LEFT BLANK*/}

    //endregion

    //region Callback invoking methods
    /**
     * Performs an {@link ConnectionListener#onConnectionEvent(ConnectionEvent, boolean)} callback on all registered {@link ConnectionListener} listeners.
     * @param event The connection event that being notified.
     * @param demo  Indicating if the connection event is for a demo mode connection.
     */
    private void performConnectionEvent(ConnectionEvent event, boolean demo) {
        synchronized (mConnections) {
            for (int i = 0; i < mConnections.size(); i++) {
                mConnections.get(i).onConnectionEvent(event, demo);
            }
        }
    }

    /**
     * Performs an {@link MalformedDataListener#onBadESPData(String)} callback on all registered {@link MalformedDataListener} listeners.
     * @param message  The bad data message to deliver to the MalformedDataListener listener.
     */
    private void performBadDataCallback(String message) {
        synchronized(this) {
            if (mMalformedCB != null) {
                mMalformedCB.onBadESPData(message);
            }
        }
    }

    /**
     * Performs an {@link NoDataListener#onNoDataDetected()} callback on all registered {@link NoDataListener} listeners.
     */
    private void performNoDataCallback() {
        synchronized (this) {
            if (mNoDataCB != null) {
                mNoDataCB.onNoDataDetected();
            }
        }
    }
    //endregion

    //region ESP processing.
    /**
     * Perform processing on the provided {@link ESPPacket}.
     * This method should be called from the bluetooth data receipt method/callback from the
     * {@link IV1connectionWrapper} implementation.
     *
     * @param packet {@link ESPPacket} to process.
     */
    protected void processESPPacket(ESPPacket packet) {
        // Do nothing if we're not connected
        if (!isConnected()) {
            return;
        }
        // Whenever we get ESP data we wanna reschedule the no data timeout.
        mHandler.removeMessages(WHAT_NO_DATA);
        mHandler.sendEmptyMessageDelayed(WHAT_NO_DATA, mDataTimeoutMillis);

        @PacketId.PacketID int packetId = packet.getPacketID();
        mLastV1Type = packet.getValentineType();
        // If this packet is a Information display data use it to determine the V1's type.
        if(packetId == PacketId.INFDISPLAYDATA) {
            processDisplayData((InfDisplayData) packet);
        }
        // If the packet is for us, we want to check check if for some library maintenance reasons,
        // then add it to the input queue.
        if(packet.isPacketForMe()) {
            // If we've received a V1 busy packet we need to store the busy ID's in a queue so we
            // know that we know not to send another packet.
            if(packetId == PacketId.INFV1BUSY) {
                // The V1 busy packet's payload minus the checksum contains the packet
                // ID's of busy packets.
                mDisplayCount = V1_BUSY_RESET_VAL;
                for (byte busyID : packet.getPayloadData()) {
                    mBusyPacketIDs.add(Integer.valueOf(busyID));
                }
            }
            else if(packetId == PacketId.RESPREQUESTNOTPROCESSED ||
                    packetId == PacketId.RESPUNSUPPORTEDPACKET ||
                    packetId == PacketId.RESPDATAERROR) {
                mResponseProcessor.onFailurePacket(packet);
            }

            mResponseProcessor.onPacketReceivedBlocking(packet, mBusyPacketIDs);

            ESPClientListener listener;
            synchronized (this) {
                listener = mESPListener;
            }
            if (listener != null) {
                if(packetId == PacketId.RESPALERTDATA) {
                    if (mAlertProcessor == null) {
                        mAlertProcessor = new AlertDataProcessor();
                    }
                    AlertData alert = (AlertData) packet.getResponseData();
                    // Attempt to construct an alert table using the received alert.
                    List<AlertData> table = mAlertProcessor.addAlert(alert);
                    if(table != null) {
                        listener.onAlertTableReceived(table);
                    }
                }
                else if(packetId == PacketId.INFDISPLAYDATA) {
                    if (listener != null) {
                        listener.onDisplayDataReceived((InfDisplayData) packet);
                    }
                }
                listener.onPacketReceived(packet);
            }
        }
    }

    /**
     * Perform processing on the provided {@link InfDisplayData}.
     *
     * @param displayData {@link InfDisplayData} to process.
     */
    protected void processDisplayData(InfDisplayData displayData) {
        final DeviceId deviceType;
        // Determine the V1 type for this information display data.
        if(displayData.isLegacyMode()) {
            deviceType = DeviceId.VALENTINE_ONE_LEGACY;
        }
        else if(displayData.getOrigin() == DeviceId.VALENTINE_ONE_NO_CHECKSUM) {
            deviceType = DeviceId.VALENTINE_ONE_NO_CHECKSUM;
        }
        else {
            deviceType = DeviceId.VALENTINE_ONE;
        }

        // If the v1 type is different than the last determined Valentine type we want to begin
        // special processing to make sure that V1 type has in fact really changed and not some
        // jitter on the line causing the V1 to report the an incorrect origin 'Id'.
        if(deviceType != getValentineType()) {
            synchronized (mRealValentineType) {
                // For every time the V1 type is the same as expected, increment the counter,
                // otherwise reset it back to zero and reset the expected variable.
                if(deviceType == mValentineTypeTmp){
                    mV1TypeSwitchCounter++;
                }
                else {
                    mV1TypeSwitchCounter = 1;
                    mValentineTypeTmp = deviceType;
                }
                // If the v1 type counter variable reaches our threshold, we've determined with a
                // reasonable amount of certainty that the V1's type has indeed changed.
                if(mV1TypeSwitchCounter == V1_TYPE_SWITCH_THRESHOLD) {
                    // Reset the counter and tmp field.
                    mV1TypeSwitchCounter = 0;
                    mValentineTypeTmp = DeviceId.UNKNOWN_DEVICE;
                    // Store the V1 type, then indicate to the queue the V1's type.
                    setValentineType(deviceType);
                }
            }
        }
        else {
            synchronized (mRealValentineType) {
                mV1TypeSwitchCounter = 0;
                mValentineTypeTmp = DeviceId.UNKNOWN_DEVICE;
            }
        }

        // Handle processing whether the V1 is busy...
        if (mDisplayCount < BUSY_INCREMENT_THRESH) {
            mDisplayCount++;
        }
        // If the display count is equal to the V1 not busy threshold, clear the busy packet id list and
        // push the packets in the send after busy list into the output queue.
        if (mDisplayCount == V1_NOT_BUSY_THRESH) {
            // We've determined that that V1 is no longer busy so we should clear the busy queue.
            mBusyPacketIDs.clear();
        }
        if(getValentineType() != DeviceId.UNKNOWN_DEVICE) {
            // Use the TS hold-off bit inside of the display data to allow packet.
            mTSHoldoff.set(displayData.isTSHoldOff());
        }
    }
    //endregion

    /**
     * Stops the writer thread if running.
     */
    private void stopWriter() {
        if (mWriterThread != null) {
            mWriterThread.interrupt();
            mWriterThread = null;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case WHAT_CONNECTION_EVENT:
                // The connection event enum ordinal is stored in the messages arg1 field
                final ConnectionEvent event = ConnectionEvent.values()[msg.arg1];
                performConnectionEvent(event, msg.arg2 == 1);
                return true;
            case WHAT_NO_DATA:
                performNoDataCallback();
                return true;
            case WHAT_MALFORMED_DATA:
                performBadDataCallback((String) msg.obj);
                return true;
        }
        return false;
    }

    /**
     * Blocks until there is an {@link ESPRequest} available.
     * @return
     * @throws InterruptedException
     */
    private ESPRequest getNextRequestBlocking() throws InterruptedException {
        synchronized (mRequestQueue) {
            // We wanna block as long as the packet queue is empty.
            while(mRequestQueue.isEmpty()) {
                mRequestQueue.wait();
            }
            return mRequestQueue.remove(0);
        }
    }

    /**
     *
     * @return
     */
    private ESPRequest getV1cBoundPacket() {
        synchronized (mRequestQueue) {
            for (int i = 0, size = mRequestQueue.size(); i < size; i++) {
                ESPRequest request = mRequestQueue.get(i);
                // Any packet whose destination and origin values match, they are destined to a V1c device.
                if(request.packet.getDestination() == request.packet.getOrigin()) {
                    return mRequestQueue.remove(i);
                }
            }
        }
        return null;
    }

    /**
     * Indicates if the specified ESPPacket should be sent.
     * @param packet    The packet to check for transmission.
     * @return  True if the packet should be sent otherwsise false.
     */
    private boolean canSendPacket(ESPPacket packet) {
        if(getValentineType() == DeviceId.VALENTINE_ONE_LEGACY && mProtectLegacy) {
            @PacketId.PacketID int id = packet.getPacketID();
            // If the packet is a version packet and the origin and destination are the same
            // or if the packet is mute on packet and the origin and destionation are different values return true.
            if((id == PacketId.REQVERSION && packet.isSameDestinationAsOrigin()) ||
                    // If this packet is a mute packet, make sure the destination and origin aren't the same.
                    (id == PacketId.REQMUTEON && !packet.isSameDestinationAsOrigin())) {
                return true;
            }
            else {
                ESPLogger.i(LOG_TAG, String.format("Ignoring version packet to %s because the ESPLibrary is in Legacy Mode.", PacketId.getNameForPacketIdentifier(packet.getPacketID())));
                return false;
            }
        }
        // If the device is not in legacy mode, always allow sending.
        return true;
    }

    /**
     * Invoked when an ESPPacket has successfully been written.
     *
     * @param packet the written packet
     */
    protected void onPacketWritten(ESPPacket packet) {
        if (mUseEchoQ) {
            // Add the ESPPacket to the echo queue as soon as possible so we
            // correctly listen for it's receipt.
            addToEchoQueue(packet);
        }
        synchronized (this) {
            if (mWriteListener != null) {
                mWriteListener.onPacketWritten(packet);
            }
        }
    }

    /**
     * Invoked when a {@link ESPPacket} failed to send.
     *
     * @param packet the packet that failed to send
     */
    protected void onPacketWriteFailed(ESPPacket packet) {
        ESPLogger.d(LOG_TAG, String.format("Failed to write packet Id: %02X", packet.getPacketID()));
        // Remove the packet from the echo queue
        removePacketFormEchoQueue(packet);
        final ResponseHandler respHndlr = mResponseProcessor.removeResponseHandlerForPacket(packet);
        // Remove queued request that has the same response expector
        synchronized (mRequestQueue) {
            for (int i = mRequestQueue.size() - 1; i >= 0; i--) {
                final ESPRequest espRequest = mRequestQueue.get(i);
                if (espRequest.respHandler == respHndlr) {
                    mRequestQueue.remove(i);
                }
            }
        }

        if (respHndlr != null) {
            if (respHndlr.failureCallback != null) {
                respHndlr.failureCallback.onFailure("BTError: Failed to send ESPPacket");
            }
        }
    }
    //region ECHO methods.

    /**
     * Removes the specified packet from the Echo queue
     * @param packet Packet to be removed
     */
    protected void removePacketFormEchoQueue(ESPPacket packet) {
        synchronized (mEchoQueue) {
            for (int i = mEchoQueue.size() - 1; i >= 0; i--) {
                mEchoQueue.remove(packet);
                return;
            }
        }
    }

    /**
     * Checks if packet is an echo.
     *
     * @param packet Packet to check
     *
     * @return True if echo
     */
    protected boolean checkForEchos(ESPPacket packet) {
        boolean retResult = false;
        synchronized (mEchoQueue) {
            for (int i = mEchoQueue.size() - 1; i >= 0; i--) {
                ESPPacket testPacket = mEchoQueue.get(i);
                if(packet.getPacketID() == PacketId.RESPREQUESTNOTPROCESSED) {
                    @PacketId.PacketID int notProcessedPacketId = ((ResponseRequestNotProcessed) packet).getUnprocesedPacketId();
                    if(testPacket.getPacketID() == notProcessedPacketId) {
                        ESPLogger.i(LOG_TAG, "Handling packet not processed as an echo");
                        mEchoQueue.remove(i);
                        retResult = false;
                        break;
                    }
                }
                else {
                    // Normal usecase, if the packet is inside of the echo list, remove it.
                    if(packet.equals(testPacket)) {
                        ESPLogger.i(LOG_TAG, "Removing echo packet #" + i);
                        mEchoQueue.remove(i);
                        retResult = true;
                        break;
                    }
                    else {
                        // This is a special use case,
                        // If the packet was a version packet to the V1connectionWrapper, the packet will not be echoed
                        // back so we should check if the new packet is the resp
                        if(testPacket.getDestination() == testPacket.getOrigin()) {
                            if(packet.getDestination() == packet.getOrigin()) {
                                if(packet.getPacketID() == PacketId.RESPVERSION
                                        && testPacket.getPacketID() == PacketId.REQVERSION) {
                                    // If the packet is indeed a version response set the index and break.
                                    mEchoQueue.remove(i);
                                    retResult = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // Purge possibly expired packets.
            removeExpiredPackets();
            return retResult;
        }
    }

    /**
     * Enables packeting echoing in the library.
     *
     * @param enabled True to enable packet echoing
     */
    protected void enabledEchoing(boolean enabled) {
        synchronized (mEchoQueue) {
            mUseEchoQ = enabled;
        }
    }

    /**
     * Add's an ESP packet to the echo queue.
     * @param packet The ESP packet to be added.
     */
    protected void addToEchoQueue(ESPPacket packet) {
        if (!mUseEchoQ) {
            return;
        }
        // Request Mute packets will not get echoed back by the V1connectionWrapper.
        if(packet.getPacketID() == PacketId.REQMUTEON) {
            return;
        }

        ESPLogger.i(LOG_TAG, String.format("Adding %s (%d), packet destined to %s to echo queue", PacketId.getNameForPacketIdentifier(packet.getPacketID()), packet.getPacketID(), packet.getDestination().toString()));

        synchronized (mEchoQueue) {
            // Add the ESPPacket into the echo packet list.
            mEchoQueue.add(packet);
        }
    }

    /**
     * Clears the expired {@link ESPPacket ESPPackets} inside of the echo list.
     *
     * <p><b>NOTE:</b> This method will update the packets transmission time if the TS Holdoff flag is set.</p>
     */
    protected void removeExpiredPackets() {
        synchronized (mEchoQueue) {
            // Don't do work we don't have too.
            if(mEchoQueue.isEmpty()) {
                return;
            }

            long minKeepTime = System.currentTimeMillis() - ECHO_TIMEOUT;
            for (int i = mEchoQueue.size() - 1; i >= 0; i--) {
                ESPPacket packet = mEchoQueue.get(i);
                if(packet.getTransmissionTime() < minKeepTime) {
                    // If the time slicing holdoff is enabled, reset the last sent time and don't
                    // remove the packet from the echo list.
                    if(mTSHoldoff.get()) {
                        packet.setTransmissionTime(System.currentTimeMillis());
                        continue;
                    }
                    ESPLogger.e(LOG_TAG, String.format("Purging expired %s packet (%d) destined to %s",
                            PacketId.getNameForPacketIdentifier(packet.getPacketID()), packet.getPacketID(),
                            packet.getDestination().toString()));
                    mEchoQueue.remove(i);
                }
            }
        }
    }

    /**
     * Indicates if the Echo queue is full. Max 5 packets.
     *
     * Note: if echoing is disabled, this always returns false.
     *
     * @return True if the echo queue is full.
     */
    private boolean isEchoQueueFull() {
        if(!mUseEchoQ) {
            return false;
        }
        // Wait while the echo wait queue is full to avoid the packet not processed from the V1connectionWrapper due to
        // a full packet buffer in the hardware.
        synchronized (mEchoQueue) {
            return mEchoQueue.size() >= MAXPACKETECHOS;
        }
    }

    /**
     * Empties the Echo Queue.
     */
    private void clearEchoQueue() {
        synchronized (mEchoQueue) {
            mEchoQueue.clear();
        }
    }
    //endregion

    /**
     * Send a message to perform the bad data callback based on the provided byte array.
     * @param data  The bad ESP data.
     */
    protected void malformedData(byte[] data) {
        String badData;
        if(data.length == 0) {
            badData = "empty packet";
        }
        else {
            badData = BTUtil.toHexString(data);
        }
        ESPLogger.d(LOG_TAG, badData);
        Message.obtain(mHandler, WHAT_MALFORMED_DATA, String.format("Malformed packet: %s", badData)).sendToTarget();
    }

    /**
     * Set if data can be written or not.
     * @param canWrite
     */
    public abstract void setCanPerformBTWrite(boolean canWrite);

    /**
     * Indicates if data can be written.
     * @return True if data can be written.
     */
    public abstract boolean canPerformBTWrite();

    /**
     * Writes provided byte data to the implementation's endpoint.
     * @param data  Byte array of data to send
     */
    protected abstract boolean write(byte [] data);

    /**
     * Indicates if wrapper has determined the V1's type... We know if the V1 has checksum, or is
     * Legacy.
     *
     * @return True if V1 type has been determined.
     *
     * @see DeviceId#VALENTINE_ONE_LEGACY
     * @see DeviceId#VALENTINE_ONE_NO_CHECKSUM
     * @see DeviceId#VALENTINE_ONE
     */
    public boolean determinedV1Type() {
        return getValentineType() != DeviceId.UNKNOWN_DEVICE;
    }

    @Override
    public void run() {
        final Thread thread = Thread.currentThread();
        // Run until the writer thread gets interrupted
        while(!thread.isInterrupted()) {
            // We don't wanna try writing a single thing until we've determined the V1 type.
            if(determinedV1Type()) {
                ESPRequest sendReq = null;
                // If Time slicing is disabled, we wanna loop until it's not
                while(mTSHoldoff.get()) {
                    if(thread.isInterrupted()) {
                        // When the writer thread is interrupted, we wanna return because it's
                        // shutting down
                        return;
                    }
                    // Normally we don't wanna do anything because we aren't allowed to send data
                    // unless it's to a V1c, so check the request queue for a V1c bound packet.
                    sendReq = getV1cBoundPacket();
                }
                if(sendReq == null) {
                    // We wanna ask the queue for the next packet to send
                    try {
                        sendReq = getNextRequestBlocking();
                    } catch (InterruptedException e) {
                        // When the writer thread is interrupted, we wanna return because it's
                        // shutting down
                        break;
                    }
                }
                // If we can't perform a BT write or the echo queue is full, wait until we can send
                while(!canPerformBTWrite() || isEchoQueueFull()) {
                    if(thread.isInterrupted()) {
                        // When the writer thread is interrupted, we wanna return because it's
                        // shutting down
                        return;
                    }
                    if (sendReq != null) {
                        // Requeue the request and wait until we are allowed to send BT data...
                        // We do this so that we don't send a potentially expired req.. This will
                        // allow the timeout mechanism to properly expire unset request... Place it
                        // at the head so it's the next to be sent.
                        addRequest(sendReq, true);
                        sendReq = null;
                    }
                }

                if (sendReq != null) {
                    final ESPPacket packet = sendReq.packet;
                    if(canSendPacket(packet)) {
                        packet.setV1Type(getValentineType());
                        // If the packet has a response respHandler add it to the response handlers
                        // list.
                        ResponseHandler respHandler = sendReq.respHandler;
                        if(respHandler != null) {
                            // Store the packet registered
                            respHandler.addSentRequest(sendReq);
                            mResponseProcessor.addResponse(respHandler);
                        }
                        // Immediately before writing the packets byte data we need to capture the
                        // most recent time.
                        packet.setTransmissionTime(System.currentTimeMillis());
                        // Attempt to write the packet byte data.
                        if(!write(packet.getPacketData())) {
                            onPacketWriteFailed(packet);
                            continue;
                        }
                        onPacketWritten(packet);
                    }
                }
            }
        }
    }
}
