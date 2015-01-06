package com.code44.finance.ui.reports;

import android.database.Cursor;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Currency;
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

    public AmountGroups(BaseInterval baseInterval) {
        period = getPeriod(baseInterval);
        wholeInterval = baseInterval.getInterval();
        firstInterval = getFirstInterval(baseInterval.getInterval(), period);
    }

    public Map<TransactionValidator, List<Long>> getGroups(Cursor cursor, Currency mainCurrency, TransactionValidator... transactionValidators) {
        final Map<TransactionValidator, List<Long>> groups = new HashMap<>();
        for (TransactionValidator transactionValidator : transactionValidators) {
            groups.put(transactionValidator, new ArrayList<Long>());
        }

        final Map<TransactionValidator, Long> intervalAmounts = new HashMap<>();
        Interval interval = firstInterval;
        Transaction transaction = cursor != null && cursor.moveToFirst() ? Transaction.from(cursor) : null;
        while (isNotAfterLastInterval(interval)) {
            if (transaction == null || interval.isAfter(transaction.getDate())) {
                onIntervalDone(groups, intervalAmounts);
                interval = getNextInterval(interval, period);
                continue;
            }

            while (transaction != null && interval.contains(transaction.getDate())) {
                onTransactionInInterval(transaction, groups.keySet(), intervalAmounts, mainCurrency);
                transaction = cursor.moveToNext() ? Transaction.from(cursor) : null;
            }

            onIntervalDone(groups, intervalAmounts);
            interval = getNextInterval(interval, period);
        }

        return groups;
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

    private Interval getFirstInterval(Interval interval, Period period) {
        return new Interval(interval.getStart(), period);
    }

    private Interval getNextInterval(Interval interval, Period period) {
        return new Interval(interval.getEnd(), period);
    }

    private boolean isNotAfterLastInterval(Interval interval) {
        return interval.overlaps(wholeInterval);
    }

    private void onIntervalDone(Map<TransactionValidator, List<Long>> groups, Map<TransactionValidator, Long> intervalAmounts) {
        for (TransactionValidator transactionValidator : groups.keySet()) {
            Long amount = intervalAmounts.get(transactionValidator);
            if (amount == null) {
                amount = 0L;
            }
            groups.get(transactionValidator).add(amount);
        }

        intervalAmounts.clear();
    }

    private void onTransactionInInterval(Transaction transaction, Set<TransactionValidator> transactionValidators, Map<TransactionValidator, Long> intervalAmounts, Currency mainCurrency) {
        for (TransactionValidator transactionValidator : transactionValidators) {
            if (transactionValidator.isTransactionValid(transaction)) {
                Long amount = intervalAmounts.get(transactionValidator);
                if (amount == null) {
                    amount = 0L;
                }

                amount += getAmount(transaction, mainCurrency);
                intervalAmounts.put(transactionValidator, amount);
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

    public static interface TransactionValidator {
        public boolean isTransactionValid(Transaction transaction);
    }
}
