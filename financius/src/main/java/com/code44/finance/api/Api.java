package com.code44.finance.api;

import com.code44.finance.api.requests.RegisterDeviceRequest;
import com.code44.finance.api.requests.RegisterRequest;
import com.code44.finance.api.requests.SyncRequest;
import com.code44.finance.qualifiers.ForNetwork;
import com.code44.finance.utils.Injector;

import java.util.concurrent.Executor;

import javax.inject.Inject;

public final class Api {
    private final Executor executor;
    private final Injector injector;

    @Inject
    public Api(@ForNetwork Executor executor, Injector injector) {
        this.executor = executor;
        this.injector = injector;
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
        injector.inject(request);
        executor.execute(request);
    }
}
