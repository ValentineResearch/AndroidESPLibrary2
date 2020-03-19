package com.esplibrary.utilities;

import android.util.Log;

import com.valentineresearch.esplibrary_v2.BuildConfig;

public class ESPLogger {

    private final static boolean LOGGING_ENABLED = BuildConfig.ENABLE_LOGGING;
    private final static boolean INFO_LOGGING_ENABLED = LOGGING_ENABLED & BuildConfig.INFO_LOGGING;
    private final static boolean DEBUG_LOGGING_ENABLED = LOGGING_ENABLED & BuildConfig.DEBUG_LOGGING;
    private final static boolean VERBOSE_LOGGING_ENABLED = LOGGING_ENABLED & BuildConfig.VERBOSE_LOGGING;
    private final static boolean WARN_LOGGING_ENABLED = LOGGING_ENABLED & BuildConfig.WARN_LOGGING;
    private final static boolean ERROR_LOGGING_ENABLED = LOGGING_ENABLED & BuildConfig.ERROR_LOGGING;

    /**
     * Send an {@link Log#INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void i(String tag, String message) {
        if(INFO_LOGGING_ENABLED) {
           Log.i(tag, message);
        }
    }

    /**
     * Send a {@link Log#DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void d(String tag, String message) {
        if(DEBUG_LOGGING_ENABLED) {
           Log.d(tag, message);
        }
    }

    /**
     * Send a {@link Log#VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void v(String tag, String message) {
        if(VERBOSE_LOGGING_ENABLED) {
           Log.v(tag, message);
        }
    }

    /**
     * Send a {@link Log#WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void w(String tag, String message) {
        if(WARN_LOGGING_ENABLED) {
           Log.w(tag, message);
        }
    }

    /**
     * Send an {@link Log#ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    public static void e(String tag, String message) {
        if(ERROR_LOGGING_ENABLED) {
           Log.e(tag, message);
        }
    }
}
