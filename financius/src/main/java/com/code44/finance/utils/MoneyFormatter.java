package com.code44.finance.utils;

import android.support.v4.util.LongSparseArray;

import com.code44.finance.common.model.CategoryType;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;

public class MoneyFormatter {
    private static final LongSparseArray<CurrencyFormat> currencyFormats = new LongSparseArray<>();

    public static String format(Transaction transaction) {
        final Currency currency;
        if (transaction.getCategory().getCategoryType() == CategoryType.INCOME) {
            currency = transaction.getAccountTo().getCurrency();
        } else {
            currency = transaction.getAccountFrom().getCurrency();
        }
        return format(currency, transaction.getAmount());
    }

    public static String format(Currency currency, long amount) {
        return format(currency, amount, true);
    }

    public static String format(Currency currency, long amount, boolean useCache) {
        final CurrencyFormat currencyFormat = getCurrencyFormat(currency, useCache);
        final double number = amount / 100.0;
        return currencyFormat.format(number);
    }

    public static void invalidateCache() {
        currencyFormats.clear();
    }

    private static CurrencyFormat getCurrencyFormat(Currency currency, boolean useCache) {
        final long currencyId = currency.getId();
        if (currencyId == 0 || !useCache) {
            return new CurrencyFormat(currency);
        } else {
            CurrencyFormat currencyFormat = currencyFormats.get(currency.getId());
            if (currencyFormat == null) {
                currencyFormat = new CurrencyFormat(currency);
                currencyFormats.put(currency.getId(), currencyFormat);
            }
            return currencyFormat;
        }
    }
}
