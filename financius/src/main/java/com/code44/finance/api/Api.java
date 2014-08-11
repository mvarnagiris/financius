package com.code44.finance.api;

import com.code44.finance.api.requests.RegisterDeviceRequest;
import com.code44.finance.api.requests.RegisterRequest;
import com.code44.finance.api.requests.SyncRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Api {
    private final ExecutorService executor;

    public Api() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void register(String email, String googleId, String firstName, String lastName, String photoUrl, String coverUrl) {
        final RegisterRequest request = new RegisterRequest(email, googleId, firstName, lastName, photoUrl, coverUrl);
        execute(request);
    }

    public void registerDevice() {
        final RegisterDeviceRequest request = new RegisterDeviceRequest();
        execute(request);
    }

    public void sync() {
        final SyncRequest request = new SyncRequest();
        execute(request);
    }

    private void execute(Request request) {
        executor.execute(request);
    }
}
