package com.code44.finance.utils;

import android.content.ContentValues;
import com.code44.finance.db.Tables;

public class TransactionsUtils
{
    public static ContentValues getValues(long accountFromId, long accountToId, long categoryId, long date, double amount, double exchangeRate, String note, int state, boolean showInTotals)
    {
        ContentValues values = new ContentValues();

        values.put(Tables.Transactions.ACCOUNT_FROM_ID, accountFromId);
        values.put(Tables.Transactions.ACCOUNT_TO_ID, accountToId);
        values.put(Tables.Transactions.CATEGORY_ID, categoryId);
        values.put(Tables.Transactions.DATE, date);
        values.put(Tables.Transactions.AMOUNT, amount);
        values.put(Tables.Transactions.EXCHANGE_RATE, exchangeRate);
        values.put(Tables.Transactions.NOTE, note);
        values.put(Tables.Transactions.STATE, state);
        values.put(Tables.Transactions.SHOW_IN_TOTALS, showInTotals);

        return values;
    }
}