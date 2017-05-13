package com.code44.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.code44.finance.App;
import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.SyncRequest;
import com.code44.finance.common.gcm.CollapseKey;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.interval.ActiveInterval;
import com.code44.finance.utils.interval.CurrentInterval;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

public class SyncService extends IntentService {
    private static final String EXTRA_COLLAPSE_KEY = "COLLAPSE_KEY";

    @Inject EventBus eventBus;
    @Inject EndpointFactory endpointFactory;
    @Inject User user;
    @Inject DBHelper dbHelper;
    @Inject Device device;
    @Inject CurrenciesManager currenciesManager;
    @Inject GeneralPrefs generalPrefs;
    @Inject Security security;
    @Inject CurrentInterval currentInterval;
    @Inject ActiveInterval activeInterval;

    public SyncService() {
        super(SyncService.class.getSimpleName());
    }

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, CollapseKey collapseKey) {
        final Intent intent = new Intent(context, SyncService.class);
        intent.putExtra(EXTRA_COLLAPSE_KEY, collapseKey);
        context.startService(intent);
    }

    @Override public void onCreate() {
        super.onCreate();
        App.with(getApplicationContext()).inject(this);
    }

    @Override protected void onHandleIntent(Intent intent) {
        final CollapseKey collapseKey = (CollapseKey) intent.getSerializableExtra(EXTRA_COLLAPSE_KEY);

        if (collapseKey != null) {
            switch (collapseKey) {
                case DataChanged:
                    sync();
                    break;
            }
        }
    }

    private void sync() {
        try {
            final SyncRequest syncRequest = new SyncRequest(eventBus, endpointFactory, getApplicationContext(), user, dbHelper, device, currenciesManager, generalPrefs, security, currentInterval, activeInterval);
            syncRequest.call();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }
}
