package com.code44.finance.ui.tags.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.AmountGrouper;
import com.code44.finance.money.AmountRetriever;
import com.code44.finance.money.CurrenciesManager;
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

    public TagTrendsChartPresenter(TrendsChartView trendsChartView, CurrenciesManager currenciesManager, AmountFormatter amountFormatter, LoaderManager loaderManager, BaseInterval baseInterval) {
        super(trendsChartView, amountFormatter);
        this.loaderManager = loaderManager;
        expenseValidator = new ExpenseAmountCalculator(currenciesManager);
        incomeValidator = new IncomeAmountCalculator(currenciesManager);
        transferValidator = new TransferAmountCalculator(currenciesManager);
        setData(null, baseInterval);
    }

    @Override protected AmountGrouper.AmountCalculator[] getTransactionValidators() {
        return new AmountGrouper.AmountCalculator[]{transferValidator, incomeValidator, expenseValidator};
    }

    @Override protected void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line) {
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
            return Tables.Transactions.getQuery()
                    .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(baseInterval.getInterval()
                                                                                                               .getStartMillis()), String.valueOf(baseInterval
                                                                                                                                                          .getInterval()
                                                                                                                                                          .getEndMillis() - 1))
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

    private static class ExpenseAmountCalculator implements AmountGrouper.AmountCalculator {
        private final CurrenciesManager currenciesManager;

        private ExpenseAmountCalculator(CurrenciesManager currenciesManager) {
            this.currenciesManager = currenciesManager;
        }

        @Override public long getAmount(Transaction transaction) {
            return AmountRetriever.getExpenseAmount(transaction, currenciesManager, currenciesManager.getMainCurrencyCode());
        }
    }

    private static class IncomeAmountCalculator implements AmountGrouper.AmountCalculator {
        private final CurrenciesManager currenciesManager;

        private IncomeAmountCalculator(CurrenciesManager currenciesManager) {
            this.currenciesManager = currenciesManager;
        }

        @Override public long getAmount(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Income ? AmountRetriever.getIncomeAmount(transaction, currenciesManager, currenciesManager
                    .getMainCurrencyCode()) : 0;
        }
    }

    private static class TransferAmountCalculator implements AmountGrouper.AmountCalculator {
        private final CurrenciesManager currenciesManager;

        private TransferAmountCalculator(CurrenciesManager currenciesManager) {
            this.currenciesManager = currenciesManager;
        }

        @Override public long getAmount(Transaction transaction) {
            return transaction.getTransactionType() == TransactionType.Transfer ? AmountRetriever.getExpenseAmount(transaction, currenciesManager, currenciesManager
                    .getMainCurrencyCode()) : 0;
        }
    }
}
