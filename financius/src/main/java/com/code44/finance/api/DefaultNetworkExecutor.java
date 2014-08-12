package com.code44.finance.api;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultNetworkExecutor extends ThreadPoolExecutor implements NetworkExecutor {
    private static NetworkExecutor singleton;

    private final Set<Runnable> workingRequests = Collections.newSetFromMap(new ConcurrentHashMap<Runnable, Boolean>());

    public DefaultNetworkExecutor(int maxThreads) {
        super(0, maxThreads, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NetworkThreadFactory());
    }

    public static synchronized NetworkExecutor get() {
        if (singleton == null) {
            final int numberCores = Runtime.getRuntime().availableProcessors();
            singleton = new DefaultNetworkExecutor(numberCores * 2 + 1);
        }
        return singleton;
    }

    @Override protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        workingRequests.add(r);
    }

    @Override protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        workingRequests.remove(r);
    }

    @Override public boolean isWorking(Request request) {
        return workingRequests.contains(request);
    }

    @Override public void execute(Request request) {
        super.execute(request);
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
