package com.esplibrary.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.esplibrary.bluetooth.BTUtil;
import com.esplibrary.bluetooth.ConnectionListener;
import com.esplibrary.bluetooth.ConnectionType;
import com.esplibrary.bluetooth.IV1connectionWrapper;
import com.esplibrary.bluetooth.RSSICallback;
import com.esplibrary.client.callbacks.ESPRequestListener;
import com.esplibrary.client.callbacks.ESPRequestedDataListener;
import com.esplibrary.client.callbacks.MalformedDataListener;
import com.esplibrary.client.callbacks.NoDataListener;
import com.esplibrary.client.callbacks.NotificationListener;
import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.constants.V1Mode;
import com.esplibrary.data.SAVVYStatus;
import com.esplibrary.data.SweepData;
import com.esplibrary.data.SweepDefinition;
import com.esplibrary.data.SweepSection;
import com.esplibrary.data.UserSettings;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.InfDisplayData;
import com.esplibrary.packets.request.RequestAbortAudioDelay;
import com.esplibrary.packets.request.RequestDisplayCurrentVolume;
import com.esplibrary.packets.request.RequestAllSweepDefinitions;
import com.esplibrary.packets.request.RequestBatteryVoltage;
import com.esplibrary.packets.request.RequestChangeMode;
import com.esplibrary.packets.request.RequestCurrentVolume;
import com.esplibrary.packets.request.RequestDefaultSweepDefinitions;
import com.esplibrary.packets.request.RequestDefaultSweeps;
import com.esplibrary.packets.request.RequestFactoryDefault;
import com.esplibrary.packets.request.RequestMaxSweepIndex;
import com.esplibrary.packets.request.RequestMuteOff;
import com.esplibrary.packets.request.RequestMuteOn;
import com.esplibrary.packets.request.RequestOverrideThumbwheel;
import com.esplibrary.packets.request.RequestSAVVYStatus;
import com.esplibrary.packets.request.RequestSavvyUnmuteEnable;
import com.esplibrary.packets.request.RequestSerialNumber;
import com.esplibrary.packets.request.RequestStartAlertData;
import com.esplibrary.packets.request.RequestStopAlertData;
import com.esplibrary.packets.request.RequestSweepSections;
import com.esplibrary.packets.request.RequestTurnOffMainDisplay;
import com.esplibrary.packets.request.RequestTurnOnMainDisplay;
import com.esplibrary.packets.request.RequestUserBytes;
import com.esplibrary.packets.request.RequestVehicleSpeed;
import com.esplibrary.packets.request.RequestVersion;
import com.esplibrary.packets.request.RequestWriteSweepDefinition;
import com.esplibrary.packets.request.RequestWriteUserBytes;
import com.esplibrary.packets.request.RequestWriteVolume;
import com.esplibrary.packets.response.ResponseAlertData;
import com.esplibrary.packets.response.ResponseBatteryVoltage;
import com.esplibrary.packets.response.ResponseCurrentVolume;
import com.esplibrary.packets.response.ResponseMaxSweepIndex;
import com.esplibrary.packets.response.ResponseSAVVYStatus;
import com.esplibrary.packets.response.ResponseSerialNumber;
import com.esplibrary.packets.response.ResponseSweepSections;
import com.esplibrary.packets.response.ResponseSweepWriteResult;
import com.esplibrary.packets.response.ResponseUserBytes;
import com.esplibrary.packets.response.ResponseVehicleSpeed;
import com.esplibrary.packets.response.ResponseVersion;
import com.esplibrary.utilities.ESPLogger;
import com.esplibrary.utilities.V1VersionInfo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concrete implementation of the ESP Client interface.
 */
public class ESPValentineClient implements IESPClient {

    private final static String LOG_TAG = "ESPValentineClient";

    private double mLastV1Version = 0.0d;
    /**
     * Underlying bluetooth connection.
     */
    private final IV1connectionWrapper mConnection;
    /**
     * Application context used for performing various actions in the library, such as connecting and scanning.
     */
    private final Context mAppCtx;

    public ESPValentineClient(Context appContext, IV1connectionWrapper connection) {
        mConnection = connection;
        mAppCtx = appContext.getApplicationContext();
    }

    /**
     * Returns the underlying V1connection wrapper impl.
     *
     * @return Underlying V1connection wrapper
     */
    protected IV1connectionWrapper getConnectionWrapper() {
        return mConnection;
    }

