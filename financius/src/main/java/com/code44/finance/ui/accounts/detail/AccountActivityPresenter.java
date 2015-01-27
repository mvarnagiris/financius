package com.code44.finance.ui.accounts.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.accounts.AccountEditActivity;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelActivityPresenter;
import com.code44.finance.ui.reports.balance.BalanceChartView;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.interval.BaseInterval;

class AccountActivityPresenter extends ModelActivityPresenter<Account> implements LoaderManager.LoaderCallbacks<Cursor> {
    private final BaseInterval baseInterval;
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    private TextView titleTextView;
    private TextView balanceTextView;
    private TextView mainCurrencyBalanceTextView;
    private TextView noteTextView;

    private AccountBalanceChartPresenter accountBalanceChartPresenter;

    protected AccountActivityPresenter(EventBus eventBus, BaseInterval baseInterval, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(eventBus);
        this.baseInterval = baseInterval;
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        titleTextView = findView(activity, R.id.titleTextView);
        balanceTextView = findView(activity, R.id.balanceTextView);
        mainCurrencyBalanceTextView = findView(activity, R.id.mainCurrencyBalanceTextView);
        noteTextView = findView(activity, R.id.noteTextView);
        final BalanceChartView balanceChartView = findView(activity, R.id.balanceChartView);

        accountBalanceChartPresenter = new AccountBalanceChartPresenter(balanceChartView, amountFormatter, currenciesManager, activity.getSupportLoaderManager());
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccount(modelId));
    }

    @Override protected Account getModelFrom(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected void onModelLoaded(Account model) {
        titleTextView.setText(model.getTitle());
        balanceTextView.setText(amountFormatter.format(model.getCurrencyCode(), model.getBalance()));
        noteTextView.setText(model.getNote());
        noteTextView.setVisibility(Strings.isEmpty(model.getNote()) ? View.GONE : View.VISIBLE);
        if (currenciesManager.isMainCurrency(model.getCurrencyCode())) {
            mainCurrencyBalanceTextView.setVisibility(View.GONE);
        } else {
            mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
            mainCurrencyBalanceTextView.setText(amountFormatter.format((long) (model.getBalance() * currenciesManager.getExchangeRate(model.getCurrencyCode(), currenciesManager.getMainCurrencyCode()))));
        }
        accountBalanceChartPresenter.setAccountAndInterval(model, baseInterval);
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        AccountEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.Accounts.ID + "=?", new String[]{modelId});
    }
}
