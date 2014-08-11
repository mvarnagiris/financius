package com.code44.finance.utils;

import android.util.Log;

import com.code44.finance.BuildConfig;

public class LogUtils {
    private static final int MAX_LOG_TAG_LENGTH = 23;

    private LogUtils() {
    }

    /**
     * WARNING: Don't use this when obfuscating class names with Proguard!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH) {
            return str.substring(0, MAX_LOG_TAG_LENGTH);
        }

        return str;
    }

    public static void d(final String tag, String message) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void d(final String tag, String message, Throwable cause) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message, cause);
        }
    }

    public static void v(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void v(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message, cause);
        }
    }

    public static void i(final String tag, String message) {
        Log.i(tag, message);
    }

    public static void i(final String tag, String message, Throwable cause) {
        Log.i(tag, message, cause);
    }

    public static void w(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void w(final String tag, String message, Throwable cause) {
        Log.w(tag, message, cause);
    }

    public static void e(final String tag, String message) {
        Log.e(tag, message);
    }

    public static void e(final String tag, String message, Throwable cause) {
        Log.e(tag, message, cause);
    }
}
