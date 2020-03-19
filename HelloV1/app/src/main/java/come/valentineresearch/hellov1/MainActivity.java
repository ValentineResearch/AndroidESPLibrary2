package come.valentineresearch.hellov1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esplibrary.bluetooth.BTUtil;
import com.esplibrary.bluetooth.ConnectionEvent;
import com.esplibrary.bluetooth.ConnectionType;
import com.esplibrary.constants.DeviceId;
import com.esplibrary.data.AlertData;
import com.esplibrary.data.SweepDefinition;
import com.esplibrary.packets.InfDisplayData;

import java.util.List;

public class MainActivity extends ESPActivity implements V1ScannerDialog.V1SelectedListener {

    private static final int REQUEST_LOCATION_PERMISSION = 0xFFFE;

    private V1Manager mV1Mngr;
    private BluetoothDevice mDevice;
    private ConnectionType mConnType;

    private RecyclerView log;
    private Spinner mDeviceSpnr;

    private TextView mLaser;
    private TextView mKa;
    private TextView mK;
    private TextView mX;
    private TextView mFront;
    private TextView mSide;
    private TextView mRear;
    private TextView mBt;
    private TextView mMute;

    private Button mScan;
    private Button mConnect;
    private Button mDisconnect;
    private Button mVersion;
    private Button mStartAT;
    private Button mStopAT;
    private Button mReadSweeps;

    private BasicAdapter mAdapter;
    private boolean mSupportsBT;

    private void bindViews() {
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        log = findViewById(R.id.log);
        log.setAdapter(mAdapter = new BasicAdapter());
        log.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Disconnected");
        getSupportActionBar().setSubtitle("No Device");

        mScan = findViewById(R.id.scan_btn);
        mDeviceSpnr = findViewById(R.id.device_spinner);

        mLaser = findViewById(R.id.laser);
        mKa = findViewById(R.id.ka);
        mK = findViewById(R.id.k);
        mX = findViewById(R.id.x);
        mFront = findViewById(R.id.front);
        mSide = findViewById(R.id.side);
        mRear = findViewById(R.id.rear);
        mBt = findViewById(R.id.bluetooth);
        mMute = findViewById(R.id.mute);

        mConnect = findViewById(R.id.conn_btn);
        mDisconnect = findViewById(R.id.disconn_btn);
        mVersion = findViewById(R.id.version_btn);
        mStartAT = findViewById(R.id.startAT_btn);
        mStopAT = findViewById(R.id.stopAT_btn);
        mReadSweeps = findViewById(R.id.sweeps_btn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        mSupportsBT = BTUtil.isBluetoothSupported(this);
        // Make sure the device supports Bluetooth
        if(mSupportsBT) {
            // If the activity isn't being recreated, launch the scanning dialog
            if (savedInstanceState == null) {
                if (handleLocationPermission()) {
                    new V1ScannerDialog().show(getSupportFragmentManager(),
                            "V1_SCANNER_DIALOG_TAG");
                }
            }

            mV1Mngr = V1Manager.getV1Manager();
            mV1Mngr.setV1ManagerDelegate(this);
            enableButtons(false);
        }
        else { // BT not supported
            TextView tv = findViewById(R.id.notSupportTV);
            tv.setVisibility(View.VISIBLE);
            disableViews();
            new AlertDialog.Builder(this)
                    .setTitle("Not Supported")
                    .setMessage("This device doesn't support Bluetooth")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    /**
     * Disable all buttons
     */
    private void disableViews() {
        mScan.setEnabled(false);
        mConnect.setEnabled(false);
        mDisconnect.setEnabled(false);
        mVersion.setEnabled(false);
        mStartAT.setEnabled(false);
        mStopAT.setEnabled(false);
        mReadSweeps.setEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_LOCATION_PERMISSION) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Denied")
                        .setMessage("The location permission was denied. It is required to discover Bluetooth devices! Are you sure?")
                        .setPositiveButton("Request", (dialog, which) -> {
                            ActivityCompat.requestPermissions(this,
                                    new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                                    REQUEST_LOCATION_PERMISSION);
                        })
                        .setNegativeButton("Cancel", ((dialog, which) -> {
                            Toast.makeText(this, "Missing permission, closing HelloV1!", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> finish(), 1000);
                        }))
                        .show();
            }
        }
    }

