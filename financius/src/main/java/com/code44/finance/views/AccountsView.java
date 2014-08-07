package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.code44.finance.R;

public class AccountsView extends LinearLayout {
    @SuppressWarnings("UnusedDeclaration")
    public AccountsView(Context context) {
        this(context, null);
    }

    public AccountsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountsView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AccountsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.v_accounts, this);
    }
}
