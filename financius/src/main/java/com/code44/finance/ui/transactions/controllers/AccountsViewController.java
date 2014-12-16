package com.code44.finance.ui.transactions.controllers;

import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;

public class AccountsViewController extends ViewController {
    private Button accountFromButton;
    private Button accountToButton;

    public AccountsViewController(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        accountFromButton = findView(activity, R.id.accountFromButton);
        accountToButton = findView(activity, R.id.accountToButton);

        accountFromButton.setOnClickListener(clickListener);
        accountFromButton.setOnLongClickListener(longClickListener);
        accountToButton.setOnClickListener(clickListener);
        accountToButton.setOnLongClickListener(longClickListener);
    }

    @Override public void showError(Throwable error) {
    }

    public void setTransactionType(TransactionType transactionType) {
        switch (transactionType) {
            case Expense:
                accountFromButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.GONE);
                break;
            case Income:
                accountFromButton.setVisibility(View.GONE);
                accountToButton.setVisibility(View.VISIBLE);
                break;
            case Transfer:
                accountFromButton.setVisibility(View.VISIBLE);
                accountToButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    protected void setAccountFrom(Account account) {
        accountFromButton.setText(account == null ? null : account.getTitle());
    }

    protected void setAccountTo(Account account) {
        accountToButton.setText(account == null ? null : account.getTitle());
    }
}
