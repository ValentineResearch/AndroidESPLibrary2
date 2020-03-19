/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.client.callbacks;

/**
 * Interface definition for a callback to be invoked when notification data is ready.
 */
public interface NotificationListener {

    /**
     * Callback method to be invoked string message should be delivered.
     * @param message The string message read from the demo data.
     */
    void onNotificationReceived(String message);
}
