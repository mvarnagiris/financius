package com.code44.finance.api.financius;

import android.content.Context;

import com.code44.finance.App;
import com.code44.finance.api.User;
import com.code44.finance.api.financius.requests.FinanciusBaseRequest;
import com.code44.finance.api.financius.requests.RegisterRequest;

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
        final RegisterRequest request = new RegisterRequest(context, user, email, googleId, firstName, lastName, photoUrl, coverUrl);
        execute(request);
    }

    private void execute(FinanciusBaseRequest request) {
        executor.submit(request);
    }
}
