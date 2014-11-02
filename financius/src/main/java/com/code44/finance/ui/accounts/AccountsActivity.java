package com.code44.finance.ui.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.BaseModelsAdapter;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.analytics.Analytics;

import javax.inject.Inject;

public class AccountsActivity extends ModelListActivity {
    @Inject @Main Currency mainCurrency;

    private TextView balanceTextView;

    public static Intent makeViewIntent(Context context) {
        return makeViewIntent(context, AccountsActivity.class);
    }

    public static void startSelect(Activity activity, int requestCode) {
        startActivityForResult(activity, makeSelectIntent(activity, AccountsActivity.class), requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setShowDrawer(true);
        setShowDrawerToggle(true);
        super.onCreate(savedInstanceState);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_accounts;
    }

    @Override protected void onViewCreated() {
        super.onViewCreated();

        // Get views
        balanceTextView = (TextView) findViewById(R.id.balanceTextView);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            updateBalance(data);
        }
        super.onLoadFinished(loader, data);
    }

    @Override protected BaseModelsAdapter createAdapter() {
        return new AccountsAdapter(this, mainCurrency);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.Accounts.getQuery().asCursorLoader(this, AccountsProvider.uriAccounts());
    }

    @Override protected BaseModel modelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected void onModelClick(View view, int position, String modelId, BaseModel model) {
        AccountActivity.start(this, modelId);
    }

    @Override protected void startModelEdit(String modelId) {
        AccountEditActivity.start(this, modelId);
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return NavigationAdapter.NavigationScreen.Accounts;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.AccountList;
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
