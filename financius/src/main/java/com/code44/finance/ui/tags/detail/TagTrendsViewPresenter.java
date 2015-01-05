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
import com.code44.finance.ui.reports.trends.TrendsGraphData;
import com.code44.finance.ui.reports.trends.TrendsGraphView;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.ThemeUtils;

public class TagTrendsViewPresenter extends ViewPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TAG_TRENDS = 712;

    private final TrendsGraphView trendsGraphView;
    private final BaseInterval interval;
    private final Currency mainCurrency;
    private final LoaderManager loaderManager;
    private final TrendsGraphData trendsGraphData;
    private Tag tag;

    public TagTrendsViewPresenter(TrendsGraphView view, BaseInterval interval, Currency mainCurrency, LoaderManager loaderManager) {
        this.trendsGraphView = view;
        this.interval = interval;
        this.mainCurrency = mainCurrency;
        this.loaderManager = loaderManager;

        final Context context = view.getContext();
        trendsGraphData = new TrendsGraphData(getExpenseTrendOptions(context), getIncomeTrendOptions(context), getTransferTrendOptions(context));
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_TAG_TRENDS) {
            return Tables.Transactions
                    .getQuery()
                    .selection(" and " + Tables.Transactions.DATE + " between ? and ?", String.valueOf(interval.getInterval().getStartMillis()), String.valueOf(interval.getInterval().getEndMillis() - 1))
                    .selection(" and " + Tables.TransactionTags.TAG_ID + "=?", tag != null ? tag.getId() : "0")
                    .clearSort()
                    .sortOrder(Tables.Transactions.DATE.getName())
                    .asCursorLoader(trendsGraphView.getContext(), TransactionsProvider.uriTransactions());
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
        trendsGraphData.init(cursor, mainCurrency, interval);
        trendsGraphView.setLineGraphData(trendsGraphData.getLineGraphData());
    }

    private TrendsGraphData.TrendOptions getExpenseTrendOptions(Context context) {
        return new TrendsGraphData.TrendOptions(ThemeUtils.getColor(context, R.attr.textColorNegative), context.getResources().getDimension(R.dimen.report_trend_graph_width), null, new TrendsGraphData.TransactionValidator() {
            @Override public boolean isTransactionValid(Transaction transaction) {
                return transaction.includeInReports() && transaction.getTransactionType() == TransactionType.Expense && transaction.getTransactionState() == TransactionState.Confirmed;
            }
        });
    }

    private TrendsGraphData.TrendOptions getIncomeTrendOptions(Context context) {
        return new TrendsGraphData.TrendOptions(ThemeUtils.getColor(context, R.attr.textColorPositive), context.getResources().getDimension(R.dimen.report_trend_graph_width), null, new TrendsGraphData.TransactionValidator() {
            @Override public boolean isTransactionValid(Transaction transaction) {
                return transaction.includeInReports() && transaction.getTransactionType() == TransactionType.Income && transaction.getTransactionState() == TransactionState.Confirmed;
            }
        });
    }

    private TrendsGraphData.TrendOptions getTransferTrendOptions(Context context) {
        return new TrendsGraphData.TrendOptions(ThemeUtils.getColor(context, R.attr.textColorNeutral), context.getResources().getDimension(R.dimen.report_trend_graph_width), null, new TrendsGraphData.TransactionValidator() {
            @Override public boolean isTransactionValid(Transaction transaction) {
                return transaction.includeInReports() && transaction.getTransactionType() == TransactionType.Transfer && transaction.getTransactionState() == TransactionState.Confirmed;
            }
        });
    }
}
