package com.code44.finance.services;

import android.app.IntentService;
import android.content.Intent;

import com.code44.finance.api.Api;
import com.code44.finance.receivers.GcmBroadcastReceiver;

import javax.inject.Inject;

public class GcmService extends IntentService {
    @Inject Api api;

    public GcmService() {
        super(GcmService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        api.sync();

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
