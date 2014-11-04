package com.code44.finance.ui.reports;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.CategoriesReportAdapter;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartValue;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.utils.ActiveInterval;
import com.code44.finance.utils.CategoriesExpenseComparator;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.views.CategoriesReportView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

public class CategoriesReportActivity extends BaseReportActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TRANSACTIONS = 1;

    @Inject ActiveInterval activeInterval;
    @Inject @Main Currency mainCurrency;

    private CategoriesReportView categoriesReportView;

    private CategoriesReportAdapter adapter;

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
        adapter = new CategoriesReportAdapter(this, mainCurrency);
        listView.setAdapter(adapter);
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return NavigationAdapter.NavigationScreen.Reports;
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
        final Map<Category, Long> expenses = new HashMap<>();
        if (cursor.moveToFirst()) {
            final Category noCategory = new Category();
            noCategory.setId("0");
            noCategory.setTitle(getString(R.string.no_category));
            noCategory.setColor(getResources().getColor(R.color.text_neutral));
            do {
                final Transaction transaction = Transaction.from(cursor);
                final Category category = transaction.getCategory() == null ? noCategory : transaction.getCategory();
                if (transaction.includeInReports() && transaction.getTransactionType() == TransactionType.Expense && transaction.getTransactionState() == TransactionState.Confirmed) {
                    final Long amount;
                    if (transaction.getAccountFrom().getCurrency().getId().equals(mainCurrency.getId())) {
                        amount = transaction.getAmount();
                    } else {
                        amount = Math.round(transaction.getAmount() * transaction.getAccountFrom().getCurrency().getExchangeRate());
                    }

                    Long totalExpenseForCategory = expenses.get(category);
                    if (totalExpenseForCategory == null) {
                        totalExpenseForCategory = amount;
                    } else {
                        totalExpenseForCategory += amount;
                    }
                    expenses.put(category, totalExpenseForCategory);
                }
            } while (cursor.moveToNext());
        }

        final TreeMap<Category, Long> sortedExpenses = new TreeMap<>(new CategoriesExpenseComparator(expenses));
        sortedExpenses.putAll(expenses);
        final List<Pair<Category, Long>> items = new ArrayList<>();
        final PieChartData.Builder builder = PieChartData.builder();
        for (Category category : sortedExpenses.descendingKeySet()) {
            final Long amount = sortedExpenses.get(category);
            builder.addValues(new PieChartValue(sortedExpenses.get(category), category.getColor()));
            items.add(Pair.create(category, amount));
        }
        final PieChartData pieChartData = builder.build();
        categoriesReportView.setPieChartData(pieChartData);
        categoriesReportView.setTotalExpense(pieChartData.getTotalValue());
        adapter.setItems(items, pieChartData.getTotalValue());
    }
}
