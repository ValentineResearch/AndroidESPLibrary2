package com.esplibrary.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import com.esplibrary.client.ESPClientListener;
import com.esplibrary.client.callbacks.NoDataListener;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketFactory;
import com.esplibrary.packets.PacketUtils;
import com.esplibrary.utilities.ESPLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class V1connectionWrapper extends V1connectionBaseWrapper {

    private final static String LOG_TAG = "V1cWrpr";
    /**
     *
     */
    private final static String V_1_C_CONNECT_THREAD = "V1cConnectThread";
    /**
     *
     */
    private final static String V_1_C_READER_THREAD = "V1C_ReaderThread";

    /**
     *
     */
    private BluetoothSocket mBTSocket;
    /**
     * Bluetooth Input stream for reading ESP data
     */
    private InputStream mInput;
    /**
     * Bluetooth Output stream for writing ESP data
     */
    private OutputStream mOutput;
    /**
     * Background thread for reading data from the input stream.
     */
    private Thread mReaderThread;

    /**
     * Constructs a Bluetooth SPP {@link IV1connectionWrapper} instance.
     * @param listener The {@link ESPClientListener callback} that will be invoked when ESP data is
     *                 received.
     * @param factory           {@link PacketFactory} used to construct ESP packets.
     * @param timeoutInMillis   Number of milliseconds before the
     * {@link NoDataListener#onNoDataDetected()} is invoked.
     */
    public V1connectionWrapper(@Nullable ESPClientListener listener, PacketFactory factory, long timeoutInMillis) {
        super(listener, factory, timeoutInMillis);
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.SPP;
    }

    /**
     * Runnable used for reading ESP data from an Android V1connectionWrapper (SPP).
     */
    private class ReaderRunnable implements Runnable {

        private static final int STREAM_BUFFER_SIZE = 1024;
        private final byte [] mData = new byte [STREAM_BUFFER_SIZE];

        ReaderRunnable() {
        }

        @Override
        public void run() {
            Thread thread = Thread.currentThread();
            while(!thread.isInterrupted()) {
                try {
                    int readSize = mInput.read(mData);
                    mBuffer.addSubsetOfBytes(mData, readSize);
                    ESPPacket packet;
                    // Loop until we read all available packets inside of the buffer.
                    do {
                        // Attempt to convert the byte data into an esp packet.
                        packet = PacketUtils.makeFromBufferSPP(mFactory, mBuffer, mLastV1Type);
                        // If packet isn't null, we should add it to the packet list.
                        if(packet != null) {
                            // Check the packets for echos, skip if this is an echo packet.
                            if(checkForEchos(packet)) {
                                continue;
                            }
                            processESPPacket(packet);
                        }
                    }
                    while(packet != null);
                } catch (IOException e) {
                    if (isConnecting()) {
                        onConnectionFailed();
                    }
                    else if(isConnected()) {
                        onConnectionLost();
                    }
                    return;
                }
            }
        }
    }

    @Override
    protected void onConnected() {
        super.onConnected();
        // Start the reader thread to indefinitely read data from 'mInput'
        // (BluetoothInputStream).
        mReaderThread = new Thread(new ReaderRunnable(), V_1_C_READER_THREAD);
        mReaderThread.start();
    }

    @Override
    public void connect(Context ctx, BluetoothDevice v1Device) {
        super.connect(ctx, v1Device);
        // Check to see if we are already connecting or connected and return.
        if(mState.get() == STATE_CONNECTING || mState.get() == STATE_CONNECTED) {
            return;
        }
        // Set the state to connecting.
        if(mState.compareAndSet(STATE_DISCONNECTED, STATE_CONNECTING)) {
            Message.obtain(getHandler(), WHAT_CONNECTION_EVENT, ConnectionEvent.Connecting.ordinal(), 0).sendToTarget();
            new Thread(() -> performConnection(v1Device), V_1_C_CONNECT_THREAD).start();
        }
        else{
            mState.set(STATE_DISCONNECTED);
            // We are in an unexpected state so indicate the connection failed.
            Message.obtain(getHandler(), WHAT_CONNECTION_EVENT, ConnectionEvent.ConnectionFailed.ordinal(), 0).sendToTarget();
        }
    }

    /**
     *
     * @param v1Device
     */
    @SuppressLint("MissingPermission")
    private void performConnection(BluetoothDevice v1Device) {
        ESPLogger.d(LOG_TAG, "Attempting to connect to " + v1Device.getAddress());
        try {
            // Create a RFCOMM Socket with device, on the the SPP service channel.
            mBTSocket = v1Device.createRfcommSocketToServiceRecord(BTUtil.SPP_UUID);
            mBTSocket.connect();
            // Store a reference input and out streams.
            mInput = mBTSocket.getInputStream();
            mOutput = mBTSocket.getOutputStream();
            onConnected();
        } catch (IOException e) {
            // We only wanna perform the secondary connection process if we are still connecting.
            if(mState.get() == STATE_CONNECTING) {
                ESPLogger.w(LOG_TAG, "Failed to make a connection to " + v1Device.getAddress());
                performAttempt2(v1Device);
            }
        }
    }

    /**
     *
     * @param v1Device
     */
    private void performAttempt2(BluetoothDevice v1Device) {
        ESPLogger.d(LOG_TAG, "Second attempt to connect to " + v1Device.getAddress());
        try {
            mBTSocket = createRfcommSocket(v1Device,1);
            // Connect will block until a connection has been made, or an exception
            // is thrown indicating that the connections failed.
            mBTSocket.connect();

            mInput = mBTSocket.getInputStream();
            mOutput = mBTSocket.getOutputStream();
            onConnected();
        }
        catch(Exception e) {
            // We only wanna indicate the connection failed if we are in the connecting state. Any other state means that we
            if(mState.get() == STATE_CONNECTING) {
                ESPLogger.w(LOG_TAG, String.format("Failed again to make a connection to %s", v1Device.getAddress()));
                Message.obtain(getHandler(), WHAT_CONNECTION_EVENT, ConnectionEvent.ConnectionFailed.ordinal(), 0).sendToTarget();
                // Transition the wrapper into the disconnect state but do not perform the disconnected callback.
                onDisconnected();
                return;
            }
        }
    }

    /**
     * Create an RFCOMM {@link BluetoothSocket} ready to start a secure
     * outgoing connection to this remote device on given channel.
     * <p>The remote device will be authenticated and communication on this
     * socket will be encrypted.
     * <p> Use this socket only if an authenticated socket link is possible.
     * Authentication refers to the authentication of the link key to
     * prevent man-in-the-middle type of attacks.
     * For example, for Bluetooth 2.1 devices, if any of the devices does not
     * have an input and output capability or just has the ability to
     * display a numeric key, a secure socket connection is not possible.
     * In such a case, use {#link createInsecureRfcommSocket}.
     * For more details, refer to the Security Model range 5.2 (vol 3) of
     * Bluetooth Core Specification version 2.1 + EDR.
     * <p>Use {@link BluetoothSocket#connect} to initiate the outgoing
     * connection.
     * <p>Valid RFCOMM channels are in range 1 to 30.
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH}
     *
     * @param channel RFCOMM channel to connect to
     * @return a RFCOMM BluetoothServerSocket ready for an outgoing connection
     * @throws IOException on error, for example Bluetooth not available, or insufficient
     * permissions
     * @hide
     */
    static BluetoothSocket createRfcommSocket(BluetoothDevice v1Device, int channel) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = v1Device.getClass().getMethod("createRfcommSocket", int.class);
        return (BluetoothSocket) m.invoke(v1Device, channel);
    }

    @Override
    public void disconnect(boolean notifyOnDisconnect) {
        if(mBTSocket == null) {
            super.disconnect(notifyOnDisconnect);
            return;
        }

        // Whenever disconnecting always stop the reader thread.
        if (mReaderThread != null) {
            mReaderThread.interrupt();
            mReaderThread = null;
        }
        try {
            // Close the BluetoothSocket. If we are in the middle of connecting calling close should cancel the connection attempt.
            if(mBTSocket != null) {
                mBTSocket.close();
            }
        } catch (IOException e) {}

        // Call super to invoke the disconnecting callback
        super.disconnect(notifyOnDisconnect);
        // Only notify we've disconnected if told to do so.
        if (notifyOnDisconnect) {
            new Handler(Looper.getMainLooper()).post(()-> onDisconnected());
        }
    }

    @Override
    public boolean canPerformBTWrite() {
        return true;
    }

    @Override
    public void setCanPerformBTWrite(boolean canWrite) {/*INTENTIONALLY LEFT BLANK*/}

    @Override
    protected boolean write(byte[] data) {
        if(mOutput != null) {
            // Delimit the data so that the V1connectionWrapper will respect the data.
            byte [] delimited = BTUtil.getV1cDelimitedData(data);
            // Escape the byte buffer.
            byte [] escapedData = BTUtil.escapeV1cData(delimited);
            try {
                mOutput.write(escapedData);
                // Flush the outputs buffer and force a write.
                mOutput.flush();
            }
            catch (IOException e) {
                // TODO handle notify the Library the write failed.
                return false;
            }
        }
        return true;
    }
}
