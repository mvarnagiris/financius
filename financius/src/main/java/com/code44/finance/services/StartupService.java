package com.code44.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;

import com.code44.finance.App;
import com.code44.finance.api.Api;
import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.utils.GeneralPrefs;

import javax.inject.Inject;

public class StartupService extends IntentService {
    @Inject User user;
    @Inject GcmRegistration gcmRegistration;
    @Inject Api api;
    @Inject CurrenciesApi currenciesApi;
    @Inject GeneralPrefs generalPrefs;
    @Inject CurrenciesManager currenciesManager;

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

        if (user.isPremium()) {
            api.sync();

            if (!gcmRegistration.isRegisteredWithServer()) {
                api.registerDevice();
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
    }
}
