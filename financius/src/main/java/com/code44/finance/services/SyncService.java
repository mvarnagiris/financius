package com.code44.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.code44.finance.App;

public class SyncService extends IntentService {
    public SyncService() {
        super(SyncService.class.getSimpleName());
    }

    public static void start() {
        final Context context = App.getAppContext();
        final Intent intent = new Intent(context, SyncService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        syncCurrencies();
        syncCategories();
        syncAccounts();
        syncTransactions();
    }

    private void syncCurrencies() {
        // TODO Implement
    }

    private void syncCategories() {
        // TODO Implement
    }

    private void syncAccounts() {
        // TODO Implement
    }

    private void syncTransactions() {
        // TODO Implement
    }
}
