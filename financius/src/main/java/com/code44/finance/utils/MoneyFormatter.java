package com.code44.finance.utils;

import android.support.v4.util.LongSparseArray;

import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.db.model.Transaction;

public class MoneyFormatter {
    private static final LongSparseArray<CurrencyFormat> currencyFormats = new LongSparseArray<>();

    public static String format(Transaction transaction) {
        final Currency currency;
        if (transaction.getCategory().getType() == Category.Type.INCOME) {
            currency = transaction.getAccountTo().getCurrency();
        } else {
            currency = transaction.getAccountFrom().getCurrency();
        }
        return format(currency, transaction.getAmount());
    }

    public static String format(Currency currency, long amount) {
        final CurrencyFormat currencyFormat = getCurrencyFormat(currency);
        final double number = amount / 100.0;
        return currencyFormat.format(number);
    }

    private static CurrencyFormat getCurrencyFormat(Currency currency) {
        CurrencyFormat currencyFormat = currencyFormats.get(currency.getId());
        if (currencyFormat == null) {
            currencyFormat = new CurrencyFormat(currency);
            currencyFormats.put(currency.getId(), currencyFormat);
        }
        return currencyFormat;
    }
}
