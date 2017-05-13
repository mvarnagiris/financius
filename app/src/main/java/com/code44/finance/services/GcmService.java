package com.code44.finance.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.common.gcm.CollapseKey;
import com.code44.finance.receivers.GcmReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmService extends IntentService {
    public GcmService() {
        super(GcmService.class.getSimpleName());
    }

    @Override protected void onHandleIntent(Intent intent) {
        final Bundle extras = intent.getExtras();
        if (!extras.isEmpty()) {
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            final String messageType = gcm.getMessageType(intent);
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    final CollapseKey collapseKey = CollapseKey.valueOf(extras.getString("collapse_key"));
                    switch (collapseKey) {
                        case DataChanged:
                            SyncService.start(getApplicationContext(), collapseKey);
                            break;
                    }
                    break;
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmReceiver.completeWakefulIntent(intent);
    }
}
