package com.code44.finance.ui.accounts.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.accounts.edit.AccountEditActivity;
import com.code44.finance.ui.common.activities.ModelActivity;
import com.code44.finance.ui.reports.balance.BalanceChartView;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.interval.CurrentInterval;
import com.google.common.base.Strings;

import javax.inject.Inject;

public class AccountActivity extends ModelActivity<Account> {
    @Inject CurrentInterval currentInterval;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private TextView titleTextView;
    private TextView balanceTextView;
    private TextView mainCurrencyBalanceTextView;
    private TextView noteTextView;

    private AccountBalanceChartPresenter accountBalanceChartPresenter;

    public static void start(Context context, String accountId) {
        makeActivityStarter(context, AccountActivity.class, accountId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Get views
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        balanceTextView = (TextView) findViewById(R.id.balanceTextView);
        mainCurrencyBalanceTextView = (TextView) findViewById(R.id.mainCurrencyBalanceTextView);
        noteTextView = (TextView) findViewById(R.id.noteTextView);
        final BalanceChartView balanceChartView = (BalanceChartView) findViewById(R.id.balanceChartView);

        // Setup
        accountBalanceChartPresenter = new AccountBalanceChartPresenter(balanceChartView, amountFormatter, currenciesManager, getSupportLoaderManager());
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Accounts.getQuery().asCursorLoader(this, AccountsProvider.uriAccount(modelId));
    }

    @NonNull @Override protected Account getModelFrom(@NonNull Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected void onModelLoaded(@NonNull Account model) {
        titleTextView.setText(model.getTitle());
        balanceTextView.setText(amountFormatter.format(model.getCurrencyCode(), model.getBalance()));
        noteTextView.setText(model.getNote());
        noteTextView.setVisibility(Strings.isNullOrEmpty(model.getNote()) ? View.GONE : View.VISIBLE);
        if (currenciesManager.isMainCurrency(model.getCurrencyCode())) {
            mainCurrencyBalanceTextView.setVisibility(View.GONE);
        } else {
            mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
            mainCurrencyBalanceTextView.setText(amountFormatter.format((long) (model.getBalance() * currenciesManager.getExchangeRate(model.getCurrencyCode(), currenciesManager
                    .getMainCurrencyCode()))));
        }
        accountBalanceChartPresenter.setAccountAndInterval(model, currentInterval);
    }

    @Override protected void startModelEdit(@NonNull String modelId) {
        AccountEditActivity.start(this, modelId);
    }

    @Nullable @Override protected Uri getDeleteUri() {
        return AccountsProvider.uriAccounts();
    }

    @Nullable @Override protected Pair<String, String[]> getDeleteSelection(@NonNull String modelId) {
        return Pair.create(Tables.Accounts.ID + "=?", new String[]{modelId});
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Account;
    }
}