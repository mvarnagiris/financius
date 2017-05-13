package com.code44.finance.ui.reports.categories;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.money.grouping.CategoryGroup;
import com.code44.finance.money.grouping.CategoryTagAmountGroups;
import com.code44.finance.money.grouping.CategoryTagGroup;
import com.code44.finance.money.grouping.TransactionValidator;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.DrawerActivity;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.utils.CategoryUtils;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.interval.ActiveInterval;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class CategoriesReportActivity extends DrawerActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TRANSACTIONS = 1;

    @Inject ActiveInterval activeInterval;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private CategoriesPieChartView categoriesPieChartView;

    private CategoriesReportAdapter adapter;
    private TransactionType transactionType = TransactionType.Expense;

    public static void start(Context context) {
        ActivityStarter.begin(context, CategoriesReportActivity.class).topLevel().showDrawer().showDrawerToggle().start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_report);

        // Get views
        categoriesPieChartView = (CategoriesPieChartView) findViewById(R.id.categoriesPieChartView);
        final ListView listView = (ListView) findViewById(R.id.listView);

        // Setup
        adapter = new CategoriesReportAdapter(this, amountFormatter);
        listView.setAdapter(adapter);
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Reports;
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.CategoriesReport;
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_TRANSACTIONS:
                return Tables.Transactions.getQuery()
                        .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(activeInterval.getInterval()
                                                                                                                   .getStartMillis()), String
                                           .valueOf(activeInterval.getInterval().getEndMillis() - 1))
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
        final CategoryTagAmountGroups categoryTagAmountGroups = new CategoryTagAmountGroups(currenciesManager, new TransactionValidator() {
            @Override public boolean isTransactionValid(Transaction transaction) {
                return transaction.getTransactionType() == transactionType && transaction.getTransactionState() == TransactionState.Confirmed && transaction
                        .includeInReports();
            }
        });
        final List<CategoryTagGroup> groups = categoryTagAmountGroups.getGroups(cursor);
        final List<SliceValue> sliceValues = new ArrayList<>();
        for (CategoryGroup group : groups) {
            sliceValues.add(new SliceValue(group.getValue(), CategoryUtils.getColor(this, group.getCategory(), transactionType), 0));
        }
        categoriesPieChartView.setPieChartData(new PieChartData(sliceValues), currenciesManager.getMainCurrencyCode());
        adapter.setData(groups);
    }
}
