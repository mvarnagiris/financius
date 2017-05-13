package com.code44.finance.ui.overview;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.money.grouping.CategoryAmountGroups;
import com.code44.finance.money.grouping.CategoryGroup;
import com.code44.finance.money.grouping.TransactionValidator;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.DrawerActivity;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.common.views.AccountsView;
import com.code44.finance.ui.reports.categories.CategoriesPieChartView;
import com.code44.finance.ui.reports.trends.DefaultTrendsChartPresenter;
import com.code44.finance.ui.reports.trends.TrendsChartPresenter;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.ui.transactions.edit.TransactionEditActivity;
import com.code44.finance.utils.CategoryUtils;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.interval.CurrentInterval;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class OverviewActivity extends DrawerActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int LOADER_TRANSACTIONS = 1;
    private static final int LOADER_ACCOUNTS = 2;

    @Inject CurrentInterval currentInterval;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private CategoriesPieChartView categoriesPieChartView;
    private AccountsView accountsView;
    private TrendsChartPresenter trendsChartPresenter;

    public static void start(Context context) {
        ActivityStarter.begin(context, OverviewActivity.class).topLevel().showDrawer().showDrawerToggle().start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Get views
        final FloatingActionButton newTransactionButton = (FloatingActionButton) findViewById(R.id.newTransactionButton);
        final TrendsChartView trendsChartView = (TrendsChartView) findViewById(R.id.trendsChartView);
        categoriesPieChartView = (CategoriesPieChartView) findViewById(R.id.categoriesPieChartView);
        accountsView = (AccountsView) findViewById(R.id.accounts);

        // Setup
        trendsChartPresenter = new DefaultTrendsChartPresenter(trendsChartView, amountFormatter, currenciesManager);
        categoriesPieChartView.setOnClickListener(this);
        accountsView.setOnClickListener(this);
        trendsChartView.setOnClickListener(this);
        newTransactionButton.setOnClickListener(this);
        final Drawable drawable = getResources().getDrawable(R.drawable.ic_action_new).mutate();
        drawable.setColorFilter(ThemeUtils.getColor(this, R.attr.backgroundColorPrimaryInverse), PorterDuff.Mode.SRC_ATOP);
        newTransactionButton.setIconDrawable(drawable);

        // Loader
        getSupportLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
        getSupportLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Overview;
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Overview;
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_TRANSACTIONS:
                return Tables.Transactions.getQuery()
                        .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(currentInterval.getInterval()
                                                                                                                   .getStartMillis()), String
                                           .valueOf(currentInterval.getInterval().getEndMillis() - 1))
                        .clearSort()
                        .sortOrder(Tables.Transactions.DATE.getName())
                        .asCursorLoader(this, TransactionsProvider.uriTransactions());
            case LOADER_ACCOUNTS:
                return Tables.Accounts.getQuery()
                        .selection(" and " + Tables.Accounts.INCLUDE_IN_TOTALS + "=?", "1")
                        .asCursorLoader(this, AccountsProvider.uriAccounts());
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
            case R.id.newTransactionButton:
                TransactionEditActivity.start(this, null);
                break;
            case R.id.categoriesPieChartView:
                onNavigationItemSelected(NavigationScreen.Reports);
                break;
            case R.id.accounts:
                onNavigationItemSelected(NavigationScreen.Accounts);
                break;
            case R.id.trendsChartView:
                onNavigationItemSelected(NavigationScreen.Transactions);
                break;
        }
    }

    @Subscribe public void onCurrentIntervalChanged(CurrentInterval currentInterval) {
        getSupportLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
        getSupportActionBar().setTitle(currentInterval.getTitle());
    }

    private void onTransactionsLoaded(Cursor cursor) {
        final CategoryAmountGroups categoryAmountGroups = new CategoryAmountGroups(currenciesManager, new TransactionValidator() {
            @Override public boolean isTransactionValid(Transaction transaction) {
                return transaction.getTransactionType() == TransactionType.Expense && transaction.getTransactionState() == TransactionState.Confirmed && transaction
                        .includeInReports();
            }
        });
        final List<CategoryGroup> groups = categoryAmountGroups.getGroups(cursor);
        final List<SliceValue> sliceValues = new ArrayList<>();
        for (CategoryGroup group : groups) {
            sliceValues.add(new SliceValue(group.getValue(), CategoryUtils.getColor(this, group.getCategory(), TransactionType.Expense), 0));
        }

        categoriesPieChartView.setPieChartData(new PieChartData(sliceValues), currenciesManager.getMainCurrencyCode());
        trendsChartPresenter.setData(cursor, currentInterval);
    }

    private void onAccountsLoaded(Cursor cursor) {
        final List<Account> accounts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                accounts.add(Account.from(cursor));
            } while (cursor.moveToNext());
        }
        accountsView.setAccounts(accounts);
    }
}