    /**
     * Checks if the app has the location permission, if not request its.
     *
     * @return True if the app has the permission to use location
     */
    private boolean handleLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show a dialog indicating why we need this permission
                    new AlertDialog.Builder(this)
                            .setMessage("The location permission is required to discover Bluetooth Devices")
                            .setPositiveButton("Request", (dialog, which) -> {
                                ActivityCompat.requestPermissions(this,
                                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                        REQUEST_LOCATION_PERMISSION);
                            })
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show();
                    return false;
                }
                // We don't need to show a permission rationale, just request the permission.
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                        REQUEST_LOCATION_PERMISSION);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSupportsBT) {
            // If we have a bt device, mConnect
            if (mDevice != null) {
                if (!mV1Mngr.isConnected()) {
                    if (!mV1Mngr.connect(this, mDevice, mConnType)) {
                        // Failed to establish a connection.
                        addLog("Failed to establish a connection.");
                    }
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Always mDisconnect when the activity stop, unless rotating
        mV1Mngr.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mV1Mngr.setV1ManagerDelegate(null);
    }

    /**
     * Sets the enabled state of the buttons
     * @param enable True to enable button
     */
    private void enableButtons(boolean enable) {
        mVersion.setEnabled(enable);
        mStartAT.setEnabled(enable);
        mStopAT.setEnabled(enable);
        mReadSweeps.setEnabled(enable);
    }

    /**
     * Clears the display data textviews (sets their text color to gray)
     */
    private void clearDisplayTextViews() {
        mLaser.setTextColor(Color.GRAY);
        mKa.setTextColor(Color.GRAY);
        mK.setTextColor(Color.GRAY);
        mX.setTextColor(Color.GRAY);
        mFront.setTextColor(Color.GRAY);
        mSide.setTextColor(Color.GRAY);
        mRear.setTextColor(Color.GRAY);
        mBt.setTextColor(Color.GRAY);
        mMute.setTextColor(Color.GRAY);
    }

    @Override
    public void onDisplayData(InfDisplayData displayData) {
        clearDisplayTextViews();
        if (displayData.isLaser()) {
            mLaser.setTextColor(Color.RED);
        }
        if (displayData.isKa()) {
            mKa.setTextColor(Color.GREEN);
        }
        if (displayData.isK()) {
            mK.setTextColor(Color.BLUE);
        }
        if (displayData.isX()) {
            mX.setTextColor(Color.MAGENTA);
        }

        if (displayData.isFrontImage1()) {
            mFront.setTextColor(Color.YELLOW);
        }
        if (displayData.isSideImage1()) {
            mSide.setTextColor(Color.CYAN);
        }

        if (displayData.isRearImage1()) {
            mRear.setTextColor(Color.RED);
        }

        // The values returned from this method are only valid on V1 versions V4.1018 and above
        if(displayData.isMuteIndicatorLitImg1()) {
            mMute.setTextColor(Color.RED);
        }

        // The values returned from this method are only valid on V1 versions V4.1018 and above
        if(displayData.isBluetoothIndicatorLitImg1()) {
            mBt.setTextColor(Color.BLUE);
        }
    }

    @Override
    public void onAlertTableReceived(List<AlertData> alerts) {
        super.onAlertTableReceived(alerts);
        if (alerts.isEmpty()) {
            addLog("Received an empty alarm table ");
        }
        else {
            // Find priority
            for (AlertData alert : alerts) {
                addLog(String.format("Idx: %d, Freq: %.3f Ghz, FS: %02X, RS: %02X", alert.getIndex(), (alert.getFrequency() / 1000F),
                        alert.getFrontSignalStrength(), alert.getRearSignalStrength()));
            }
        }
    }

    @Override
    public void onConnectionEvent(ConnectionEvent event) {
        super.onConnectionEvent(event);

        clearDisplayTextViews();
        setTitle(event.toString());

        Drawable btIcon = mScan.getBackground();
        Drawable wrappedDrawable = DrawableCompat.wrap(btIcon);

        boolean connected = event == ConnectionEvent.Connected;
        mConnect.setEnabled(!connected);
        mDisconnect.setEnabled(connected);
        enableButtons(connected);

        if (connected) {
            DrawableCompat.setTint(wrappedDrawable, Color.BLUE);
        }
        else if(event == ConnectionEvent.ConnectionFailed) {
            DrawableCompat.setTint(wrappedDrawable, Color.RED);
        }
        else {
            DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
        }
    }

    @Override
    public void onV1Selected(Dialog dialog, BluetoothDevice device, ConnectionType connType) {
        mV1Mngr.disconnect();
        mDevice = device;
        mConnType = connType;
        mConnect.setEnabled(device != null);

        setTitle("Disconnected");
        getSupportActionBar().setSubtitle(BTUtil.getFriendlyName(mDevice));

        mV1Mngr.connect(this, mDevice, mConnType);
    }

    /**
     * Helper method for returning a {@link DeviceId} from the selected item in the device spinner.
     *
     * @return Selected {@link DeviceId}
     */
    private DeviceId getDeviceFromSpinner() {
        Object selectedItem = mDeviceSpnr.getSelectedItem();
        if(mDeviceSpnr != null) {
            if(selectedItem.getClass().equals(String.class)) {
                String devStr = (String) selectedItem;

                switch (devStr) {
                    case "V1": return DeviceId.VALENTINE_ONE;
                    case "V1connection": return DeviceId.V1CONNECTION;
                    case "CD": return DeviceId.CONCEALED_DISPLAY;
                    case "SAVVY": return DeviceId.SAVVY;
                }
            }
        }
        return DeviceId.UNKNOWN_DEVICE;
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void scan(View view) {
        if (handleLocationPermission()) {
            new V1ScannerDialog().show(getSupportFragmentManager(),
                    "V1_SCANNER_DIALOG_TAG");
        }
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void connect(View view) {
        if (mDevice != null) {
            if (!mV1Mngr.connect(this, mDevice, mConnType)) {
                addLog("Failed to establish a connection.");
            }
        }
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void disconnect(View view) {
        mV1Mngr.disconnect();
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void version(View view) {
        DeviceId dev = getDeviceFromSpinner();
        mV1Mngr.requestVersion(dev, (version, error) -> runOnUiThread(() -> {
            if (error != null) {
                addLog(String.format("Error requesting stopping alert table: %s", error));
                return;
            }
            addLog(String.format("%s version: %s", dev.toString(), version));
        }));
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void startAT(View view) {
        mV1Mngr.startAlertTables(error -> runOnUiThread(() -> {
            if (error != null) {
                addLog(String.format("Error starting alert tables: %s", error));
                return;
            }
        }));
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void stopAT(View view) {
        mV1Mngr.stopAlertTables(error -> runOnUiThread(() -> {
            if (error != null) {
                addLog(String.format("Error stopping alert tables: %s", error));
                return;
            }
        }));
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void requestSweeps(View view) {
        mV1Mngr.requestSweeps((sweeps, error) -> runOnUiThread(() -> {
            if (error != null) {
                addLog(String.format("Error reading current sweeps: %s", error));
                return;
            }
            StringBuilder builder = new StringBuilder();
            for (SweepDefinition sweep : sweeps) {
                builder.append(String.format("Sweep Index: %d ", sweep.getIndex()))
                        .append(String.format("Start: %.3f Ghz\t\t", sweep.getUpperEdge() / 1000F))
                        .append(String.format("Stop: %.3f Ghz\n", sweep.getLowerEdge() / 1000F));
            }
            addLog(builder.toString());
        }));
    }

    /**
     * Button onclick handler
     * @param view clicked button
     */
    public void clear(View view) {
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    /**
     * Adds a log message to the adapter.
     *
     * @param log log message
     */
    private void addLog(String log) {
        if (mAdapter != null) {
            mAdapter.addString(log);
        }
    }
}