    @Override
    public boolean isLegacy() {
        // If the valentine type is equal to Legacy return true.
        return mConnection.getValentineType() == DeviceId.VALENTINE_ONE_LEGACY;
    }

    //region State methods
    @Override
    public int getDataTimeout() {
        return (int) (mConnection.getDataTimeout() / 1000);
    }

    @Override
    public void setDataTimeout(int timeoutInSeconds) {
        // We need to convert them timeout ins seconds to milliseconds
        mConnection.setDataTimeout(timeoutInSeconds * 1000);
    }

    @Override
    public long getDataTimeoutMillis() {
        return mConnection.getDataTimeout();
    }

    @Override
    public void setDataTimeout(long timeoutInSeconds) {
        mConnection.setDataTimeout(timeoutInSeconds);
    }

    @Override
    public void setDemoData(String demoData) {
        mConnection.setDemoData(demoData);
    }

    @Override
    public void repeatDemoData(boolean repeat) {
        mConnection.repeatDemoMode(repeat);
    }

    @Override
    public void protectLegacyMode(boolean protect) {
        mConnection.protectLegacyMode(protect);
    }
    //endregion

    //region V1 info methods
    @Override
    public DeviceId getValentineType() {
        return mConnection.getValentineType();
    }

    @Override
    public boolean areDefaultSweepDefinitionsAvailableForV1Version(double v1Version) {
        return V1VersionInfo.areDefaultSweepDefsAvailable(v1Version);
    }
    //endregion

    //region Callback Registration methods
    @Override
    public void setESPClientListener(ESPClientListener listener) {
        mConnection.setESPClientListener(listener);
    }

    @Override
    public void clearESPClientListener() {
        mConnection.clearESPClientListener();
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        mConnection.addConnectionListener(listener);
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        mConnection.removeConnectionListener(listener);
    }

    @Override
    public void setMalformedListener(MalformedDataListener listener) {
        mConnection.setMalformedListener(listener);
    }

    @Override
    public void clearMalformedListener() {
        mConnection.clearMalformedListener();
    }

    @Override
    public void setNoDataListener(NoDataListener listener) {
        mConnection.setNoDataListener(listener);
    }

    @Override
    public void clearNoDataListener() {
        mConnection.clearNoDataListener();
    }

    @Override
    public void setNotificationListener(NotificationListener listener) {
        mConnection.setNotificationListener(listener);
    }

    @Override
    public void clearNotificationListener() {
        mConnection.clearNotificationListener();
    }
    //endregion

    //region Connection methods
    @Override
    public boolean isConnected() {
        return mConnection.isConnected();
    }

    @Override
    public boolean isConnecting() {
        return mConnection.isConnecting();
    }

    @Override
    public boolean isDemoMode() {
        return mConnection.getConnectionType() == ConnectionType.Demo && mConnection.isConnected();
    }

    @Override
    public void disconnect() {
        mConnection.disconnect(true);
    }

    @Override
    public void disconnect(boolean notifyOnDisconnect) {
        mConnection.disconnect(notifyOnDisconnect);
    }

    @Override
    public boolean connect(BluetoothDevice v1Device, ConnectionType connType, @Nullable ConnectionListener listener) {
        // Return false if the device doesn't support BT.
        if(!BTUtil.isBluetoothSupported(mAppCtx)) {
            ESPLogger.e(LOG_TAG, "Bluetooth isn't supported by this device");
            return false;
        }
        // Return false if an invalid connection type is provided
        else if(connType == ConnectionType.Invalid) {
            ESPLogger.e(LOG_TAG, "Invalid connection type!");
            return false;
        }
        // Return false if the connection type of the BT device provided isn't supported
        else if (connType == ConnectionType.LE && !BTUtil.isLESupported(mAppCtx)) {
            ESPLogger.e(LOG_TAG, "Bluetooth LE is not supported!");
            return false;
        }
        // Return false if the provided connection type doesn't match the bluetooth wrapper
        else if(connType != mConnection.getConnectionType()) {
            ESPLogger.e(LOG_TAG, "Invalid connection type; Expected connection type = " + mConnection.getConnectionType());
            return false;
        }
        // Register the conn. event listener
        mConnection.addConnectionListener(listener);
        mConnection.connect(mAppCtx, v1Device);
        return true;
    }

