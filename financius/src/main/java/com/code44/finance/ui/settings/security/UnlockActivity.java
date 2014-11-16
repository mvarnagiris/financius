package com.code44.finance.ui.settings.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.ui.common.BaseActivity;

import javax.inject.Inject;

public class UnlockActivity extends BaseActivity {
    private static final String EXTRA_CLOSE_IF_NOT_VERIFIED = "EXTRA_CLOSE_IF_NOT_VERIFIED";

    @Inject Security security;

    private boolean closeIfNotVerified;

    public static void startForResult(Activity activity, int requestCode, boolean closeIfNotVerified) {
        final Intent intent = makeIntentForActivity(activity, UnlockActivity.class);
        intent.putExtra(EXTRA_CLOSE_IF_NOT_VERIFIED, closeIfNotVerified);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeIfNotVerified = getIntent().getBooleanExtra(EXTRA_CLOSE_IF_NOT_VERIFIED, true);
        startVerification();
    }

    private void startVerification() {
        if (security.getType() == Security.Type.None) {
            onVerificationSuccessful();
            return;
        }
        onVerificationFailed();
    }

    public void onVerificationSuccessful() {
        setResult(RESULT_OK);
        finish();
    }

    public void onVerificationFailed() {
    }
}
