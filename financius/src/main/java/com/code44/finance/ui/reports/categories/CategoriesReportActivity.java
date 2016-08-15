package com.code44.finance.ui.reports.categories;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.reports.BaseReportActivity;
import com.code44.finance.ui.transactions.edit.presenters.TransactionTypePresenter;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.utils.interval.ActiveInterval;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class CategoriesReportActivity extends BaseReportActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TRANSACTIONS = 1;

    private static final int LOADER_INCOMES = 2;
    private static final int LOADER_TRANSFERS = 3;

    @Inject ActiveInterval activeInterval;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private CategoriesReportView categoriesReportView;

    private CategoriesReportAdapter adapter;
    private TransactionType transactionType = TransactionType.Expense;
    private TransactionTypePresenter transactionTypeViewController;

    public static Intent makeIntent(Context context) {
        return makeIntentForActivity(context, CategoriesReportActivity.class);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShowDrawer(true);
        setShowDrawerToggle(true);
        setContentView(R.layout.activity_categories_report);

        // Get views
        categoriesReportView = (CategoriesReportView) findViewById(R.id.categoriesReportView);
        transactionTypeViewController = new TransactionTypePresenter(this, categoriesReportView);
        transactionTypeViewController.setTransactionType(transactionType);
        final ListView listView = (ListView) findViewById(R.id.listView);

        // Setup
        adapter = new CategoriesReportAdapter(this, amountFormatter);
        listView.setAdapter(adapter);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Reports;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CategoriesReport;
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_TRANSACTIONS:
                return Tables.Transactions
                        .getQuery()
                        .selection(" and "+ Tables.Transactions.TYPE+"=? and " + Tables.Transactions.DATE + " between ? and ?", TransactionType.Expense.asString(), String.valueOf(activeInterval.getInterval().getStartMillis()), String.valueOf(activeInterval.getInterval().getEndMillis() - 1))
                        .asCursorLoader(this, TransactionsProvider.uriTransactions());
            case LOADER_INCOMES:
                return Tables.Transactions
                        .getQuery()
                        .selection(" and "+ Tables.Transactions.TYPE+"=? and " + Tables.Transactions.DATE + " between ? and ?", TransactionType.Income.asString(), String.valueOf(activeInterval.getInterval().getStartMillis()), String.valueOf(activeInterval.getInterval().getEndMillis() - 1))
                        .asCursorLoader(this, TransactionsProvider.uriTransactions());
            case LOADER_TRANSFERS:
                return Tables.Transactions
                        .getQuery()
                        .selection(" and "+ Tables.Transactions.TYPE+"=? and " + Tables.Transactions.DATE + " between ? and ?", TransactionType.Transfer.asString(), String.valueOf(activeInterval.getInterval().getStartMillis()), String.valueOf(activeInterval.getInterval().getEndMillis() - 1))
                        .asCursorLoader(this, TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_TRANSACTIONS:
                onTransactionsLoaded(cursor, TransactionType.Expense);
                break;
            case LOADER_INCOMES:
                onTransactionsLoaded(cursor, TransactionType.Income);
                break;
            case LOADER_TRANSFERS:
                onTransactionsLoaded(cursor, TransactionType.Transfer);
                break;
        }
    }

    @Override public void onLoaderReset(final Loader<Cursor> loader) {
    }

    @Subscribe public void onActiveIntervalChanged(ActiveInterval interval) {
        transactionTypeViewController.setTransactionType(TransactionType.Expense);
        getSupportLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
    }

    @Subscribe
    public void onTransactionTypeChanged(TransactionType transactionType) {
        int loaderType = 0;
        switch (this.transactionType) {
            case Expense:
                this.transactionType = TransactionType.Income;
                loaderType = LOADER_INCOMES;
                break;
            case Income:
                this.transactionType = TransactionType.Transfer;
                loaderType = LOADER_TRANSFERS;
                break;
            case Transfer:
                this.transactionType = TransactionType.Expense;
                loaderType = LOADER_TRANSACTIONS;
                break;
            default:
                this.transactionType = TransactionType.Expense;
                loaderType = LOADER_TRANSACTIONS;
        }
        transactionTypeViewController.setTransactionType(this.transactionType);
        getSupportLoaderManager().restartLoader(loaderType, null, this);
    }

    private void onTransactionsLoaded(Cursor cursor, TransactionType transactionType) {
        final CategoriesReportData categoriesReportData = new CategoriesReportData(this, cursor, currenciesManager, transactionType);
        categoriesReportView.setPieChartData(categoriesReportData.getPieChartData());
        categoriesReportView.setTotalExpense(categoriesReportData.getPieChartData().getTotalValue());
        adapter.setData(categoriesReportData, categoriesReportData.getPieChartData().getTotalValue());
    }
}
