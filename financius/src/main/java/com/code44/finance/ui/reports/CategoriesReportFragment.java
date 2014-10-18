package com.code44.finance.ui.reports;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.CategoriesReportAdapter;
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
import com.code44.finance.views.CategoriesReportView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

public class CategoriesReportFragment extends BaseReportFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TRANSACTIONS = 1;

    @Inject ActiveInterval activeInterval;
    @Inject @Main Currency mainCurrency;

    private CategoriesReportView categoriesReport_V;

    private CategoriesReportAdapter adapter;

    public static CategoriesReportFragment newInstance() {
        return new CategoriesReportFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories_report, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        categoriesReport_V = (CategoriesReportView) view.findViewById(R.id.categoriesReport_V);
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        adapter = new CategoriesReportAdapter(getActivity(), mainCurrency);
        list_V.setAdapter(adapter);
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
                        .asCursorLoader(getActivity(), TransactionsProvider.uriTransactions());
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
        getLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
    }

    private void onTransactionsLoaded(Cursor cursor) {
        final Map<Category, Long> expenses = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                final Transaction transaction = Transaction.from(cursor);
                final Category category = transaction.getCategory();
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
        categoriesReport_V.setPieChartData(pieChartData);
        categoriesReport_V.setTotalExpense(pieChartData.getTotalValue());
        adapter.setItems(items, pieChartData.getTotalValue());
    }
}
