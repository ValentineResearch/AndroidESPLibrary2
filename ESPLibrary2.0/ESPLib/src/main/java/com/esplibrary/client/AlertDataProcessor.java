/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.client;

import com.esplibrary.data.AlertData;
import com.esplibrary.data.SweepDefinition;
import com.esplibrary.utilities.ESPLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for assembly an alert table from individual {@link AlertData}.
 */
public class AlertDataProcessor {

    private static final String LOG_TAG = "AlertProc_TAG";

    private List<AlertData> mAlerts;

    public AlertDataProcessor() {
        mAlerts = new ArrayList<>(15);
    }

    /**
     * Adds a {@link AlertData} received from the V1. Once all alerts have been received, a list of
     * alerts is returned.
     *
     * @return  List of {@link AlertData alerts} once all alerts have been received.
     */
    public List<AlertData> addAlert(AlertData data) {
        // Build the AlertData from the response packet.
        final int count = data.getCount();
        // If count is zero, remove all of the old alerts.
        if(count == 0) {
            mAlerts.clear();
            return Collections.emptyList();
        }
        final int index = data.getIndex();
        // Remove any duplicates alert data items at 'index'
        for (int i = mAlerts.size() - 1; i >= 0; i--) {
            // We found an alert with a matching index, remove it because it's considered an old
            // alert from a previous AlertTable and no longer eligible for use.
            if (mAlerts.get(i).getIndex() == index) {
                mAlerts.remove(i);
            }
        }
        mAlerts.add(data);
        // If the number of alerts match, count, try to assemble a full table.
        if(mAlerts.size() >= count) {
            List<AlertData> table = new ArrayList<>(count);
            // Verify that we have all alert data indices.
            for (int i = 1; i <= count; i++) {
                // Make sure the alerts are in the correct order
                boolean foundIdx = false;
                for (int j = 0, size = mAlerts.size(); j < size; j++) {
                    AlertData ad = mAlerts.get(j);
                    // Make sure the index and count are valid for the most recent alert table
                    if(ad.getIndex() == i && ad.getCount() == count) {
                        table.add(ad);
                        foundIdx = true;
                        break;
                    }
                }
                // We didn't find expected index so return.
                if(!foundIdx) {
                    ESPLogger.w(LOG_TAG, "Didn't find alert index = " + i);
                    return null;
                }
            }
            // Clear the alerts
            mAlerts.clear();
            return table;
        }
        return null;
    }
}
