package com.code44.finance.money;

import android.database.Cursor;

import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.interval.BaseInterval;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmountGrouper {
    private final Period period;
    private final Interval wholeInterval;
    private final Interval firstInterval;

    public AmountGrouper(Interval interval, BaseInterval.Type intervalType) {
        period = getPeriod(intervalType);
        wholeInterval = interval;
        firstInterval = new Interval(interval.getStart(), period);
    }

    public Map<AmountCalculator, List<Long>> getGroups(Cursor cursor, AmountCalculator... amountCalculators) {
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
                onTransactionInInterval(transaction, groups.keySet(), intervalAmounts);
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

    private void onTransactionInInterval(Transaction transaction, Set<AmountCalculator> amountCalculators, Map<AmountCalculator, Long> intervalAmounts) {
        for (AmountCalculator amountCalculator : amountCalculators) {
            Long amount = intervalAmounts.get(amountCalculator);
            if (amount == null) {
                amount = 0L;
            }

            amount += amountCalculator.getAmount(transaction);
            intervalAmounts.put(amountCalculator, amount);
        }
    }

    public static interface AmountCalculator {
        public long getAmount(Transaction transaction);
    }
}
