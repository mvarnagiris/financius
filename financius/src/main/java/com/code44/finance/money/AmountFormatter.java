package com.code44.finance.money;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.IOUtils;

import java.util.HashMap;
import java.util.Map;

public class AmountFormatter {
    private final Map<String, CurrencyFormat> currencyFormats = new HashMap<>();
    private final CurrenciesManager currenciesManager;

    public AmountFormatter(CurrenciesManager currenciesManager) {
        this.currenciesManager = currenciesManager;
    }

    public void updateFormats(SQLiteDatabase database) {
        currencyFormats.clear();
        final Cursor cursor = Tables.CurrencyFormats.getQuery().from(database, Tables.CurrencyFormats.TABLE_NAME).execute();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final CurrencyFormat currencyFormat = CurrencyFormat.from(cursor);
                currencyFormats.put(currencyFormat.getCode(), currencyFormat);
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);
    }

    public String format(long amount) {
        return format(currenciesManager.getMainCurrencyCode(), amount);
    }

    public String format(String currencyCode, long amount) {
        return getCurrencyFormat(currencyCode).format(amount);
    }

    public String format(Transaction transaction) {
        final Account account;
        if (transaction.getTransactionType() == TransactionType.Income) {
            account = transaction.getAccountTo();
        } else {
            account = transaction.getAccountFrom();
        }

        if (account != null) {
            return format(account.getCurrencyCode(), transaction.getAmount());
        }

        return format(transaction.getAmount());
    }

    private CurrencyFormat getCurrencyFormat(String code) {
        final CurrencyFormat currencyFormat = currencyFormats.get(code);
        if (currencyFormat != null) {
            return currencyFormat;
        }

        return new CurrencyFormat();
    }
}
