package com.code44.finance.ui.reports;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.ReportsAdapter;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.utils.interval.ActiveInterval;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class ReportsFragment extends BaseReportFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TRANSACTIONS = 1;

    @Inject ActiveInterval activeInterval;
    @Inject @Main CurrencyFormat mainCurrencyFormat;

    private ReportsAdapter adapter;

    public static ReportsFragment newInstance() {
        return new ReportsFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final RecyclerView recycler_V = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Setup
        adapter = new ReportsAdapter(getActivity());
        //recycler_V.setLayoutManager(new ListLayoutManager(getActivity(), TwoWayLayoutManager.Orientation.VERTICAL));
        //recycler_V.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_normal), getResources().getDimensionPixelSize(R.dimen.space_normal)));
        recycler_V.setItemAnimator(new DefaultItemAnimator());
        recycler_V.setAdapter(adapter);
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
//        final Map<Category, Long> expenses = new HashMap<>();
//        if (cursor.moveToFirst()) {
//            do {
//                final Transaction transaction = Transaction.from(cursor);
//                final Category category = transaction.getCategory();
//                if (transaction.includeInReports() && transaction.getTransactionType() == TransactionType.Expense && transaction.getTransactionState() == TransactionState.Confirmed) {
//                    final Long amount;
//                    if (transaction.getAccountFrom().getCurrency().getId().equals(mainCurrency.getId())) {
//                        amount = transaction.getAmount();
//                    } else {
//                        amount = Math.round(transaction.getAmount() * transaction.getAccountFrom().getCurrency().getExchangeRate());
//                    }
//
//                    Long totalExpenseForCategory = expenses.get(category);
//                    if (totalExpenseForCategory == null) {
//                        totalExpenseForCategory = amount;
//                    } else {
//                        totalExpenseForCategory += amount;
//                    }
//                    expenses.put(category, totalExpenseForCategory);
//                }
//            } while (cursor.moveToNext());
//        }
//
//        final TreeMap<Category, Long> sortedExpenses = new TreeMap<>(new CategoryExpenseComparator(expenses));
//        sortedExpenses.putAll(expenses);
//        final PieChartData.Builder builder = PieChartData.builder();
//        for (Category category : sortedExpenses.descendingKeySet()) {
//            builder.addValues(new PieChartValue(sortedExpenses.get(category), category.getColor()));
//        }
//        final PieChartData pieChartData = builder.build();
//        adapter.setCategoriesReportData(pieChartData);
    }
}
