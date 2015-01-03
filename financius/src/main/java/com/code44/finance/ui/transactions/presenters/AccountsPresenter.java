package com.code44.finance.ui.transactions.presenters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.Presenter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.adapters.AutoCompleteAccountsFromAdapter;
import com.code44.finance.ui.transactions.autocomplete.adapters.AutoCompleteAccountsToAdapter;

public class AccountsPresenter extends Presenter implements AutoCompletePresenter<Account>, AutoCompleteAdapter.AutoCompleteAdapterListener {
    private final Button accountFromButton;
    private final Button accountToButton;
    private final View accountsDividerView;
    private final ViewGroup accountsAutoCompleteContainerView;

    public AccountsPresenter(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        accountFromButton = findView(activity, R.id.accountFromButton);
        accountToButton = findView(activity, R.id.accountToButton);
        accountsDividerView = findView(activity, R.id.accountsDividerView);
        accountsAutoCompleteContainerView = findView(activity, R.id.accountsAutoCompleteContainerView);

        accountFromButton.setOnClickListener(clickListener);
        accountFromButton.setOnLongClickListener(longClickListener);
        accountToButton.setOnClickListener(clickListener);
        accountToButton.setOnLongClickListener(longClickListener);
    }

    @Override public void showError(Throwable error) {
        accountFromButton.setHintTextColor(accountFromButton.getResources().getColor(R.color.error));
        accountToButton.setHintTextColor(accountToButton.getResources().getColor(R.color.error));
    }

    @Override public void onAutoCompleteAdapterShown(AutoCompleteAdapter autoCompleteAdapter) {
        if (autoCompleteAdapter instanceof AutoCompleteAccountsFromAdapter) {
            accountFromButton.setHint(R.string.show_all);
        } else {
            accountToButton.setHint(R.string.show_all);
        }
        accountsDividerView.setVisibility(View.GONE);
    }

    @Override public void onAutoCompleteAdapterHidden(AutoCompleteAdapter autoCompleteAdapter) {
        if (autoCompleteAdapter instanceof AutoCompleteAccountsFromAdapter) {
            accountFromButton.setHint(R.string.from);
        } else {
            accountToButton.setHint(R.string.to);
        }
        accountsDividerView.setVisibility(View.VISIBLE);
    }

    @Override public AutoCompleteAdapter<Account> showAutoComplete(AutoCompleteAdapter<?> currentAdapter, AutoCompleteResult autoCompleteResult, AutoCompleteAdapter.OnAutoCompleteItemClickListener<Account> clickListener, View view) {
        final AutoCompleteAdapter<Account> adapter = view.getId() == R.id.accountFromButton ? new AutoCompleteAccountsFromAdapter(accountsAutoCompleteContainerView, this, clickListener) : new AutoCompleteAccountsToAdapter(accountsAutoCompleteContainerView, this, clickListener);
        if (adapter.show(currentAdapter, autoCompleteResult)) {
            return adapter;
        }
        return null;
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
