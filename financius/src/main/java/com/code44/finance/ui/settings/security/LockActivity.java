package com.code44.finance.ui.settings.security;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;

public class LockActivity extends BaseActivity implements LockView.OnPasswordEnteredListener {
    private static final String EXTRA_SECURITY_TYPE = "EXTRA_SECURITY_TYPE";

    private static final String STATE_PASSWORD = "STATE_PASSWORD";
    private static final String STATE_STATE = "STATE_STATE";

    private LockView lockView;

    private Security.Type type;
    private String password;

    public static void start(Context context, Security.Type securityType) {
        final Intent intent = makeIntentForActivity(context, LockActivity.class);
        intent.putExtra(EXTRA_SECURITY_TYPE, securityType);
        startActivity(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        type = (Security.Type) getIntent().getExtras().getSerializable(EXTRA_SECURITY_TYPE);

        LockView.State restoredState = LockView.State.NewLock;
        if (savedInstanceState != null) {
            password = savedInstanceState.getString(STATE_PASSWORD);
            restoredState = (LockView.State) savedInstanceState.getSerializable(STATE_STATE);
        }
        startNewLockProcess(restoredState);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PASSWORD, password);
        if (lockView != null) {
            outState.putSerializable(STATE_STATE, lockView.getState());
        }
    }

    @Override public void onPasswordEntered(String password) {
        switch (lockView.getState()) {
            case NewLock:
                this.password = password;
                lockView.setState(LockView.State.NewLockConfirm);
                break;

            case NewLockConfirm:
                if (isPasswordMatch(password)) {
                    onNewLockCreated(password);
                } else {
                    // TODO Create message for error
                    lockView.showError("Password doesn't match, try again.");
                }
                break;

            default:
                throw new IllegalArgumentException("State " + lockView.getState() + " is not supported.");
        }
    }

    private void startNewLockProcess(LockView.State restoredState) {
        switch (type) {
            case None:
                getSecurity().clear();
                finish();
                return;
            case Pin:
                lockView = new PinLockView(this);
                break;
            default:
                throw new IllegalStateException("Security type " + type + " is not supported.");
        }

        ((ViewGroup) findViewById(R.id.content)).addView(lockView);
        lockView.setListener(this);
        lockView.setState(restoredState);
    }

    private void onNewLockCreated(String password) {
        getSecurity().setLastUnlockTimestamp(System.currentTimeMillis());
        getSecurity().setType(type, password);
        finish();
    }

    private boolean isPasswordMatch(String password) {
        return this.password.equals(password);
    }
}
