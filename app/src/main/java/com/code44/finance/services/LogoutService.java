package com.code44.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.code44.finance.App;
import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.devices.UnregisterDeviceRequest;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.activities.SplashActivity;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

public class LogoutService extends IntentService {
    @Inject EndpointFactory endpointFactory;
    @Inject User user;
    @Inject DBHelper dbHelper;
    @Inject Device device;
    @Inject Security security;
    @Inject GeneralPrefs generalPrefs;
    @Inject EventBus eventBus;

    public LogoutService() {
        super(LogoutService.class.getSimpleName());
    }

    public static void start(Context context) {
        context.startService(new Intent(context, LogoutService.class));
    }

    @Override public void onCreate() {
        super.onCreate();
        App.with(getApplicationContext()).inject(this);
    }

    @Override protected void onHandleIntent(Intent intent) {
        unregisterDevice();
        user.clear();
        dbHelper.clear();
        dbHelper.addDefaults();
        device.clear();
        security.clear();
        security.notifyChanged();
        generalPrefs.clear();
        generalPrefs.notifyChanged();
        eventBus.post(new BaseActivity.KillEverythingThanMoves());
        SplashActivity.start(getApplicationContext());
    }

    private void unregisterDevice() {
        try {
            new UnregisterDeviceRequest(eventBus, endpointFactory, device).call();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }
}
