package com.code44.finance.ui.settings.security;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.ui.common.BaseActivity;

import javax.inject.Inject;

public class LockActivity extends BaseActivity {
    private static final String EXTRA_SECURITY_TYPE = "EXTRA_SECURITY_TYPE";

    @Inject Security security;

    private Security.Type type;

    public static void start(Context context, Security.Type securityType) {
        final Intent intent = makeIntentForActivity(context, LockActivity.class);
        intent.putExtra(EXTRA_SECURITY_TYPE, securityType);
        startActivity(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = (Security.Type) getIntent().getExtras().getSerializable(EXTRA_SECURITY_TYPE);
        startNewLockProcess();
    }

    private void startNewLockProcess() {
        switch (type) {
            case None:
                onNewLockCreated("");
                break;
            case Pin:
                break;
            default:
                throw new IllegalStateException("Security type " + type + " is not supported.");
        }
    }

    private void onNewLockCreated(String password) {
        security.setLastUnlockTimestamp(System.currentTimeMillis());
        security.setPassword(password);
        security.setType(type);
        finish();
    }
}
