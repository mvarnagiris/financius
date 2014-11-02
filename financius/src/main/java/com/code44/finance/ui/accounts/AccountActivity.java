package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.ModelActivity;
import com.code44.finance.utils.MoneyFormatter;

import javax.inject.Inject;

public class AccountActivity extends ModelActivity<Account> {
    @Inject @Main Currency mainCurrency;

    private TextView titleTextView;
    private TextView balanceTextView;
    private TextView mainCurrencyBalanceTextView;
    private TextView noteTextView;

    public static void start(Context context, String accountServerId) {
        final Intent intent = makeIntent(context, AccountActivity.class, accountServerId);
        startActivity(context, intent);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        balanceTextView = (TextView) findViewById(R.id.balanceTextView);
        mainCurrencyBalanceTextView = (TextView) findViewById(R.id.mainCurrencyBalanceTextView);
        noteTextView = (TextView) findViewById(R.id.noteTextView);
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Accounts.getQuery().asCursorLoader(this, AccountsProvider.uriAccount(modelId));
    }

    @Override protected Account getModelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected void onModelLoaded(Account model) {
        titleTextView.setText(model.getTitle());
        balanceTextView.setText(MoneyFormatter.format(model.getCurrency(), model.getBalance()));
        noteTextView.setText(model.getNote());
        if (model.getCurrency() == null || model.getCurrency().getId().equals(mainCurrency.getId())) {
            mainCurrencyBalanceTextView.setVisibility(View.GONE);
        } else {
            mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
            mainCurrencyBalanceTextView.setText(MoneyFormatter.format(mainCurrency, (long) (model.getBalance() * model.getCurrency().getExchangeRate())));
        }
    }

    @Override protected Uri getDeleteUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Accounts.ID + "=?", new String[]{modelId});
    }

    @Override protected void startModelEdit(String modelId) {
        AccountEditActivity.start(this, modelId);
    }
}
