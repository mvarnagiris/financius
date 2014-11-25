package com.code44.finance.ui.settings.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;

public class UnlockActivity extends BaseActivity implements LockView.OnPasswordEnteredListener {
    private static final String EXTRA_CLOSE_IF_NOT_VERIFIED = "EXTRA_CLOSE_IF_NOT_VERIFIED";

    private LockView lockView;

    private boolean closeIfNotVerified;
    private boolean isPasswordValidated = false;

    public static void startForResult(Activity activity, int requestCode, boolean closeIfNotVerified) {
        final Intent intent = makeIntentForActivity(activity, UnlockActivity.class);
        intent.putExtra(EXTRA_CLOSE_IF_NOT_VERIFIED, closeIfNotVerified);
        startActivityForResult(activity, intent, requestCode);
        if (closeIfNotVerified) {
            activity.overridePendingTransition(0, 0);
        }
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        closeIfNotVerified = getIntent().getBooleanExtra(EXTRA_CLOSE_IF_NOT_VERIFIED, true);
        startUnlockProcess();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (isFinishing() && closeIfNotVerified && !isPasswordValidated) {
            getSecurity().setLastUnlockTimestamp(0);
            getEventBus().post(new KillEverythingThanMoves());
        }
    }

    @Override public void onPasswordEntered(String password) {
        if (getSecurity().validate(password)) {
            onVerificationSuccessful();
        } else {
            onVerificationFailed();
        }
    }

    private void startUnlockProcess() {
        switch (getSecurity().getType()) {
            case None:
                onVerificationSuccessful();
                return;

            case Pin:
                lockView = new PinLockView(this);
                break;
            default:
                throw new IllegalStateException("Security type " + getSecurity().getType() + " is not supported.");
        }

        ((ViewGroup) findViewById(R.id.content)).addView(lockView);
        lockView.setListener(this);
        lockView.setState(LockView.State.Unlock);
    }

    public void onVerificationSuccessful() {
        getSecurity().setLastUnlockTimestamp(System.currentTimeMillis());
        setResult(RESULT_OK);
        isPasswordValidated = true;
        finish();
    }

    public void onVerificationFailed() {
        // TODO Show proper text
        isPasswordValidated = false;
        lockView.showError(getString(R.string.error_unlock_password));
        getSecurity().setLastUnlockTimestamp(0);
    }
}
