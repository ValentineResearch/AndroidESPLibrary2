package com.esplibrary.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Scans for {@link BluetoothDevice V1's}.
 */
public class BluetoothScanner extends BroadcastReceiver implements IScanner {

    /**
     * Main Looper (UI) bound Handler to perform callbacks on the main thread.
     */
    private Handler mHandler;
    private Context mContext;
    private AtomicBoolean mScanning;
    private BTScanListener mListener;
    /**
     * Holds the current scan type the scanner is to perform.
     */
    private ConnectionType mScanType;
    private long mTimeout;
    private int mScanMode;
    private BluetoothAdapter mBTAdptr;
    private V1cLEScanCallback mLEScanCB;
    private String mFindAddy;

    public BluetoothScanner(Context ctx) {
        mContext = ctx.getApplicationContext();
        mScanning = new AtomicBoolean(false);
        // We wanna create a respHandler that will be used for performing scanning operations on the Main(UI) thread.
        mHandler = new Handler(Looper.getMainLooper());
        mScanType = ConnectionType.Invalid;
        mBTAdptr = BTUtil.getBluetoothAdapterCompat(mContext);
        mScanMode = ScanSettings.SCAN_MODE_LOW_LATENCY;
    }

    @Override
    public void setScanCallback(BTScanListener listener) {
        synchronized (this) {
            mListener = listener;
        }
    }

    @Override
    public void setTimeout(long timeout) {
        // Only allow values zero and greater.
        mTimeout = timeout > 0 ? timeout : 0;
    }

    @Override
    public void setScanMode(int scanMode) {
        mScanMode = scanMode;
    }

    @Override
    public boolean scanForDevice(String addy, ConnectionType type) {
        if(addy == null || addy.isEmpty()) {
            throw new IllegalArgumentException("The find address cannot be null or empty!");
        }
        mFindAddy = addy;
        return performScanForType(type);
    }

    @Override
    public boolean scanForType(ConnectionType type) {
        // We wanna explicitly prevent scanning while a scan is in progress.
        if(isScanning()) {
            return false;
        }
        // If there is not registered listener return false.
        synchronized (this) {
            if(mListener == null) {
                return false;
            }
        }
        mFindAddy = null;
        return performScanForType(type);
    }

    /**
     * Performs the actual scan for the given Bluetooth type.
     * @param type  The Bluetooth type for which a BluetoothDevice discovery scan should be performed.
     *
     * @return  True if a scan was initiated otherwise false is returned.
     */
    @SuppressLint("MissingPermission")
    private boolean performScanForType(ConnectionType type) {
        // These two types are unsupported.
        if(type == ConnectionType.Invalid || type == ConnectionType.Demo) {
            mScanType = ConnectionType.Invalid;
            return false;
        }
        else if(type == ConnectionType.SPP) {
            // SPP scan.
            // Listen for scanning and bonding events.
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(this, filter);
            mBTAdptr.startDiscovery();
        }
        else {
            BluetoothLeScanner scanner = mBTAdptr.getBluetoothLeScanner();
            if (scanner == null) {
                return false;
            }
            Builder scanSettingsBuilder = new Builder();
            // If we are running on the apis that supports MatchMode use MATCH_MODE_AGGRESSIVE.
            if (VERSION_CODES.M <= VERSION.SDK_INT) {
                scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
                scanSettingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
            }
            scanSettingsBuilder.setScanMode(mScanMode);
            List<ScanFilter> filters = new ArrayList<>(1);
            // Creates a scanFilter Builder for the V1 LE UUID.
            ScanFilter.Builder filterBuilder = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BTUtil.V1CONNECTION_LE_SERVICE_UUID));
            filters.add(filterBuilder.build());
            if (mLEScanCB == null) {
                mLEScanCB = new V1cLEScanCallback();
            }
            scanner.startScan(filters, scanSettingsBuilder.build(), mLEScanCB);
        }

        // Only setup a timer if we have a timeout.
        if(mTimeout != 0) {
            mHandler.removeCallbacksAndMessages(null);
            // Post delay a runnable that will stop the scan.
            mHandler.postDelayed(this::didTimeout, mTimeout);
        }
        mScanType = type;
        mScanning.set(true);
        return true;
    }

    /**
     * Callback that fires when BT scan has timed out.
     */
    private void didTimeout() {
        // Stop the scan and notify the listener.
        stopScan();
        performScanCompletedCallback();
    }

    @Override
    public boolean isScanning() {
        return mScanning.get();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void stopScan() {
        mScanning.set(false);
        mHandler.removeCallbacksAndMessages(null);
        mFindAddy = null;
        if(mScanType == ConnectionType.SPP) {
            // Make sure that we cancel and unregister the broadcast receiver.
            mContext.unregisterReceiver(this);
            mBTAdptr.cancelDiscovery();
        }
        else if(mScanType == ConnectionType.LE) {
            if (mLEScanCB != null) {
                BluetoothLeScanner scanner = mBTAdptr.getBluetoothLeScanner();
                if (scanner != null) {
                    scanner.stopScan(mLEScanCB);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case BluetoothDevice.ACTION_FOUND:
                if(mScanning.get() && mScanType == ConnectionType.SPP) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Only fire callback for discovered V1connections.
                    if(isV1Connection(device.getName())) {
                        int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) -127);
                        if(mFindAddy != null) {
                            if(mFindAddy.equals(device.getAddress())) {
                                stopScan();
                                performV1connectionScannedCallback(this, device, ConnectionType.SPP, rssi);
                            }
                            return;
                        }
                        // If the find address isn't null and has characters, check to see if it matches.
                        performV1connectionScannedCallback(BluetoothScanner.this, device, ConnectionType.SPP, rssi);
                    }
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                // We should run until we are stopped.
                BluetoothAdapter adapter = BTUtil.getBluetoothAdapterCompat(context);
                adapter.startDiscovery();
                break;
        }
    }

    /**
     * Helper method for determining if a BluetoothDevice is a V1.
     *
     * @param name  The name of the BluetoothDevices to scan if it a V1.
     *
     * @return  Returns true if the BluetoothDevices is a V1.
     */
    public final static boolean isV1Connection(String name) {
        return name != null && !name.isEmpty() && (name.contains("V1connection"));
    }

    /**
     * Invokes {@link BTScanListener#onDeviceScanned(BluetoothScanner, BluetoothDevice, ConnectionType, int)} callback.
     */
    private void performV1connectionScannedCallback(BluetoothScanner scanner, BluetoothDevice device, ConnectionType type, int dbm) {
        synchronized (this) {
            if(mListener != null) {
                mListener.onDeviceScanned(scanner, device, type, dbm);
            }
        }
    }

    /**
     * Invokes {@link BTScanListener#onScanCompleted(BluetoothScanner)} callback.
     */
    private void performScanCompletedCallback() {
        synchronized (this) {
            if(mListener != null) {
                mListener.onScanCompleted(this);
            }
        }
    }

    /**
     * Bluetooth LE scan callback to be used for Android API v.21 and above.
     *
     * Created by JDavis on 4/7/2016.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private class V1cLEScanCallback extends ScanCallback {

        /**
         * Callback when a BLE advertisement has been found.
         *
         * @param callbackType Determines how this callback was triggered. Could be one of
         *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
         *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
         *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_MATCH_LOST}
         * @param result       A Bluetooth LE scan result.
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(mScanning.get() && mScanType == ConnectionType.LE) {
                BluetoothDevice device = result.getDevice();
                // If we have a find address, perform the V1 scanned callback and stop the
                // scan.
                if(mFindAddy != null) {
                    if(mFindAddy.equals(device.getAddress())) {
                        stopScan();
                        performV1connectionScannedCallback(BluetoothScanner.this, device, ConnectionType.LE, result.getRssi());
                    }
                    return;
                }
                // If the find address isn't null and has characters, check to see if it matches.
                performV1connectionScannedCallback(BluetoothScanner.this, device, ConnectionType.LE, result.getRssi());
            }
        }
    }
}
