package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.esplibrary.client.ESPClientListener;
import com.esplibrary.client.ESPRequest;
import com.esplibrary.client.ResponseHandler;
import com.esplibrary.client.ResponseProcessor;
import com.esplibrary.client.callbacks.NoDataListener;
import com.esplibrary.client.callbacks.NotificationListener;
import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.PacketFactory;
import com.esplibrary.packets.PacketUtils;
import com.esplibrary.packets.response.ResponseBatteryVoltage;
import com.esplibrary.packets.response.ResponseMaxSweepIndex;
import com.esplibrary.packets.response.ResponseSAVVYStatus;
import com.esplibrary.packets.response.ResponseSerialNumber;
import com.esplibrary.packets.response.ResponseSweepDefinition;
import com.esplibrary.packets.response.ResponseSweepSections;
import com.esplibrary.packets.response.ResponseUserBytes;
import com.esplibrary.packets.response.ResponseVehicleSpeed;
import com.esplibrary.packets.response.ResponseVersion;
import com.esplibrary.utilities.ESPLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class V1connectionDemoWrapper extends V1connectionBaseWrapper {

    protected final static String DEMO_FILE_SPACE_CHARACTER = " ";

    private final static String LOG_TAG = "V1cDemoWrpr";

    HashMap<DeviceId, ResponseVersion> mVersions;
    HashMap<DeviceId, ResponseSerialNumber> mSerialNumbers;

    private ResponseSAVVYStatus mSAVVYStatus;
    private ResponseUserBytes mV1UserBytes;
    private List<ResponseSweepSections> mSweepSections;
    private List<ResponseSweepDefinition> mSweepDefinitions;
    private ResponseMaxSweepIndex mMaxSweepIndex;
    private ResponseBatteryVoltage mVoltage;
    private ResponseVehicleSpeed mVehicleSpeed;

    private String mDemoData;

    private boolean mRepeat = true;

    private Thread mThread;
    /**
     * List of ESP packet ID's that the V1 is busy processing.
     */
    private final List<Integer> EMPTY = new ArrayList<>();

    /**
     *
     */
    private NotificationListener mNotificationCB;

    /**
     * Constructs a demo {@link IV1connectionWrapper} instance.
     * @param listener The {@link ESPClientListener callback} that will be invoked when ESP data is
     *                 received.
     * @param factory           {@link PacketFactory} used to construct ESP packets.
     * @param timeoutInMillis   Number of milliseconds before the
     * {@link NoDataListener#onNoDataDetected()} is invoked.
     */
    public V1connectionDemoWrapper(@Nullable ESPClientListener listener, PacketFactory factory, long timeoutInMillis) {
        super(listener, factory, timeoutInMillis);

        mVersions = new HashMap<>();
        mSerialNumbers = new HashMap<>();
        mSweepSections = new ArrayList<>();
        mSweepDefinitions = new ArrayList<>();
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.Demo;
    }

    @Override
    public void setNotificationListener(NotificationListener listener) {
        synchronized (this) {
            mNotificationCB = listener;
        }
    }

    @Override
    public void clearNotificationListener() {
        setNotificationListener(null);
    }

    @Override
    public void setDemoData(@NonNull String demoData) {
        if (mDemoData == null || mDemoData != demoData) {
            mDemoData = demoData;
        }
    }

    @Override
    public void repeatDemoMode(boolean repeat) {
        mRepeat = repeat;
    }

    @Override
    public void connect(Context ctx, @Nullable BluetoothDevice v1Device) {
        super.connect(ctx, v1Device);
        // Check to see if we are already connecting or connected and return.
        if(mState.get() == STATE_CONNECTING || mState.get() == STATE_CONNECTED) {
            return;
        }

        if (mDemoData == null) {
            ESPLogger.e(LOG_TAG, "An invalid demo data type was passed into. Only string types are supported");
            mState.set(STATE_DISCONNECTED);
            Message.obtain(getHandler(), WHAT_CONNECTION_EVENT, ConnectionEvent.ConnectionFailed.ordinal(), 1).sendToTarget();
            return;
        }
        // Set the state to connecting.
        mState.set(STATE_CONNECTING);
        Message.obtain(getHandler(), WHAT_CONNECTION_EVENT, ConnectionEvent.Connecting.ordinal(), 1).sendToTarget();
        mThread = new Thread(new DemoESPRunnable());
        mThread.start();
    }

    @Override
    public void disconnect(boolean notifyDisconnection) {
        if (mThread != null) {
            mThread.interrupt();
        }
        // report disconnections immediately
        onDisconnected();
    }

    @Override
    public boolean canPerformBTWrite() {
        return true;
    }

    @Override
    protected boolean write(byte[] data) {
        return true;
    }

    @Override
    public void setCanPerformBTWrite(boolean canWrite) {/*INTENTIONALLY LEFT BLANK*/}

    @Override
    public void addRequest(ESPRequest request) {
        // While in demo mode, packet do not timeout.
        ESPPacket req = request.packet;
        ResponseHandler respHandler = request.respHandler;
        if (respHandler != null) {
            switch (req.getPacketID()) {
                case PacketId.REQVERSION: {
                    DeviceId origin = req.getDestination();
                    ResponseVersion responseVersion = mVersions.get(origin);
                    if(responseVersion != null) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(responseVersion);
                        }
                        return;
                    }
                    break;
                }
                case PacketId.REQSERIALNUMBER: {
                    DeviceId origin = req.getDestination();
                    ResponseSerialNumber responseSerialNumber = mSerialNumbers.get(origin);
                    if(responseSerialNumber != null) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(responseSerialNumber);
                        }
                        return;
                    }
                    break;
                }
                case PacketId.REQUSERBYTES:
                    if (mV1UserBytes != null && req.isForV1()) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(mV1UserBytes);
                        }
                        return;
                    }
                    break;
                case PacketId.REQMAXSWEEPINDEX:
                    if (mMaxSweepIndex != null) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(mMaxSweepIndex);
                        }
                        return;
                    }
                    break;
                case PacketId.REQSWEEPSECTIONS:
                    for (int i = 0; i < mSweepSections.size(); i++) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(mSweepSections.get(i));
                        }
                    }
                    // If we've received all sweep sections return, so we don't queue the packet
                    if(mSweepSections.size() == 2) {
                        return;
                    }
                    break;
                case PacketId.REQBATTERYVOLTAGE:
                    if (mVoltage != null) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(mVoltage);
                        }
                    }
                    break;
                case PacketId.REQSAVVYSTATUS:
                    if (mSAVVYStatus != null) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(mSAVVYStatus);
                        }
                    }
                    break;
                case PacketId.REQVEHICLESPEED:
                    if (mVehicleSpeed != null && respHandler != null) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(mVehicleSpeed);
                        }
                    }
                    break;
                case PacketId.REQALLSWEEPDEFINITIONS:
                case PacketId.REQDEFAULTSWEEPS:
                    for (int i = 0; i < mSweepDefinitions.size(); i++) {
                        if (respHandler.successCallback != null) {
                            respHandler.successCallback.onPacketReceived(mSweepDefinitions.get(i));
                        }
                    }
                    // If we've received all sweep return return, so we don't queue the packet
                    if(mSweepDefinitions.size() == 6) {
                        return;
                    }
                    break;
            }
            // Add the packet to the response handler and add then add it to the response processor
            // object
            respHandler.addSentRequest(request);
            ResponseProcessor processor = getResponseProcessor();
            processor.addResponse(respHandler);
        }
    }

    /**
     * Runnable used for processing ESP data.
     */
    private class DemoESPRunnable implements Runnable {

        protected static final String DEMO_FILE_DOUBLE_COMMENT_CHARACTER = "//";
        protected static final char DEMO_FILE_BEGIN_COMMENT_CHARACTER = '<';
        protected static final String DEMO_FILE_SEMI_COLON_CHARACTER = ":";
        protected static final String DEMO_FILE_END_COMMENT_CHARACTER = ">";

        @Override
        public void run() {
            try {
                // Force the V1 type to a V1 with checksum.
                setValentineType(DeviceId.VALENTINE_ONE);
                // Tell the Library we've made a connection.
                onConnected();
                boolean process = true;
                int step = 0;
                do {
                    int endOfLineLoc = mDemoData.indexOf("\n", step);
                    // Get the whole line of byte data and store it into a string.
                    String currentLine = mDemoData.substring(step, endOfLineLoc);
                    // Offset the step variable by location of the first new line character.
                    step = endOfLineLoc + 1;

                    String specialTest =  currentLine.substring(0, 2);
                    if(specialTest.equals(DEMO_FILE_DOUBLE_COMMENT_CHARACTER)) {
                        /*INTENTIONALLY LEFT BLANK*/
                    }
                    else if(specialTest.charAt(0) == DEMO_FILE_BEGIN_COMMENT_CHARACTER) {
                        int startLoc = currentLine.indexOf(DEMO_FILE_SEMI_COLON_CHARACTER);
                        int endLoc = currentLine.indexOf(DEMO_FILE_END_COMMENT_CHARACTER);
                        // Send the embedded message demo data, to the helper thread to be delivered to any registered
                        // notification callbacks.
                        handleNoticationMessage(currentLine.substring(startLoc + 1, endLoc));
                    }
                    else {
                        byte[] bytes = convertStringToByteArray(currentLine);
                        mBuffer.addAll(bytes);
                        ESPPacket newPacket = PacketUtils.makeFromBufferSPP(mFactory, mBuffer, DeviceId.VALENTINE_ONE);
                        if (newPacket != null) {
                            processDemoData(newPacket);
                        }
                        // To emulate data coming in from the bluetooth stack sleep for 68 milliseconds
                        Thread.sleep(68);
                    }
                    // If we have reached the end of the demo data ArrayList, loop back to the front.
                    if(step == mDemoData.length()) {
                        // Repeating is enabled so set step back to zero to begin re-reading the demo data.
                        if (mRepeat) {
                            step = 0;
                        }
                        else {
                            process = false;
                        }
                    }
                }
                while(process && !Thread.currentThread().isInterrupted());
            } catch (InterruptedException e) {}
        }

        /**
         * Dispatches notifMessage to the registered {@link NotificationListener} on the Main (UI) thread
         * @param notifMessage notification message to be dispatched
         */
        private void handleNoticationMessage(String notifMessage) {
            if (!isConnected()) {
                return;
            }
            getHandler().post(()-> {
                synchronized (this) {
                    if (mNotificationCB != null) {
                        mNotificationCB.onNotificationReceived(notifMessage);
                    }
                }
            });
        }

        /**
         * Processes packet
         *
         * @param packet ESPPacket received from the demo data.
         */
        private void processDemoData(ESPPacket packet) {
            // If we're not connected stop procesing ESP data.
            if (!isConnected()) {
                return;
            }

            int packetID = packet.getPacketID();
            if(packetID == PacketId.INFDISPLAYDATA
                    || packetID == PacketId.RESPALERTDATA) {
                processESPPacket(packet);
            }
            else {
                ResponseProcessor responseProcessor = getResponseProcessor();
                switch(packetID) {
                    case PacketId.RESPVERSION:
                        mVersions.put(packet.getOrigin(), (ResponseVersion) packet);
                        // Whenever we receive a version we wanna see if we have any response
                        // handlers to complete.
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPSERIALNUMBER:
                        mSerialNumbers.put(packet.getOrigin(), (ResponseSerialNumber) packet);
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPSAVVYSTATUS:
                        mSAVVYStatus = (ResponseSAVVYStatus) packet;
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPSWEEPSECTIONS:
                        if(mSweepSections.size() < 2) {
                            mSweepSections.add((ResponseSweepSections) packet);
                        }
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPSWEEPDEFINITION:
                        if(mSweepDefinitions.size() < 6) {
                            mSweepDefinitions.add((ResponseSweepDefinition) packet);
                        }
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPMAXSWEEPINDEX:
                        mMaxSweepIndex = (ResponseMaxSweepIndex) packet;
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPBATTERYVOLTAGE:
                        mVoltage = (ResponseBatteryVoltage) packet;
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPVEHICLESPEED:
                        mVehicleSpeed = (ResponseVehicleSpeed) packet;
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                    case PacketId.RESPUSERBYTES:
                        mV1UserBytes = (ResponseUserBytes) packet;
                        responseProcessor.onPacketReceivedBlocking(packet, EMPTY);
                        break;
                }
            }
        }

        /**
         * Helper function for converting a hex string into a byte array
         *
         * @param data String containing hex data
         *
         * @return byte representation of the data
         */
        public final byte [] convertStringToByteArray(String data) {
            String [] splitData = data.split(DEMO_FILE_SPACE_CHARACTER);
            // Create a byte array the size of the split demo data array.
            byte [] espData = new byte [splitData.length];
            for(int i = 0, size = splitData.length; i < size; i++) {
                String chr = splitData[i];
                // Convert the string byte value to an actual byte.
                espData[i] = (byte) ((Character.digit(chr.charAt(0), 16) << 4) + (Character.digit(chr.charAt(1), 16)));
            }
            return espData;
        }
    }
}
