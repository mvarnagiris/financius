package com.code44.finance.ui.tags.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.reports.AmountGroups;
import com.code44.finance.ui.reports.trends.TrendsChartPresenter;
import com.code44.finance.ui.reports.trends.TrendsChartView;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.ThemeUtils;

import lecho.lib.hellocharts.model.Line;

public class TagTrendsChartPresenter extends TrendsChartPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TAG_TRENDS = 712;

    private final LoaderManager loaderManager;
    private final ExpenseTransactionValidator expenseValidator;
    private final IncomeTransactionValidator incomeValidator;
    private final TransferTransactionValidator transferValidator;
    private BaseInterval baseInterval;
    private Tag tag;

    public TagTrendsChartPresenter(TrendsChartView trendsChartView, Currency mainCurrency, LoaderManager loaderManager, BaseInterval baseInterval) {
        super(trendsChartView, mainCurrency);
        this.loaderManager = loaderManager;
        expenseValidator = new ExpenseTransactionValidator();
        incomeValidator = new IncomeTransactionValidator();
        transferValidator = new TransferTransactionValidator();
        setData(null, baseInterval);
    }

    @Override protected AmountGroups.TransactionValidator[] getTransactionValidators() {
        return new AmountGroups.TransactionValidator[]{expenseValidator, incomeValidator, transferValidator};
    }

    @Override protected void onLineCreated(AmountGroups.TransactionValidator transactionValidator, Line line) {
        if (transactionValidator.equals(expenseValidator)) {
            line.setColor(ThemeUtils.getColor(getContext(), R.attr.textColorNegative));
        } else if (transactionValidator.equals(incomeValidator)) {
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

    private static class ExpenseTransactionValidator implements AmountGroups.TransactionValidator {
        @Override public boolean isTransactionValid(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Expense;
        }
    }

    private static class IncomeTransactionValidator implements AmountGroups.TransactionValidator {
        @Override public boolean isTransactionValid(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Income;
        }
    }

    private static class TransferTransactionValidator implements AmountGroups.TransactionValidator {
        @Override public boolean isTransactionValid(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Transfer;
        }
    }
}
