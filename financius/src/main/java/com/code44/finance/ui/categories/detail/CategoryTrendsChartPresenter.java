package com.code44.finance.ui.categories.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.reports.AmountGroups;
import com.code44.finance.ui.reports.trends.TrendsChartPresenter;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.utils.BaseInterval;

import lecho.lib.hellocharts.model.Line;

class CategoryTrendsChartPresenter extends TrendsChartPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_CATEGORY_TRENDS = 712;

    private final LoaderManager loaderManager;
    private final AmountGroups.AmountCalculator amountCalculator;
    private BaseInterval baseInterval;
    private Category category;

    public CategoryTrendsChartPresenter(TrendsChartView trendsChartView, CurrencyFormat mainCurrencyFormat, LoaderManager loaderManager, BaseInterval baseInterval) {
        super(trendsChartView, mainCurrencyFormat);
        this.loaderManager = loaderManager;
        amountCalculator = new CategoryAmountCalculator();
        setData(null, baseInterval);
    }

    @Override protected AmountGroups.AmountCalculator[] getTransactionValidators() {
        return new AmountGroups.AmountCalculator[]{amountCalculator};
    }

    @Override protected void onLineCreated(AmountGroups.AmountCalculator amountCalculator, Line line) {
        if (category != null) {
            line.setColor(category.getColor());
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CATEGORY_TRENDS) {
            return Tables.Transactions
                    .getQuery()
                    .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(baseInterval.getInterval().getStartMillis()), String.valueOf(baseInterval.getInterval().getEndMillis() - 1))
                    .selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", category != null ? category.getId() : "0")
                    .selection(" and " + Tables.Transactions.INCLUDE_IN_REPORTS + "=?", "1")
                    .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                    .clearSort()
                    .sortOrder(Tables.Transactions.DATE.getName())
                    .asCursorLoader(getContext(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CATEGORY_TRENDS) {
            setData(data, baseInterval);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void setCategoryAndInterval(Category category, BaseInterval baseInterval) {
        this.category = category;
        this.baseInterval = baseInterval;
        loaderManager.restartLoader(LOADER_CATEGORY_TRENDS, null, this);
    }

    private static class CategoryAmountCalculator implements AmountGroups.AmountCalculator {
        @Override public boolean isTransactionValid(Transaction transaction) {
            return true;
        }
    }
}
