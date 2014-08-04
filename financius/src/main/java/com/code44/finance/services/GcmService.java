package com.code44.finance.services;

import android.app.IntentService;
import android.content.Intent;

import com.code44.finance.api.financius.FinanciusApi;
import com.code44.finance.receivers.GcmBroadcastReceiver;

public class GcmService extends IntentService {
    public GcmService() {
        super(GcmService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FinanciusApi.get().sync();

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
