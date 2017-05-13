package com.code44.finance.utils;

import android.util.Log;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("UnusedDeclaration")
public class Logger {
    private static final int LOG_CHUNK_SIZE = 4000;

    private final String tag;

    private LogLevel logLevel;

    private Logger(String tag) {
        this.tag = tag;
        this.logLevel = LogLevel.Warning;
    }

    public static Logger with(String tag) {
        return new Logger(tag);
    }

    public Logger logLevel(LogLevel logLevel) {
        this.logLevel = checkNotNull(logLevel, "LogLevel cannot be null.");
        return this;
    }

    public void error(String message) {
        if (logLevel.log(LogLevel.Error)) {
            Log.e(tag, message);
        }
    }

    public void error(String message, Throwable error) {
        if (logLevel.log(LogLevel.Error)) {
            Log.e(tag, message, error);
        }
    }

    public void warning(String message) {
        if (logLevel.log(LogLevel.Warning)) {
            Log.w(tag, message);
        }
    }

    public void warning(String message, Throwable error) {
        if (logLevel.log(LogLevel.Warning)) {
            Log.w(tag, message, error);
        }
    }

    public void info(String message) {
        if (logLevel.log(LogLevel.Info)) {
            for (int i = 0, len = message.length(); i < len; i += LOG_CHUNK_SIZE) {
                int end = Math.min(len, i + LOG_CHUNK_SIZE);
                Log.i(tag, message.substring(i, end));
            }
        }
    }

    public void debug(String message) {
        if (logLevel.log(LogLevel.Debug)) {
            for (int i = 0, len = message.length(); i < len; i += LOG_CHUNK_SIZE) {
                int end = Math.min(len, i + LOG_CHUNK_SIZE);
                Log.d(tag, message.substring(i, end));
            }
        }
    }

    public enum LogLevel {
        None(0), Error(1), Warning(2), Info(3), Debug(4);

        private final int level;

        LogLevel(int level) {
            this.level = level;
        }

        public boolean log(LogLevel logLevel) {
            return level >= logLevel.level;
        }
    }
}
