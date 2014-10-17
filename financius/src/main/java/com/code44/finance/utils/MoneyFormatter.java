package com.code44.finance.utils;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;

import java.util.HashMap;
import java.util.Map;

public class MoneyFormatter {
    private static final Map<String, CurrencyFormat> currencyFormats = new HashMap<>();

    public static String format(Transaction transaction, Currency mainCurrency) {
        final Account account;
        if (transaction.getTransactionType() == TransactionType.INCOME) {
            account = transaction.getAccountTo();
        } else {
            account = transaction.getAccountFrom();
        }

        Currency currency = null;
        if (account != null) {
            currency = account.getCurrency();
        }

        if (currency == null) {
            currency = mainCurrency;
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
        final String currencyId = currency.getId();
        if (StringUtils.isEmpty(currencyId) || !useCache) {
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
