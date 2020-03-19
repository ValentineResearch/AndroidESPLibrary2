package come.valentineresearch.hellov1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esplibrary.bluetooth.BTScanListener;
import com.esplibrary.bluetooth.BTUtil;
import com.esplibrary.bluetooth.BluetoothDeviceAdapter;
import com.esplibrary.bluetooth.BluetoothScanner;
import com.esplibrary.bluetooth.ConnectionType;

import java.util.ArrayList;
import java.util.List;

public class V1ScannerDialog extends DialogFragment implements BTScanListener, BluetoothDeviceAdapter.BluetoothDeviceSelectionListener {

    /**
     * Interface used to allow the owner/creator of the {@link V1ScannerDialog dialog} to run some
     * code a {@link BluetoothDevice device} is selected.
     * <P>This will only be called when a device is selected.</P>
     */
    public interface V1SelectedListener {
        /**
         * This method will be invoked when a {@link BluetoothDevice device} is selected.
         *
         * @param dialog    the internal dialog that device was selected from
         * @param device    The selected {@link BluetoothDevice}
         * @param connType  The VRI connection type of the dialog
         */
        void onV1Selected(Dialog dialog, BluetoothDevice device, ConnectionType connType);
    }

    private BluetoothScanner mScanner;
    private ConnectionType mScanType;
    private BluetoothDeviceAdapter mBTAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        // Initialize the BT scanner
        mScanner = new BluetoothScanner(getActivity().getApplicationContext());
        // We want the fastest possible scans
        mScanner.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        // Scan indefinitely.
        mScanner.setTimeout(0);
        mScanner.setScanCallback(this);

        // If LE is supported default to that,
        mScanType = BTUtil.isLESupported(getContext()) ? ConnectionType.LE : ConnectionType.SPP;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.v1scanner_dialog, null);

        RecyclerView list = v.findViewById(R.id.list);
        mBTAdapter = new BluetoothDeviceAdapter(this);

        List<BluetoothDeviceAdapter.BTAdapterItem> data = new ArrayList<>();
        // Add the section header for discovered devices
        data.add(new BluetoothDeviceAdapter.BTAdapterItem(getString(R.string.disc_devices_label)));
        mBTAdapter.setData(data);
        list.setAdapter(mBTAdapter);

        Button close = v.findViewById(R.id.close_btn);
        // When the cancel button is clicked dismiss the dialog
        close.setOnClickListener(v1 -> getDialog().cancel());

        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
        radioGroup.check((mScanType == ConnectionType.LE) ? R.id.le : R.id.spp);

        // This check will disable the LE option if it isn't supported
        if(!BTUtil.isLESupported(getContext())) {
            RadioButton lEoption = v.findViewById(R.id.le);
            lEoption.setEnabled(false);
        }

        // Setup a on checked change listener for the LE and SPP radio group.
        radioGroup.setOnCheckedChangeListener(this::onConnectionTypedChanged);

        list.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemAnimator itemAnimator = list.getItemAnimator();
        if(itemAnimator instanceof DefaultItemAnimator) {
            // Disable the change animation so we don't see flickering
            ((DefaultItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        return new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setOnCancelListener(this)
                .setOnDismissListener(this)
                .setView(v).create();
    }

    /**
     * RadioGroup onCheckedChanged callback that will get invoked radio button has been selected
     *
     * @param group
     * @param checkedId
     */
    private void onConnectionTypedChanged(RadioGroup group, int checkedId) {
        ConnectionType scanType = (checkedId == R.id.le) ? ConnectionType.LE : ConnectionType.SPP;
        // If the can type has changed, notify the scanner
        if (mScanType != scanType) {
            mScanType = scanType;
            mScanner.stopScan();
            mScanner.scanForType(scanType);

            List<BluetoothDeviceAdapter.BTAdapterItem> data = new ArrayList<>();
            // Add the section header for discovered devices
            data.add(new BluetoothDeviceAdapter.BTAdapterItem(getString(R.string.disc_devices_label)));
            mBTAdapter.setData(data);
        }
    }

    @Override
    public void onDeviceScanned(BluetoothScanner scanner, BluetoothDevice device, ConnectionType type, int dbm) {
        mBTAdapter.addBluetoothDevice(device, type, dbm);
    }

    @Override
    public void onScanCompleted(BluetoothScanner scanner) {}

    @Override
    public void onDeviceSelected(BluetoothDevice device, ConnectionType connType) {
        final Fragment parentFragment = getParentFragment();
        // parent fragment isn't null, this instance is attached to a fragment and not an Activity
        if (parentFragment instanceof V1SelectedListener) {
            ((V1SelectedListener) parentFragment).onV1Selected(getDialog(), device, connType);
            return;
        }

        FragmentActivity parentAct = getActivity();
        if(parentAct instanceof V1SelectedListener) {
            ((V1SelectedListener) parentAct).onV1Selected(getDialog(), device, connType);
        }
        // Once a device has been selected, always dismiss the activity.
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        mScanner.scanForType(mScanType);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Always cancel scans once in the background
        mScanner.stopScan();
    }
}
