package com.code44.finance.ui.overview;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartValue;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.ui.NavigationFragment;
import com.code44.finance.ui.transactions.TransactionEditActivity;
import com.code44.finance.utils.CategoriesExpenseComparator;
import com.code44.finance.utils.CurrentInterval;
import com.code44.finance.views.AccountsView;
import com.code44.finance.views.FabImageButton;
import com.code44.finance.views.OverviewGraphView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

public class OverviewFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int LOADER_TRANSACTIONS = 1;
    private static final int LOADER_ACCOUNTS = 2;

    @Inject CurrentInterval currentInterval;
    @Inject @Main Currency mainCurrency;

    private OverviewGraphView overviewGraph_V;
    private AccountsView accounts_V;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final FabImageButton newTransaction_FAB = (FabImageButton) view.findViewById(R.id.newTransaction_FAB);
        overviewGraph_V = (OverviewGraphView) view.findViewById(R.id.overviewGraph_V);
        accounts_V = (AccountsView) view.findViewById(R.id.accounts_V);

        // Setup
        newTransaction_FAB.setColorFilter(getResources().getColor(R.color.text_primary));
        newTransaction_FAB.setOnClickListener(this);
        overviewGraph_V.setOnClickListener(this);
        accounts_V.setOnClickListener(this);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
        getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
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
                        .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(currentInterval.getInterval().getStartMillis()), String.valueOf(currentInterval.getInterval().getEndMillis() - 1))
                        .asCursorLoader(getActivity(), TransactionsProvider.uriTransactions());
            case LOADER_ACCOUNTS:
                return Tables.Accounts.getQuery().selection(" and " + Tables.Accounts.INCLUDE_IN_TOTALS + "=?", "1").asCursorLoader(getActivity(), AccountsProvider.uriAccounts());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_TRANSACTIONS:
                onTransactionsLoaded(cursor);
                break;
            case LOADER_ACCOUNTS:
                onAccountsLoaded(cursor);
                break;
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newTransaction_FAB:
                TransactionEditActivity.start(getActivity(), null);
                break;
            case R.id.overviewGraph_V:
                getEventBus().post(new NavigationFragment.RequestNavigation(NavigationAdapter.NAV_ID_REPORTS));
                break;
            case R.id.accounts_V:
                getEventBus().post(new NavigationFragment.RequestNavigation(NavigationAdapter.NAV_ID_ACCOUNTS));
                break;
        }
    }

    @Subscribe public void onCurrentIntervalChanged(CurrentInterval currentInterval) {
        getLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
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
        final PieChartData.Builder builder = PieChartData.builder();
        for (Category category : sortedExpenses.descendingKeySet()) {
            builder.addValues(new PieChartValue(sortedExpenses.get(category), category.getColor()));
        }
        final PieChartData pieChartData = builder.build();
        overviewGraph_V.setPieChartData(pieChartData);
        overviewGraph_V.setTotalExpense(pieChartData.getTotalValue());
    }

    private void onAccountsLoaded(Cursor cursor) {
        final List<Account> accounts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                accounts.add(Account.from(cursor));
            } while (cursor.moveToNext());
        }
        accounts_V.setAccounts(accounts);
    }
}
