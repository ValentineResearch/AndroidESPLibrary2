/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jdavis on 8/11/2016.
 */
public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.BaseViewHolder> {

    public interface BluetoothDeviceSelectionListener {
        void onDeviceSelected(BluetoothDevice device, ConnectionType connType);
    }

    public static final int SECTION_HEADER = 0;
    public static final int BT_LIST_ITEM = 1;
    private List<BTAdapterItem> mData = new ArrayList<>(10);
    private BluetoothDeviceSelectionListener mSelListener;

    public BluetoothDeviceAdapter(BluetoothDeviceSelectionListener selectionListener) {
        mSelListener = selectionListener;
        notifyDataSetChanged();
    }

    /**
     * Sets the adapters datas et.
     *
     * @param data List of {@link BTAdapterItem}
     */
    public void setData(List<BTAdapterItem> data) {
        // Determines the differences between the two list and update the adapter
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BTDiffCallback(mData, data));
        mData = data;
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Returns the {@link BTAdapterItem item} at position.
     *
     * @param position Index of the item to return
     *
     * @return Item at position inside of the adapters data set.
     */
    private BTAdapterItem get(int position) {
        return mData.get(position);
    }

    /**
     * Returns the adapters data set
     *
     * @return List of {@link BTAdapterItem}
     */
    public List<BTAdapterItem> getData() {
        return mData;
    }

    @Override
    public int getItemViewType(int position) {
        return get(position).isSectionHeader ? SECTION_HEADER : BT_LIST_ITEM;
    }

    /**
     * Adds {@link BluetoothDevice} to this adapters data set.
     *
     * @param device Bluetooth device to add
     * @param type Bluetooth type of device
     * @param rssi Signal strength of device.
     */
    public void addBluetoothDevice(BluetoothDevice device, ConnectionType type, int rssi) {
        if (device == null) {
            return;
        }
        // Try to find the matching item and update it's rssi
        for (int i = 0; i < mData.size(); i++) {
            BTAdapterItem datum = mData.get(i);
            if (!datum.isSectionHeader) {
                if (datum.btBundle.equals(device)) {
                    // Update the RSSI of the bundle
                    datum.btBundle.setRSSI(rssi);
                    notifyItemChanged(i);
                    return;
                }
            }
        }

        // We didn't find a matching BT device so add one to the tail end.
        mData.add(new BTAdapterItem(new BTBundle(device, type, rssi)));
        notifyItemInserted(getItemCount());
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        if (viewType == SECTION_HEADER) {
            return new SectionHeaderVH(li.inflate(android.R.layout.simple_list_item_1, parent, false));
        }
        return new BluetoothDeviceVH(li.inflate(android.R.layout.simple_list_item_2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bind(BTAdapterItem datum);
    }

    /**
     * Section ViewHoler
     */
    public class SectionHeaderVH extends BaseViewHolder {

        TextView title;

        public SectionHeaderVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
        }

        @Override
        void bind(BTAdapterItem datum) {
            title.setText(datum.getTitle());
        }
    }

    /**
     * Bluetooth item ViewHolder
     */
    public class BluetoothDeviceVH extends SectionHeaderVH implements View.OnClickListener {

        TextView subtitle;

        public BluetoothDeviceVH(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            subtitle = itemView.findViewById(android.R.id.text2);
        }

        @Override
        void bind(BTAdapterItem datum) {
            super.bind(datum);
            subtitle.setText(datum.getSubtitle());
        }

        @Override
        public void onClick(View v) {
            if (mSelListener != null) {
                BTAdapterItem datum = get(getAdapterPosition());
                mSelListener.onDeviceSelected(datum.btBundle.getDevice(), datum.btBundle.getConnectionType());
            }
        }
    }

    public final static class BTAdapterItem {

        final boolean isSectionHeader;
        final String title;
        BTBundle btBundle;

        public BTAdapterItem(String sectionTitle) {
            title = sectionTitle;
            isSectionHeader = true;
            btBundle = null;
        }

        public BTAdapterItem(BTBundle bluetoothBundle) {
            title = null;
            isSectionHeader = false;
            this.btBundle = bluetoothBundle;
        }

        /**
         * Indicates if the item represents a section header.
         *
         * @return True if the item is for a section header
         */
        public boolean isSectionHeader() {
            return isSectionHeader;
        }

        /**
         * Return's the title of the item.
         *
         * @return title
         */
        public String getTitle() {
            if (isSectionHeader) {
                return title;
            }
            return BTUtil.getFriendlyName(btBundle.getDevice());
        }

        /**
         * Returns the subtitle of the item.
         * @apiNote {@link #isSectionHeader Section headers} don't support subtitles and null is
         * always returned.
         *
         * @return subtitle
         */
        public String getSubtitle() {
            if (isSectionHeader) {
                return null;
            }
            return String.format("rssi: %d", btBundle.getRSSI());
        }

        /**
         * Return the item was last updated.
         * @apiNote {@link #isSectionHeader Section headers} don't support last update time-keeping
         * so {@link Long#MIN_VALUE} is always returned if {@code this.isSectionHeader == true}.
         *
         * @return Time since item was last updated
         */
        public long getTimeSinceLastUpdated() {
            if (isSectionHeader) {
                return Long.MIN_VALUE;
            }
            // Calculate the diff since now and when the item was last updated
            return System.currentTimeMillis() - btBundle.getLastTimeUpdated();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BTAdapterItem that = (BTAdapterItem) o;
            return isSectionHeader == that.isSectionHeader &&
                    Objects.equals(getTitle(), that.getTitle()) &&
                    Objects.equals(btBundle, that.btBundle);
        }

        @NonNull
        @Override
        public String toString() {
            return getTitle();
        }
    }

    public final static class BTDiffCallback extends DiffUtil.Callback {

        List<BTAdapterItem> oldList;

        List<BTAdapterItem> newList;

        public BTDiffCallback(List<BTAdapterItem> oldList, List<BTAdapterItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            BTAdapterItem oldBtAdapterItem = oldList.get(oldItemPosition);
            BTAdapterItem newBtAdapterItem = newList.get(newItemPosition);

            if (oldBtAdapterItem.isSectionHeader == newBtAdapterItem.isSectionHeader) {
                if(oldBtAdapterItem.isSectionHeader) {
                    // Both items are section headers, we want to make sure their titles are identical
                    return oldBtAdapterItem.getTitle().equals(newBtAdapterItem.getTitle());
                }
                // Both items are BT Device item so compare their respect BT device addresses
                return Objects.equals(oldBtAdapterItem.btBundle, newBtAdapterItem.btBundle);
            }
            // The two items aren't equal because one is a section header and the other isn't
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            BTAdapterItem oldBtAdapterItem = oldList.get(oldItemPosition);
            BTAdapterItem newBtAdapterItem = newList.get(newItemPosition);
            // This two items have been determined to be the same so we wanna check if their
            // content is changed (determine if their contained data has been updated)

            // Check to see if the two items are section headers
            if (oldBtAdapterItem.isSectionHeader) { //
                return oldBtAdapterItem.getTitle().equals(newBtAdapterItem.getTitle());
            }
            // The two items BT device items, check if their rssi has changed
            return oldBtAdapterItem.btBundle.getRSSI() ==newBtAdapterItem.btBundle.getRSSI();
        }
    }
}
