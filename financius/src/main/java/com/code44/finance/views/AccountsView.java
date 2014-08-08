package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.utils.MoneyFormatter;

import java.util.List;

public class AccountsView extends LinearLayout {
    private static final int TOP_STATIC_VIEWS_COUNT = 1;
    private static final int BOTTOM_STATIC_VIEWS_COUNT = 1;

    private final View balanceContainer_V;
    private final TextView totalBalance_TV;

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

        // Get views
        balanceContainer_V = findViewById(R.id.balanceContainer_V);
        totalBalance_TV = (TextView) findViewById(R.id.totalBalance_TV);
    }

    public void setAccounts(List<Account> accounts) {
        addOrRemoveAccountViews(accounts);
        updateAccountViews(accounts);
    }

    private void addOrRemoveAccountViews(List<Account> accounts) {
        final int currentSize = getChildCount();
        final int newSize = TOP_STATIC_VIEWS_COUNT + BOTTOM_STATIC_VIEWS_COUNT + accounts.size();

        if (newSize > currentSize) {
            for (int i = 0, count = newSize - currentSize; i < count; i++) {
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.include_account, this, false);
                addView(view, TOP_STATIC_VIEWS_COUNT);
            }
        } else if (newSize < currentSize) {
            removeViews(TOP_STATIC_VIEWS_COUNT, currentSize - newSize);
        }
    }

    private void updateAccountViews(List<Account> accounts) {
        for (int i = TOP_STATIC_VIEWS_COUNT, size = getChildCount() - BOTTOM_STATIC_VIEWS_COUNT; i < size; i++) {
            final Account account = accounts.get(i - TOP_STATIC_VIEWS_COUNT);
            final View view = getChildAt(i);
            ((TextView) view.findViewById(R.id.title_TV)).setText(account.getTitle());
            ((TextView) view.findViewById(R.id.balance_TV)).setText(MoneyFormatter.format(account.getCurrency(), account.getBalance()));
        }
    }
}
