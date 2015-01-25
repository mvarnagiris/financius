package com.code44.finance.ui.reports;

import android.database.Cursor;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.BaseInterval;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmountGroups {
    private final Period period;
    private final Interval wholeInterval;
    private final Interval firstInterval;

    public AmountGroups(Interval interval, BaseInterval.Type intervalType) {
        period = getPeriod(intervalType);
        wholeInterval = interval;
        firstInterval = new Interval(interval.getStart(), period);
    }

    public Map<AmountCalculator, List<Long>> getGroups(Cursor cursor, CurrencyFormat mainCurrencyFormat, AmountCalculator... amountCalculators) {
        final Map<AmountCalculator, List<Long>> groups = new HashMap<>();
        for (AmountCalculator amountCalculator : amountCalculators) {
            groups.put(amountCalculator, new ArrayList<Long>());
        }

        final Map<AmountCalculator, Long> intervalAmounts = new HashMap<>();
        Interval interval = firstInterval;
        Transaction transaction = cursor != null && cursor.moveToFirst() ? Transaction.from(cursor) : null;
        while (isNotAfterLastInterval(interval)) {
            if (transaction == null || interval.isAfter(transaction.getDate())) {
                onIntervalDone(groups, intervalAmounts);
                interval = getNextInterval(interval, period);
                continue;
            }

            while (transaction != null && interval.contains(transaction.getDate())) {
                onTransactionInInterval(transaction, groups.keySet(), intervalAmounts, mainCurrencyFormat);
                transaction = cursor.moveToNext() ? Transaction.from(cursor) : null;
            }

            onIntervalDone(groups, intervalAmounts);
            interval = getNextInterval(interval, period);
        }

        return groups;
    }

    private Period getPeriod(BaseInterval.Type intervalType) {
        switch (intervalType) {
            case DAY:
                return Period.hours(1);
            case WEEK:
            case MONTH:
                return Period.days(1);
            case YEAR:
                return Period.months(1);
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
    }

    private Interval getNextInterval(Interval interval, Period period) {
        return new Interval(interval.getEnd(), period);
    }

    private boolean isNotAfterLastInterval(Interval interval) {
        return interval.overlaps(wholeInterval);
    }

    private void onIntervalDone(Map<AmountCalculator, List<Long>> groups, Map<AmountCalculator, Long> intervalAmounts) {
        for (AmountCalculator amountCalculator : groups.keySet()) {
            Long amount = intervalAmounts.get(amountCalculator);
            if (amount == null) {
                amount = 0L;
            }
            groups.get(amountCalculator).add(amount);
        }

        intervalAmounts.clear();
    }

    private void onTransactionInInterval(Transaction transaction, Set<AmountCalculator> amountCalculators, Map<AmountCalculator, Long> intervalAmounts, CurrencyFormat mainCurrencyFormat) {
        for (AmountCalculator amountCalculator : amountCalculators) {
            Long amount = intervalAmounts.get(amountCalculator);
            if (amount == null) {
                amount = 0L;
            }

            amount += getAmount(transaction, mainCurrencyFormat);
            intervalAmounts.put(amountCalculator, amount);
        }
    }

    private long getAmount(Transaction transaction, CurrencyFormat mainCurrencyFormat) {
        final CurrencyFormat currencyFormat = transaction.getTransactionType() != TransactionType.Income ? transaction.getAccountFrom().getCurrencyCode() : transaction.getAccountTo().getCurrencyCode();
        if (currencyFormat.getId().equals(mainCurrencyFormat.getId())) {
            return transaction.getAmount();
        } else {
            return Math.round(transaction.getAmount() * currencyFormat.getExchangeRate());
        }
    }

    public static interface AmountCalculator {
        public long getAmount(Transaction transaction);
    }

    public static class ExpenseAmountCalculator implements AmountCalculator {
        @Override public long getAmount(Transaction transaction) {
            return 0;
        }
    }
}
