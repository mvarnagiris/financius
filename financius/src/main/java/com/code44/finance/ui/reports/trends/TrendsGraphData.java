package com.code44.finance.ui.reports.trends;

import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.graphs.line.LineGraphData;
import com.code44.finance.graphs.line.LineGraphValue;
import com.code44.finance.utils.BaseInterval;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrendsGraphData {
    private final TrendOptions[] trendOptionsList;
    private final List<LineGraphData> lineGraphDataList;

    public TrendsGraphData(TrendOptions... trendOptionsList) {
        this.trendOptionsList = trendOptionsList;
        this.lineGraphDataList = new ArrayList<>();
    }

    public void init(Cursor cursor, Currency mainCurrency, BaseInterval interval) {
        final Map<TrendOptions, LineGraphData.Builder> builderMap = prepareLineGraphDataBuilders();
        final Map<TrendOptions, Long> amounts = new HashMap<>();
        final Period period = getPeriod(interval);
        final Interval lastInterval = getLastInterval(interval, period);
        Interval currentInterval = getFirstInterval(interval, period);
        Transaction transaction = cursor.moveToFirst() ? Transaction.from(cursor) : null;
        while (!currentInterval.equals(lastInterval) && !currentInterval.isAfter(lastInterval)) {
            if (transaction == null || currentInterval.isAfter(transaction.getDate())) {
                onIntervalDone(builderMap, amounts);
                currentInterval = getNextInterval(currentInterval, period);
                continue;
            }

            while (transaction != null && currentInterval.contains(transaction.getDate())) {
                onTransactionInInterval(transaction, amounts, mainCurrency);
                transaction = cursor.moveToNext() ? Transaction.from(cursor) : null;
            }

            onIntervalDone(builderMap, amounts);
            currentInterval = getNextInterval(currentInterval, period);
        }

        lineGraphDataList.clear();
        for (TrendOptions trendOptions : trendOptionsList) {
            lineGraphDataList.add(builderMap.get(trendOptions).build());
        }
    }

    public LineGraphData[] getLineGraphData() {
        return lineGraphDataList.toArray(new LineGraphData[lineGraphDataList.size()]);
    }

    private Map<TrendOptions, LineGraphData.Builder> prepareLineGraphDataBuilders() {
        final Map<TrendOptions, LineGraphData.Builder> builderMap = new HashMap<>();
        for (TrendOptions trendOptions : trendOptionsList) {
            builderMap.put(trendOptions, new LineGraphData.Builder()
                    .setColor(trendOptions.color)
                    .setLineWidth(trendOptions.lineWidth)
                    .setDividerDrawable(trendOptions.dividerDrawable)
                    .setUseGlobalMinMax(false)
                    .setSmooth(true));
        }
        return builderMap;
    }

    private void onIntervalDone(Map<TrendOptions, LineGraphData.Builder> builderMap, Map<TrendOptions, Long> amounts) {
        for (TrendOptions trendOptions : trendOptionsList) {
            Long amount = amounts.get(trendOptions);
            if (amount == null) {
                amount = 0L;
            }
            builderMap.get(trendOptions).addValue(new LineGraphValue(amount));
        }

        amounts.clear();
    }

    private void onTransactionInInterval(Transaction transaction, Map<TrendOptions, Long> amounts, Currency mainCurrency) {
        for (TrendOptions trendOptions : trendOptionsList) {
            if (trendOptions.transactionValidator.isTransactionValid(transaction)) {
                Long amount = amounts.get(trendOptions);
                if (amount == null) {
                    amount = 0L;
                }

                amount += getAmount(transaction, mainCurrency);
                amounts.put(trendOptions, amount);
            }
        }
    }

    private long getAmount(Transaction transaction, Currency mainCurrency) {
        final Currency currency = transaction.getTransactionType() == TransactionType.Expense ? transaction.getAccountFrom().getCurrency() : transaction.getAccountTo().getCurrency();
        if (currency.getId().equals(mainCurrency.getId())) {
            return transaction.getAmount();
        } else {
            return Math.round(transaction.getAmount() * currency.getExchangeRate());
        }
    }

    private Period getPeriod(BaseInterval interval) {
        switch (interval.getType()) {
            case DAY:
                return Period.hours(1);
            case WEEK:
            case MONTH:
                return Period.days(1);
            case YEAR:
                return Period.months(1);
            default:
                throw new IllegalArgumentException("Type " + interval.getType() + " is not supported.");
        }
    }

    private Interval getFirstInterval(BaseInterval interval, Period period) {
        return new Interval(interval.getInterval().getStart(), period);
    }

    private Interval getLastInterval(BaseInterval interval, Period period) {
        return new Interval(period, interval.getInterval().getEnd());
    }

    private Interval getNextInterval(Interval interval, Period period) {
        return new Interval(interval.getEnd(), period);
    }

    public static interface TransactionValidator {
        public boolean isTransactionValid(Transaction transaction);
    }

    public static class TrendOptions {
        private final int color;
        private final float lineWidth;
        private final Drawable dividerDrawable;
        private final TransactionValidator transactionValidator;

        public TrendOptions(int color, float lineWidth, Drawable dividerDrawable, TransactionValidator transactionValidator) {
            this.color = color;
            this.lineWidth = lineWidth;
            this.dividerDrawable = dividerDrawable;
            this.transactionValidator = transactionValidator;
        }
    }
}