    @Override
    public boolean connectDemo(String demoESPData, @Nullable ConnectionListener listener) {
        if(ConnectionType.Demo != mConnection.getConnectionType()) {
            ESPLogger.e(LOG_TAG, "ESPClient's connection type isn't suitable for demo mode");
            return false;
        }
        // Register the conn. event listener
        mConnection.addConnectionListener(listener);
        mConnection.setDemoData(demoESPData);
        mConnection.connect(mAppCtx, null);
        return true;
    }

    @Override
    public ConnectionType getConnectionType() {
        return mConnection.getConnectionType();
    }

    @Override
    public BluetoothDevice getConnectedDevice() {
        return mConnection.getDevice();
    }

    @Override
    public int getConnectedDeviceRSSI() {
        return mConnection.getCachedRSSI();
    }

    @Override
    public boolean readConnectedDeviceRSSI(@NonNull RSSICallback callback) {
        return mConnection.readRemoteRSSI(callback);
    }

    //endregion

    //region ESP Data Request methods
    @Override
    public void requestVersion(DeviceId deviceID, ESPRequestedDataListener<String> callback) {
        // Generate the Version packet.
        ESPPacket versionRequest = new RequestVersion(mConnection.getValentineType(), deviceID);
        // Create a response respHandler that will invoked the esp packet listener callback.
        ResponseHandler<ResponseVersion> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPVERSION);
        // Add a response callback to receive the device version.
        handler.successCallback = response -> {
            // Response should never be null but in case of bugs we wanna prevent a null pointer
            // exception.
            if (response != null) {
                final String version = response.getVersion();
                newVersionReceived (version);
                if (callback != null) {
                    // Make sure we got a valid version string.
                    if(version.length() != 7 || !Character.isAlphabetic(version.codePointAt(0))) {
                        callback.onDataReceived(null, "Received a bad version for " + deviceID.toString());
                        return true;
                    }
                    callback.onDataReceived(version, null);
                }
            }
            // Always return true so that the response respHandler is remove from the queue.
            return true;
        };
        // Add a failure callback.
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };

        // Queue the packet.
        mConnection.addRequest(new ESPRequest(versionRequest, handler));
    }

    @Override
    public void requestVersionAsDouble(DeviceId device, ESPRequestedDataListener<Double> callback) {
        requestVersion(device, (deviceVersion, error) -> {
            if ( error == null ){
                newVersionReceived (deviceVersion);
            }
            if (callback != null) {
                if (error != null) {
                    callback.onDataReceived(null, error);
                    return;
                }

                double version = ResponseVersion.getVersionDouble(deviceVersion);
                if (version == 0.0d) {
                    callback.onDataReceived(null, "Unable to parse version number for " + device.toString());
                    return;
                }

                callback.onDataReceived(version, null);
            }
        });
    }

    private void newVersionReceived (String version) {
        if (version.length() == 7 && version.codePointAt(0) == 'V') {
            // This is a V1 version, so store it for later use
            mLastV1Version = ResponseVersion.getVersionDouble (version);
        }
    }

    @Override
    public void requestSerialNumber(DeviceId deviceID, ESPRequestedDataListener<String> callback) {
        // Generate the Serial Number packet.
        ESPPacket serialNumberRequest = new RequestSerialNumber(mConnection.getValentineType(), deviceID);
        // Create a response respHandler that will invoked the esp packet listener callback.
        ResponseHandler<ResponseSerialNumber> handler = new ResponseHandler<>();
        // We want to listener for the serial number response callback
        handler.addResponseID(PacketId.RESPSERIALNUMBER);
        // Add a response callback to receive the serial number.
        handler.successCallback = response -> {
            if (response != null) {
                String serialNumber = response.getSerialNumber();
                if (callback != null) {
                    callback.onDataReceived(serialNumber, null);
                }
            }
            return true;
        };

        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };

        // Queue the packet.
        mConnection.addRequest(new ESPRequest(serialNumberRequest, handler));
    }

    @Override
    public void requestUserSettings(double v1Version, ESPRequestedDataListener<UserSettings> callback) {
        ESPPacket userSettingsRequest = new RequestUserBytes(mConnection.getValentineType());
        // Create a response respHandler that will invoked the esp packet listener callback.
        ResponseHandler<ResponseUserBytes> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPUSERBYTES);
        handler.successCallback = response -> {
            if (response != null) {
                if (callback != null) {
                    callback.onDataReceived(response.getUserSettings(v1Version), null);
                }
            }
            return true;
        };
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };
        // Queue the packet.
        mConnection.addRequest(new ESPRequest(userSettingsRequest, handler));
    }

    @Override
    public void requestUserBytes(ESPRequestedDataListener<byte[]> callback) {
        requestUserBytes (mConnection.getValentineType(), callback);
    }

    @Override
    public void requestUserBytes(DeviceId device, ESPRequestedDataListener<byte[]> callback) {
        ESPPacket userSettingsRequest = new RequestUserBytes(mConnection.getValentineType(), device);
        // Create a response respHandler that will invoked the esp packet listener callback.
        ResponseHandler<ResponseUserBytes> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPUSERBYTES);
        handler.successCallback = response -> {
            if (response != null) {
                if (callback != null) {
                    callback.onDataReceived(response.getUserBytes(), null);
                }
            }
            return true;
        };
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };
        // Queue the packet.
        mConnection.addRequest(new ESPRequest(userSettingsRequest, handler));
    }

    @Override
    public void requestWriteUserBytes(byte[] userBytes, ESPRequestListener callback) {
        requestWriteUserBytes(mConnection.getValentineType(), userBytes, callback);
    }

    @Override
    public void requestWriteUserBytes(DeviceId device, byte[] userBytes, ESPRequestListener callback) {
        RequestWriteUserBytes userBytesRequest = new RequestWriteUserBytes(mConnection.getValentineType(), device, userBytes);
        ResponseHandler handler = new ResponseHandler<>();
        handler.successCallback = packet -> {
            // Consider requesting the sent user bytes and performing a comparison on them with
            //  the provided userbytes.
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(userBytesRequest, handler));
    }

    @Override
    public void requestMaxSweepIndex(ESPRequestedDataListener<Integer> callback) {
        ESPPacket maxSweepRequest = new RequestMaxSweepIndex(mConnection.getValentineType());
        // Create a response respHandler that will invoked the esp packet listener callback.
        ResponseHandler<ResponseMaxSweepIndex> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPMAXSWEEPINDEX);
        handler.successCallback = response -> {
            if (response != null) {
                if (callback != null) {
                    callback.onDataReceived(response.getMaxSweepIndex(), null);
                }
            }
            return true;
        };
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };

        // Queue the packet.
        mConnection.addRequest(new ESPRequest(maxSweepRequest, handler));
    }

    @Override
    public void requestSweepSections(ESPRequestedDataListener<List<SweepSection>> callback) {
        ESPPacket sweepSectionRequest = new RequestSweepSections(mConnection.getValentineType());
        final SweepSectionProcessor processor = new SweepSectionProcessor();
        // Create a response respHandler that will invoked the esp packet listener callback.
        ResponseHandler<ResponseSweepSections> handler = new ResponseHandler<>();
        // Add the packet ID the response respHandler should be invoked for
        handler.addResponseID(PacketId.RESPSWEEPSECTIONS);
        handler.successCallback = response -> {
            // Add the sweep range responses to the processor until we are returned a list of
            // sweep sections.
            List<SweepSection> swpSections = processor.addSweepSection(response);
            if (swpSections != null) {
                if (callback != null) {
                    callback.onDataReceived(swpSections, null);
                }
                return true;
            }
            return false;
        };
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };
        // Queue the packet.
        mConnection.addRequest(new ESPRequest(sweepSectionRequest, handler));
    }

    @Override
    public void requestAllSweepDefinitions(ESPRequestedDataListener<List<SweepDefinition>> callback) {
        // We need the max sweep index before we can packet the Sweep definitions because we have
        // no way of knowing how many there could be.
        requestMaxSweepIndex((data, error) -> {
            if(error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
            else {
                requestSweepDefinitions(false, PacketId.RESPSWEEPDEFINITION, data, callback);
            }
        });
    }

    @Override
    public void requestDefaultSweeps(ESPRequestListener callback) {
        RequestDefaultSweeps defaultSweepsRequest = new RequestDefaultSweeps(mConnection.getValentineType());
        ResponseHandler<ESPPacket> handler = new ResponseHandler<>();
        // We have no particular response to look for, we just wanna make sure that we didn't
        // encounter an error sending the packet.
        handler.successCallback = response -> {
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        // Add failure callback that will invoked the onRequestCompleted callback indicating the
        // reason the packet failed.
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(defaultSweepsRequest, handler));
    }

    @Override
    public void requestDefaultSweepDefinitions(ESPRequestedDataListener<List<SweepDefinition>> callback) {
        // We need the max sweep index before we can packet the default Sweep definitions because we have
        // no way of knowing how many there could be.
        requestMaxSweepIndex((data, error) -> {
            if(error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
            else {
                requestSweepDefinitions(true, PacketId.RESPDEFAULTSWEEPDEFINITIONS,  data, callback);
            }
        });
    }

    /**
     * Helper method that can packet either the default {@link SweepDefinition sweeps} or normal {@link SweepDefinition sweeps}.
     *
     * @param responseID    Packet Id of the ESP response
     * @param maxSweepIndex maximum number of sweeps
     * @param callback  Callback that will be invoked once the sweep definitions are received
     */
    private void requestSweepDefinitions(boolean defaultSweeps, int responseID, int maxSweepIndex, ESPRequestedDataListener<List<SweepDefinition>> callback) {
        final SweepDefinitionProcessor sweepProcessor = new SweepDefinitionProcessor(maxSweepIndex + 1);
        ESPPacket sweepRequest;
        if(defaultSweeps) {
            sweepRequest = new RequestDefaultSweepDefinitions(mConnection.getValentineType());
        }
        else {
            sweepRequest = new RequestAllSweepDefinitions(mConnection.getValentineType());
        }
        // Create a response respHandler that will be invoked when the esp packet listener callback.
        ResponseHandler<ESPPacket> handler = new ResponseHandler<>();
        // Add the packet ID the response respHandler should be invoked for
        handler.addResponseID(responseID);
        handler.successCallback = response -> {
            if (response != null) {
                SweepDefinition def = (SweepDefinition) response.getResponseData();
                // Attempt to construct a list of sweep definitions from the attached V1.
                List<SweepDefinition> rxSweeps = sweepProcessor.addSweepDefinition(def);
                // If we've received all sweeps invoke the callback.
                if(rxSweeps != null) {
                    if (callback != null) {
                        callback.onDataReceived(rxSweeps, null);
                    }
                    return true;
                }
            }
            return false;
        };
        handler.failureCallback = sweepError -> {
            if (sweepError != null) {
                if (callback != null) {
                    callback.onDataReceived(null, sweepError);
                }
            }
        };
        // Queue the packet.
        mConnection.addRequest(new ESPRequest(sweepRequest, handler));
    }

    @Override
    public void requestSweepData(double v1Version, ESPRequestedDataListener<SweepData> callback) {
        // We first want to packet the sweep sections.
        requestSweepSections((sweepSections, error) -> {
            if(error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
                return;
            }
            // We've successfully received the V1's custom sweeps, next we want to packet the
            // maximum number of sweeps..
            requestMaxSweepIndex((data, maxSweepError) -> {
                if (maxSweepError != null) {
                    if (callback != null) {
                        callback.onDataReceived(null, maxSweepError);
                    }
                    return;
                }

                final int maxSweepIndex = data;
                if(areDefaultSweepDefinitionsAvailableForV1Version(v1Version)) {
                    // Request the default sweeps
                    requestSweepDefinitions(true, PacketId.RESPDEFAULTSWEEPDEFINITIONS, maxSweepIndex, (defaultSweeps, defaultSweepError) -> {
                        if(defaultSweepError != null) {
                            if (callback != null) {
                                callback.onDataReceived(null, "Failed to received the Sweep data from the connected V1");
                            }
                            return;
                        }
                        requestSweepDefinitions(false, PacketId.RESPSWEEPDEFINITION, maxSweepIndex, (sweeps, sweepDefinitionError) -> {
                            if (callback != null) {
                                if(sweepDefinitionError != null) {
                                    callback.onDataReceived(null, "Failed to received the Sweep data from the connected V1");
                                    return;
                                }
                                // Construct a sweep data object using the received sweep data.
                                SweepData sweepData = new SweepData(maxSweepIndex, sweepSections, defaultSweeps, sweeps);
                                callback.onDataReceived(sweepData, null);
                            }
                        });
                    });
                }
                else {
                    requestSweepDefinitions(false, PacketId.RESPSWEEPDEFINITION, maxSweepIndex, (sweeps, sweepError) -> {
                        if (callback != null) {
                            if(sweepError != null) {
                                callback.onDataReceived(null, "Failed to received the Sweep data from the connected V1");
                                return;
                            }
                            // Construct a sweep data object using the received sweep data.
                            SweepData sweepData = new SweepData(maxSweepIndex, sweepSections, null, sweeps);
                            callback.onDataReceived(sweepData, null);
                        }
                    });
                }
            });
        });
    }

    @Override
    public void requestSAVVYStatus(ESPRequestedDataListener<SAVVYStatus> callback) {
        requestSAVVYStatus(callback, -1);
    }

    @Override
    public void requestSAVVYStatus(ESPRequestedDataListener<SAVVYStatus> callback, long requestTimeout) {
        ESPPacket packet = new RequestSAVVYStatus(mConnection.getValentineType());
        ResponseHandler<ResponseSAVVYStatus> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPSAVVYSTATUS);
        // Add a response callback that will get invoked once the savvy status is received.
        handler.successCallback = response -> {
            if (response != null) {
                if (callback != null) {
                    SAVVYStatus savvyStatus = response.getSavvyStatus();
                    callback.onDataReceived(savvyStatus, null);
                }
            }
            return true;
        };
        // Failure callback that will get invoked if the packet times out.
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };
        ESPRequest req = new ESPRequest(packet, handler);
        req.setTimeout(requestTimeout);
        mConnection.addRequest(req);
    }

    @Override
    public void requestOverrideThumbwheelToNone(ESPRequestListener callback) {
        requestOverrideThumbwheel(RequestOverrideThumbwheel.NONE, callback);
    }

    @Override
    public void requestOverrideThumbwheelToAuto(ESPRequestListener callback) {
        requestOverrideThumbwheel(RequestOverrideThumbwheel.AUTO, callback);
    }

    @Override
    public void requestOverrideThumbwheel(byte speed, ESPRequestListener callback) {
        RequestOverrideThumbwheel thumbwheelRequest = new RequestOverrideThumbwheel(mConnection.getValentineType(),
                speed);
        ResponseHandler handler = new ResponseHandler();
        handler.successCallback = packet -> {
            // CONSIDER REQUESTING THE SAVVYSTATUS AND MAKING SURE THE THUMBWHEEL IS ACTUALLY
            // OVERRIDDEN
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };

        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };

        mConnection.addRequest(new ESPRequest(thumbwheelRequest, handler));
    }

    @Override
    public void requestSAVVYUnmute(boolean muteEnabled, ESPRequestListener callback) {
        RequestSavvyUnmuteEnable savvyMuteRequest = new RequestSavvyUnmuteEnable(mConnection.getValentineType(), muteEnabled);
        ResponseHandler handler = new ResponseHandler();
        handler.successCallback = packet -> {
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };

        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };

        mConnection.addRequest(new ESPRequest(savvyMuteRequest, handler));
    }

    @Override
    public void requestVehicleSpeed(ESPRequestedDataListener<Integer> callback) {
        ESPPacket vehicleSpeedRequet = new RequestVehicleSpeed(mConnection.getValentineType());
        ResponseHandler<ResponseVehicleSpeed> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPVEHICLESPEED);
        // Add a response callback that will get invoked once the vehicle speed is received.
        handler.successCallback = response -> {
            if (response != null) {
                if (callback != null) {
                    callback.onDataReceived(response.getVehicleSpeed(), null);
                }
            }
            return true;
        };
        // Failure callback that will get invoked if the packet times out.
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };

        mConnection.addRequest(new ESPRequest(vehicleSpeedRequet, handler));
    }

    @Override
    public void requestBatteryVoltage(ESPRequestedDataListener<String> callback) {
        ESPPacket batteryVoltageRequest = new RequestBatteryVoltage(mConnection.getValentineType());
        ResponseHandler<ResponseBatteryVoltage> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPBATTERYVOLTAGE);
        // Add a response callback that will get invoked once the battery voltage is received.
        handler.successCallback = response -> {
            if (response != null) {
                if (callback != null) {
                    callback.onDataReceived(response.getBatteryVoltage(), null);
                }
            }
            return true;
        };
        // Failure callback that will get invoked if the packet times out.
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };
        mConnection.addRequest(new ESPRequest(batteryVoltageRequest, handler));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void requestWriteSweepDefinitions(List<SweepDefinition> sweeps, ESPRequestedDataListener<Integer> callback) {
        ResponseHandler<ResponseSweepWriteResult> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPSWEEPWRITERESULT);
        handler.successCallback = response -> {
            if (response != null) {
                int writeResult = response.getWriteResult();
                if (callback != null) {
                    // If the write result isn't zero, indicate which sweep was invalid.
                    if(writeResult != 0) {
                        callback.onDataReceived(null, String.format("Sweep Definition %d contains invalid data", writeResult));
                    }
                    else {
                        callback.onDataReceived(writeResult, null);
                    }
                }
            }
            return true;
        };

        // Failure callback that will get invoked if the requests times out.
        handler.failureCallback = error -> {
            if (error != null) {
                if (callback != null) {
                    callback.onDataReceived(null, error);
                }
            }
        };

        // Loop through all sweeps and write them setting the commit bit on the very last sweep.
        for (int i = 0; i < sweeps.size(); i++) {
            SweepDefinition definition = sweeps.get(i);
            // If this is the last sweep indicate it's the commit sweep.
            if(i == (sweeps.size() - 1)) {
                definition.setCommit(true);
            }

            // Create a sweep write packet using the sweep definition
            RequestWriteSweepDefinition writeSweepRequest = new
                    RequestWriteSweepDefinition(mConnection.getValentineType(), definition);
            mConnection.addRequest(new ESPRequest(writeSweepRequest, handler));
        }
    }

    @Override
    public void requestMute(final boolean mute, ESPRequestListener callback) {
        ESPPacket packet;
        if (mute) {
            packet = new RequestMuteOn(mConnection.getValentineType());
        }
        else {
            packet = new RequestMuteOff(mConnection.getValentineType());
        }

        ResponseHandler<InfDisplayData> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.INFDISPLAYDATA);
        handler.successCallback = displayData -> {
            boolean muted = displayData.isSoft();
            if(muted == mute) {
                if (callback != null) {
                    // Pass in null to indicate that there was no muting the v1.
                    callback.onRequestCompleted(null);
                }
                return true;
            }
            return false;
        };
        // If we encounter an error we want to return the V1's current mute status.
        handler.failureCallback = error -> {
            if(callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(packet, handler));
    }

    @Override
    public void requestChangeMode(V1Mode mode, ESPRequestListener callback) {
        byte modeB = (byte) mode.getValue();
        ESPPacket modeChangeRequest = new RequestChangeMode(mConnection.getValentineType(), modeB);
        ResponseHandler<InfDisplayData> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.INFDISPLAYDATA);
        AtomicInteger cnt = new AtomicInteger(0);
        handler.successCallback = displayData -> {
            cnt.incrementAndGet();
            if(cnt.get() < 5) {
                // We need to receive 5 display data packets before returning invoking the callback
                cnt.incrementAndGet();
                return false;
            }
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        // If we encounter an error we want to return the V1's current mute status.
        handler.failureCallback = error -> {
            if(callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(modeChangeRequest, handler));
    }

    @Override
    public void requestDisplayOn(final boolean on, ESPRequestListener callback) {
        requestSetDisplayState (on, false, callback);
    }

    @Override
    public void requestSetDisplayState(boolean displayOn, boolean keepBTLedOn, ESPRequestListener callback) {
         DeviceId id = mConnection.getValentineType();
        requestSetDisplayState(id, displayOn, keepBTLedOn, callback);
    }

    @Override
    public void requestSetDisplayState(DeviceId device, boolean displayOn, boolean keepBTLedOn, ESPRequestListener callback){
        ESPPacket displayRequest;
        if (displayOn) {
            displayRequest = new RequestTurnOnMainDisplay(mConnection.getValentineType(), device);
        }
        else {
            displayRequest = new RequestTurnOffMainDisplay(mConnection.getValentineType(), device, mLastV1Version, keepBTLedOn);
        }

        ResponseHandler<InfDisplayData> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.INFDISPLAYDATA);
        handler.successCallback = displayData -> {
            if(displayData.isDisplayOn() == displayOn) {
                if (callback != null) {
                    callback.onRequestCompleted(null);
                }
                return true;
            }
            return false;
        };
        // If we encounter an error we want to return the V1's current mute status.
        handler.failureCallback = error -> {
            if(callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(displayRequest, handler));
    }

    @Override
    public void requestAbortAudioDelay(ESPRequestListener callback)
    {
        ESPPacket abortAudioRequest;
        abortAudioRequest = new RequestAbortAudioDelay(mConnection.getValentineType());
        ResponseHandler handler = new ResponseHandler<>();
        handler.successCallback = packet -> {
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(abortAudioRequest, handler));
    }

    @Override
    public void requestDisplayCurrentVolume(ESPRequestListener callback)
    {
        ESPPacket displayCurVolRequest;
        displayCurVolRequest = new RequestDisplayCurrentVolume(mConnection.getValentineType());
        ResponseHandler handler = new ResponseHandler<>();
        handler.successCallback = packet -> {
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(displayCurVolRequest, handler));
    }

    @Override
    public void requestStartAlertData(ESPRequestListener callback) {
        ESPPacket alertDataRequest = new RequestStartAlertData(mConnection.getValentineType());
        // Response respHandler that will be invoked for the first received alert data.
        ResponseHandler<ResponseAlertData> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPALERTDATA);
        handler.successCallback = response -> {
            // If we get a response indicate the packet completed.
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        // Add failure callback that will invoked the onRequestCompleted callback indicating the
        // reason the packet failed.
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };

        mConnection.addRequest(new ESPRequest(alertDataRequest, handler));
    }

    @Override
    public void requestStopAlertData(ESPRequestListener callback) {
        stopAlertData(callback, false);
    }

    @Override
    public void requestStopAlertDataImmediately(ESPRequestListener callback) {
        stopAlertData(callback, true);
    }

    /**
     *
     * @param callback Callback that will get fired once we've reasonably determined alert data has
     *                 stopped being received
     * @param sendNext Indicates if the stop at data request should be send next
     */
    private void stopAlertData(ESPRequestListener callback, boolean sendNext) {
        ESPPacket alertDataRequest = new RequestStopAlertData(mConnection.getValentineType());
        ResponseHandler handler = new ResponseHandler();
        handler.successCallback = response -> {
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        // Add failure callback that will invoked the onRequestCompleted callback indicating the
        // reason the packet failed.
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(alertDataRequest, handler), sendNext);
    }

    @Override
    public void requestFactoryDefault(DeviceId device, ESPRequestListener callback) {
        RequestFactoryDefault factoryDefaultRequest = new RequestFactoryDefault(mConnection.getValentineType(), device);
        ResponseHandler handler = new ResponseHandler();
        handler.successCallback = response -> {
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        // Add failure callback that will invoked the onRequestCompleted callback indicating the
        // reason the packet failed.
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(factoryDefaultRequest, handler));
    }

    @Override
    public void requestCurrentVolume(ESPRequestedDataListener<byte[]> callback) {
        RequestCurrentVolume currentVolRequest = new RequestCurrentVolume(mConnection
                .getValentineType());
        ResponseHandler<ResponseCurrentVolume> handler = new ResponseHandler<>();
        handler.addResponseID(PacketId.RESPCURRENTVOLUME);
        handler.successCallback = currentVolumeResp -> {
            if (callback != null) {
                callback.onDataReceived(currentVolumeResp.getCurrentVolume(), null);
            }
            return true;
        };

        // Add failure callback that will invoked the onRequestCompleted callback indicating the
        // reason the packet failed.
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onDataReceived(null, error);
            }
        };
        mConnection.addRequest(new ESPRequest(currentVolRequest, handler));
    }

    @Override
    public void requestWriteVolumeSettings(byte mainVolume, byte mutedVolume, byte aux0,
                                           ESPRequestListener callback) {
        // Construct a volume control ESP request
        RequestWriteVolume volumeWriteRequest =
                new RequestWriteVolume(mConnection.getValentineType(),
                        mainVolume, mutedVolume, aux0);

        ResponseHandler handler = new ResponseHandler();
        handler.successCallback  = packet -> {
            if (callback != null) {
                callback.onRequestCompleted(null);
            }
            return true;
        };
        // Add failure callback that will invoked the onRequestCompleted callback indicating the
        // reason the packet failed.
        handler.failureCallback = error -> {
            if (callback != null) {
                callback.onRequestCompleted(error);
            }
        };
        mConnection.addRequest(new ESPRequest(volumeWriteRequest, handler));
    }

    /**
     * Add an {@link ESPRequest} that will be sent out on the ESP bus. If not connected, this
     * request will automatically be failed.
     * @param request The {@link ESPRequest request} to be sent.
     *
     * @see #isConnected()
     */
    protected void addRequest(ESPRequest request) {
        if (request != null) {
            mConnection.addRequest(request);
        }
    }
    //endregion

    @Override
    public void destroy() {
        disconnect();
        // Unregister all listeners.
        mConnection.clearConnectionListeners();
        clearESPClientListener();
        clearNoDataListener();
        clearNotificationListener();
    }
}
