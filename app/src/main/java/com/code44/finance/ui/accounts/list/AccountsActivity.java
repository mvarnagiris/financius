package com.code44.finance.ui.accounts.list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.accounts.detail.AccountActivity;
import com.code44.finance.ui.accounts.edit.AccountEditActivity;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.utils.analytics.Screens;

import javax.inject.Inject;

public class AccountsActivity extends ModelsActivity<Account, AccountsAdapter> {
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private TextView balanceTextView;

    public static void startView(Context context) {
        ActivityStarter.begin(context, AccountsActivity.class).topLevel().showDrawer().showDrawerToggle().modelsView().start();
    }

    public static void startSelect(Context context, int requestCode) {
        ActivityStarter.begin(context, AccountsActivity.class).modelsSelect().startForResult(requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get views
        final View balanceContainerView = findViewById(R.id.balanceContainerView);
        balanceTextView = (TextView) findViewById(R.id.balanceTextView);

        if (getMode() != Mode.View) {
            balanceContainerView.setVisibility(View.GONE);
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == getModelsLoaderId()) {
            updateBalance(data);
        }
        super.onLoadFinished(loader, data);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.AccountList;
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Accounts;
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_accounts;
    }

    @Override protected AccountsAdapter createAdapter(ModelsAdapter.OnModelClickListener<Account> defaultOnModelClickListener, Mode mode) {
        return new AccountsAdapter(defaultOnModelClickListener, mode, this, currenciesManager, amountFormatter);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.Accounts.getQuery().asCursorLoader(this, AccountsProvider.uriAccounts());
    }

    @Override protected void onModelClick(Account model) {
        AccountActivity.start(this, model.getId());
    }

    @Override protected void startModelEdit(String modelId) {
        AccountEditActivity.start(this, modelId);
    }

    private void updateBalance(@NonNull Cursor cursor) {
        long balance = 0;
        if (cursor.moveToFirst()) {
            do {
                final Account account = Account.from(cursor);
                if (account.includeInTotals()) {
                    balance += account.getBalance() * currenciesManager.getExchangeRate(account.getCurrencyCode(), currenciesManager.getMainCurrencyCode());
                }
            } while (cursor.moveToNext());
        }
        balanceTextView.setText(amountFormatter.format(balance));
    }
}
