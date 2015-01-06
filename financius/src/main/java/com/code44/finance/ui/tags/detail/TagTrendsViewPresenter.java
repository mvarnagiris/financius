package com.code44.finance.ui.tags.detail;

import android.content.Context;
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
import com.code44.finance.ui.common.presenters.ViewPresenter;
import com.code44.finance.ui.reports.AmountGroups;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class TagTrendsViewPresenter extends ViewPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TAG_TRENDS = 712;

    private final LineChartView lineChartView;
    private final BaseInterval baseInterval;
    private final Currency mainCurrency;
    private final LoaderManager loaderManager;
    private final AmountGroups amountGroups;
    private final ExpenseTransactionValidator expenseValidator;
    private final IncomeTransactionValidator incomeValidator;
    private final TransferTransactionValidator transferValidator;
    private Tag tag;

    public TagTrendsViewPresenter(LineChartView lineChartView, LoaderManager loaderManager, BaseInterval baseInterval, Currency mainCurrency) {
        this.lineChartView = lineChartView;
        this.loaderManager = loaderManager;
        this.baseInterval = baseInterval;
        this.mainCurrency = mainCurrency;
        amountGroups = new AmountGroups(baseInterval);
        expenseValidator = new ExpenseTransactionValidator();
        incomeValidator = new IncomeTransactionValidator();
        transferValidator = new TransferTransactionValidator();
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
                    .asCursorLoader(lineChartView.getContext(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_TAG_TRENDS) {
            onTransactionsLoaded(data);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        loaderManager.restartLoader(LOADER_TAG_TRENDS, null, this);
    }

    private void onTransactionsLoaded(Cursor cursor) {
        final Map<AmountGroups.TransactionValidator, List<Long>> groups = amountGroups.getGroups(cursor, mainCurrency, expenseValidator, incomeValidator, transferValidator);
        final Context context = lineChartView.getContext();

        final Line expenseLine = getLine(groups.get(expenseValidator))
                .setColor(ThemeUtils.getColor(context, R.attr.textColorNegative))
                .setHasLabels(true);
        final Line incomeLine = getLine(groups.get(incomeValidator)).setColor(ThemeUtils.getColor(context, R.attr.textColorPositive));
        final Line transferLine = getLine(groups.get(transferValidator)).setColor(ThemeUtils.getColor(context, R.attr.textColorNeutral));

        final List<Line> lines = new ArrayList<>();
        lines.add(transferLine);
        lines.add(incomeLine);
        lines.add(expenseLine);

        final LineChartData lineChartData = new LineChartData(lines);
        lineChartView.setLineChartData(lineChartData);
    }

    private Line getLine(List<Long> amounts) {
        final List<PointValue> points = new ArrayList<>();
        int index = 0;
        for (Long amount : amounts) {
            points.add(new PointValue(index++, amount));
        }

        return new Line(points)
                .setCubic(true)
                .setStrokeWidth(lineChartView.getResources().getDimensionPixelSize(R.dimen.report_trend_graph_width))
                .setHasPoints(false);
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
