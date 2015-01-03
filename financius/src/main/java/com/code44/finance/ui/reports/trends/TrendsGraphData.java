package com.code44.finance.ui.reports.trends;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.graphs.line.LineGraphData;
import com.code44.finance.graphs.line.LineGraphValue;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.ThemeUtils;

import org.joda.time.Interval;
import org.joda.time.Period;

public class TrendsGraphData {
    private final long totalIncome;
    private final long totalExpense;
    private final LineGraphData lineGraphData;

    public TrendsGraphData(Context context, Cursor cursor, Currency mainCurrency, BaseInterval interval) {
        long totalIncome = 0;
        long totalExpense = 0;
        final LineGraphData.Builder builder = new LineGraphData.Builder()
                .setColor(ThemeUtils.getColor(context, R.attr.textColorNegative))
                .setLineWidth(context.getResources().getDimension(R.dimen.divider))
                .setDividerDrawable(context.getResources().getDrawable(R.drawable.trends_divider))
                .setUseGlobalMinMax(false)
                .setSmooth(true);
        final Period period = getPeriod(interval);
        Interval currentInterval = getFirstInterval(interval, period);
        long expense = 0;
        final Interval lastInterval = getLastInterval(interval, period);
        if (cursor.moveToFirst()) {
            do {
                final Transaction transaction = Transaction.from(cursor);
                if (isTransactionValid(transaction)) {
                    final long amount = getAmount(transaction, mainCurrency);

                    if (transaction.getTransactionType() == TransactionType.Expense) {
                        totalExpense += amount;

                        if (currentInterval.contains(transaction.getDate())) {
                            expense += amount;
                        } else {
                            do {
                                builder.addValue(new LineGraphValue(expense));
                                expense = 0;
                                currentInterval = new Interval(currentInterval.getEnd(), period);
                            }
                            while (!currentInterval.contains(transaction.getDate()));
                            expense = amount;
                        }
                    } else {
                        totalIncome += amount;
                    }
                }
            } while (cursor.moveToNext());
        }

        builder.addValue(new LineGraphValue(expense));
        if (!currentInterval.equals(lastInterval) && !currentInterval.isAfter(lastInterval)) {
            do {
                builder.addValue(new LineGraphValue(0));
                currentInterval = new Interval(currentInterval.getEnd(), period);
            }
            while (!currentInterval.equals(lastInterval));
        }


        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.lineGraphData = builder.build();
    }

    public long getTotalIncome() {
        return totalIncome;
    }

    public long getTotalExpense() {
        return totalExpense;
    }

    public LineGraphData getLineGraphData() {
        return lineGraphData;
    }

    private boolean isTransactionValid(Transaction transaction) {
        return transaction.includeInReports() && transaction.getTransactionType() != TransactionType.Transfer && transaction.getTransactionState() == TransactionState.Confirmed;
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
}
