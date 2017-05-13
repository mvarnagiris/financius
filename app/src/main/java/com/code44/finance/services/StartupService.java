package com.code44.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.code44.finance.App;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointsApi;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.utils.preferences.GeneralPrefs;

import javax.inject.Inject;

public class StartupService extends IntentService {
    @Inject User user;
    @Inject Device device;
    @Inject EndpointsApi endpointsApi;
    @Inject CurrenciesApi currenciesApi;
    @Inject GeneralPrefs generalPrefs;

    public StartupService() {
        super(StartupService.class.getSimpleName());
    }

    public static void start(Context context) {
        context.startService(new Intent(context, StartupService.class));
    }

    @Override public void onCreate() {
        super.onCreate();
        App.with(getApplicationContext()).inject(this);
    }

    @Override protected void onHandleIntent(Intent intent) {
        undoUncommittedDeletes();
        updateCurrenciesIfNecessary();

        if (user.isLoggedIn()) {
            SyncService.start(getApplicationContext());
            if (!device.isRegisteredWithServer()) {
                endpointsApi.registerDevice(getApplicationContext());
            }
        }
    }

    private void undoUncommittedDeletes() {
        // This is necessary, because while DeleteFragment is visible, the app can terminate and we would need to handle
        // uncommitted deletes.
        // TODO Undo
    }

    private void updateCurrenciesIfNecessary() {
        if (!generalPrefs.isAutoUpdateCurrencies() || DateUtils.isToday(generalPrefs.getAutoUpdateCurrenciesTimestamp())) {
            return;
        }

        currenciesApi.updateExchangeRates();
        generalPrefs.setAutoUpdateCurrenciesTimestamp(System.currentTimeMillis());
        generalPrefs.notifyChanged();
    }
}
