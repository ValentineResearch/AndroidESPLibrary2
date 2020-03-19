package com.esplibrary.client;

import com.esplibrary.client.callbacks.ESPRequestListener;
import com.esplibrary.data.AlertData;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.packets.InfDisplayData;

import java.util.List;

/**
 * Interface definition for a callback to be invoked when ESP data has been received.
 */
public interface ESPClientListener {
    /**
     * Callback method to be invoked when an {@link ESPPacket} has been received.
     * @param packet Received {@link ESPPacket}
     */
    void onPacketReceived(ESPPacket packet);

    /**
     * Callback method to be invoked when {@link InfDisplayData} has been received.
     *
     * @param displayData Last received {@link InfDisplayData}
     */
    void onDisplayDataReceived(InfDisplayData displayData);

    /**
     * Callback method to be invoked when an full table of {@link AlertData alerts} has been
     * received.
     *
     * @param table List of {@link AlertData alerts} detected by the V1.
     *
     * @see IESPClient#requestStartAlertData(ESPRequestListener)
     */
    void onAlertTableReceived(List<AlertData> table);
}
