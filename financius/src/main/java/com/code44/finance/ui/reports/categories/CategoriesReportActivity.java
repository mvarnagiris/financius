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
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.reports.BaseReportActivity;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.utils.interval.ActiveInterval;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class CategoriesReportActivity extends BaseReportActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TRANSACTIONS = 1;

    @Inject ActiveInterval activeInterval;
    @Inject CurrencyFormat mainCurrencyFormat;

    private CategoriesReportView categoriesReportView;

    private CategoriesReportAdapter adapter;
    private TransactionType transactionType = TransactionType.Expense;

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
        final ListView listView = (ListView) findViewById(R.id.listView);

        // Setup
        adapter = new CategoriesReportAdapter(this, mainCurrencyFormat);
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
                        .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(activeInterval.getInterval().getStartMillis()), String.valueOf(activeInterval.getInterval().getEndMillis() - 1))
                        .asCursorLoader(this, TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_TRANSACTIONS:
                onTransactionsLoaded(cursor);
                break;
        }
    }

    @Override public void onLoaderReset(final Loader<Cursor> loader) {
    }

    @Subscribe public void onActiveIntervalChanged(ActiveInterval interval) {
        getSupportLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
    }

    private void onTransactionsLoaded(Cursor cursor) {
        final CategoriesReportData categoriesReportData = new CategoriesReportData(this, cursor, mainCurrencyFormat, transactionType);
        categoriesReportView.setPieChartData(categoriesReportData.getPieChartData());
        categoriesReportView.setTotalExpense(categoriesReportData.getPieChartData().getTotalValue());
        adapter.setData(categoriesReportData, categoriesReportData.getPieChartData().getTotalValue());
    }
}
