package com.code44.finance.ui.accounts.list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.accounts.AccountActivity;
import com.code44.finance.ui.accounts.AccountEditActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.MoneyFormatter;

public class AccountsActivityPresenter extends ModelsActivityPresenter<Account> {
    private final Currency mainCurrency;

    private TextView balanceTextView;

    public AccountsActivityPresenter(Currency mainCurrency) {
        this.mainCurrency = mainCurrency;
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        final View balanceContainerView = findView(activity, R.id.balanceContainerView);
        balanceTextView = findView(activity, R.id.balanceTextView);

        if (getMode() != Mode.View) {
            balanceContainerView.setVisibility(View.GONE);
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            updateBalance(data);
        }
        super.onLoadFinished(loader, data);
    }

    @Override protected ModelsAdapter<Account> createAdapter(ModelsAdapter.OnModelClickListener<Account> defaultOnModelClickListener) {
        return new AccountsAdapter(defaultOnModelClickListener, mainCurrency);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccounts());
    }

    @Override protected void onModelClick(Context context, View view, Account model, Cursor cursor, int position) {
        AccountActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        AccountEditActivity.start(context, modelId);
    }

    private void updateBalance(Cursor cursor) {
        long balance = 0;
        if (cursor.moveToFirst()) {
            do {
                final Account account = Account.from(cursor);
                if (account.includeInTotals()) {
                    balance += account.getBalance() * account.getCurrency().getExchangeRate();
                }
            } while (cursor.moveToNext());
        }
        balanceTextView.setText(MoneyFormatter.format(mainCurrency, balance));
    }
}
