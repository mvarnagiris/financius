package com.code44.finance.ui.settings.security;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.code44.finance.R;

public class PinLockView extends LockView implements TextWatcher {
    private static final int DEFAULT_PIN_LENGTH = 4;

    private final EditText pinEditText;

    public PinLockView(Context context) {
        this(context, null);
    }

    public PinLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_pin_lock, this);

        // Get views
        pinEditText = (EditText) findViewById(R.id.pinEditText);

        // Setup
        pinEditText.addTextChangedListener(this);
    }

    @Override public void showError(String message) {
        pinEditText.setError(message);
    }

    @Override public void onShowNewLock() {
        pinEditText.setText("");
        showKeyboard();
    }

    @Override public void onShowNewLockConfirm() {
        pinEditText.setText("");
        showKeyboard();
    }

    @Override public void onShowUnlock() {
        pinEditText.setText("");
        showKeyboard();
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        final String password = pinEditText.getText().toString();
        if (password.length() == getPinLength()) {
            onPasswordEntered(password);
        }
    }

    @Override public void afterTextChanged(Editable s) {
    }

    private int getPinLength() {
        return DEFAULT_PIN_LENGTH;
    }

    private void showKeyboard() {
        postDelayed(new Runnable() {
            @Override public void run() {
                final InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(pinEditText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                pinEditText.requestFocus();
            }
        }, 100);
    }
}
