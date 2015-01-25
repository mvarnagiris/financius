package com.code44.finance.ui.tags.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.reports.AmountGroups;
import com.code44.finance.ui.reports.trends.TrendsChartPresenter;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.interval.BaseInterval;

import lecho.lib.hellocharts.model.Line;

class TagTrendsChartPresenter extends TrendsChartPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TAG_TRENDS = 712;

    private final LoaderManager loaderManager;
    private final ExpenseAmountCalculator expenseValidator;
    private final IncomeAmountCalculator incomeValidator;
    private final TransferAmountCalculator transferValidator;
    private BaseInterval baseInterval;
    private Tag tag;

    public TagTrendsChartPresenter(TrendsChartView trendsChartView, CurrencyFormat mainCurrencyFormat, LoaderManager loaderManager, BaseInterval baseInterval) {
        super(trendsChartView, mainCurrencyFormat);
        this.loaderManager = loaderManager;
        expenseValidator = new ExpenseAmountCalculator();
        incomeValidator = new IncomeAmountCalculator();
        transferValidator = new TransferAmountCalculator();
        setData(null, baseInterval);
    }

    @Override protected AmountGroups.AmountCalculator[] getTransactionValidators() {
        return new AmountGroups.AmountCalculator[]{expenseValidator, incomeValidator, transferValidator};
    }

    @Override protected void onLineCreated(AmountGroups.AmountCalculator amountCalculator, Line line) {
        if (amountCalculator.equals(expenseValidator)) {
            line.setColor(ThemeUtils.getColor(getContext(), R.attr.textColorNegative));
        } else if (amountCalculator.equals(incomeValidator)) {
            line.setColor(ThemeUtils.getColor(getContext(), R.attr.textColorPositive));
        } else {
            line.setColor(ThemeUtils.getColor(getContext(), R.attr.textColorNeutral));
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_TAG_TRENDS) {
            return Tables.Transactions
                    .getQuery()
                    .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(baseInterval.getInterval().getStartMillis()), String.valueOf(baseInterval.getInterval().getEndMillis() - 1))
                    .selection(" and " + Tables.TransactionTags.TAG_ID + "=?", tag != null ? tag.getId() : "0")
                    .selection(" and " + Tables.Transactions.INCLUDE_IN_REPORTS + "=?", "1")
                    .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                    .clearSort()
                    .sortOrder(Tables.Transactions.DATE.getName())
                    .asCursorLoader(getContext(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_TAG_TRENDS) {
            setData(data, baseInterval);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void setTagAndInterval(Tag tag, BaseInterval baseInterval) {
        this.tag = tag;
        this.baseInterval = baseInterval;
        loaderManager.restartLoader(LOADER_TAG_TRENDS, null, this);
    }

    private static class ExpenseAmountCalculator implements AmountGroups.AmountCalculator {
        @Override public boolean isTransactionValid(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Expense;
        }
    }

    private static class IncomeAmountCalculator implements AmountGroups.AmountCalculator {
        @Override public boolean isTransactionValid(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Income;
        }
    }

    private static class TransferAmountCalculator implements AmountGroups.AmountCalculator {
        @Override public boolean isTransactionValid(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Transfer;
        }
    }
}
