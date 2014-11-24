package com.code44.finance.ui.settings.security;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class LockView extends FrameLayout {
    private OnPasswordEnteredListener listener;
    private State state;

    public LockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnPasswordEnteredListener listener) {
        this.listener = listener;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        switch (state) {
            case NewLock:
                onShowNewLock();
                break;
            case NewLockConfirm:
                onShowNewLockConfirm();
                break;
            case Unlock:
                onShowUnlock();
                break;
            default:
                throw new IllegalArgumentException("State " + state + " is not supported.");
        }
    }

    public abstract void showError(String message);

    protected abstract void onShowNewLock();

    protected abstract void onShowNewLockConfirm();

    protected abstract void onShowUnlock();

    protected void onPasswordEntered(String password) {
        if (listener != null) {
            listener.onPasswordEntered(password);
        }
    }

    public static enum State {
        NewLock, NewLockConfirm, Unlock
    }

    public static interface OnPasswordEnteredListener {
        public void onPasswordEntered(String password);
    }
}
