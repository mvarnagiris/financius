package com.code44.finance.api.financius;

import android.content.Context;

import com.code44.finance.App;
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.api.User;
import com.code44.finance.api.financius.requests.FinanciusBaseRequest;
import com.code44.finance.api.financius.requests.RegisterDeviceRequest;
import com.code44.finance.api.financius.requests.RegisterRequest;
import com.code44.finance.api.financius.requests.SyncRequest;
import com.code44.finance.data.db.DBHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FinanciusApi {
    private static FinanciusApi singleton;

    private final Context context;
    private final User user;
    private final ExecutorService executor;

    private FinanciusApi(Context context, User user, ExecutorService executor) {
        this.context = context;
        this.user = user;
        this.executor = executor;
    }

    public static synchronized FinanciusApi get() {
        if (singleton == null) {
            singleton = new FinanciusApi(App.getAppContext(), User.get(), Executors.newCachedThreadPool());
        }
        return singleton;
    }

    public void register(String email, String googleId, String firstName, String lastName, String photoUrl, String coverUrl) {
        if (BaseRequestEvent.isWorking(RegisterRequest.RegisterRequestEvent.class, null)) {
            return;
        }

        final RegisterRequest request = new RegisterRequest(context, user, DBHelper.get(context), email, googleId, firstName, lastName, photoUrl, coverUrl);
        execute(request);
    }

    public void registerDevice() {
        if (BaseRequestEvent.isWorking(RegisterDeviceRequest.RegisterDeviceRequestEvent.class, null)) {
            return;
        }

        final RegisterDeviceRequest request = new RegisterDeviceRequest(context, user);
        execute(request);
    }

    public void sync() {
        if (BaseRequestEvent.isWorking(SyncRequest.SyncRequestEvent.class, null)) {
            return;
        }

        final SyncRequest request = new SyncRequest(context, user);
        execute(request);
    }

    private void execute(FinanciusBaseRequest request) {
        executor.submit(request);
    }
}
