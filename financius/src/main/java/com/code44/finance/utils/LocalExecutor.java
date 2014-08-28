package com.code44.finance.utils;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocalExecutor extends ThreadPoolExecutor {
    private static LocalExecutor singleton;

    public LocalExecutor(int maxThreads) {
        super(0, maxThreads, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new LocalThreadFactory());
    }

    public static synchronized LocalExecutor get() {
        if (singleton == null) {
            final int numberCores = Runtime.getRuntime().availableProcessors();
            singleton = new LocalExecutor(numberCores * 2 + 1);
        }
        return singleton;
    }

    private static class LocalThreadFactory implements ThreadFactory {
        @Override public Thread newThread(@NonNull Runnable runnable) {
            return new LocalThread(runnable);
        }
    }

    private static class LocalThread extends Thread {
        private LocalThread(Runnable runnable) {
            super(runnable);
        }

        @Override public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }

}
