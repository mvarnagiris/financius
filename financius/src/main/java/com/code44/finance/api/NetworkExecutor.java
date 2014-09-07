package com.code44.finance.api;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NetworkExecutor extends ThreadPoolExecutor {
    private static NetworkExecutor singleton;

    public NetworkExecutor(int maxThreads) {
        super(0, maxThreads, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NetworkThreadFactory());
    }

    public static synchronized NetworkExecutor get() {
        if (singleton == null) {
            final int numberCores = Runtime.getRuntime().availableProcessors();
            singleton = new NetworkExecutor(numberCores * 2 + 1);
        }
        return singleton;
    }

    private static class NetworkThreadFactory implements ThreadFactory {
        @Override public Thread newThread(@NonNull Runnable runnable) {
            return new NetworkThread(runnable);
        }
    }

    private static class NetworkThread extends Thread {
        private NetworkThread(Runnable runnable) {
            super(runnable);
        }

        @Override public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }

}
