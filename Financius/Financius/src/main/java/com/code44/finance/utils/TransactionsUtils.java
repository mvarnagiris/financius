package com.code44.finance.utils;

import android.content.ContentValues;
import com.code44.finance.db.Tables;
import com.code44.finance.db.Tables.DeleteState;
import com.code44.finance.db.Tables.SyncState;

public class TransactionsUtils
{
    /**
     * Prepare values for update or create.
     *
     * @param values
     * @param accountFromId
     * @param accountToId
     * @param categoryId
     * @param date
     * @param amount
     * @param note
     */
    public static void prepareValues(ContentValues values, long accountFromId, long accountToId, long categoryId, long date, double amount, double exchangeRate, String note, int state, boolean showInTotals)
    {
        values.put(Tables.Transactions.ACCOUNT_FROM_ID, accountFromId);
        values.put(Tables.Transactions.ACCOUNT_TO_ID, accountToId);
        values.put(Tables.Transactions.CATEGORY_ID, categoryId);
        values.put(Tables.Transactions.DATE, date);
        values.put(Tables.Transactions.AMOUNT, amount);
        values.put(Tables.Transactions.EXCHANGE_RATE, exchangeRate);
        values.put(Tables.Transactions.NOTE, note);
        values.put(Tables.Transactions.STATE, state);
        values.put(Tables.Transactions.SHOW_IN_TOTALS, showInTotals);
        values.put(Tables.Transactions.TIMESTAMP, System.currentTimeMillis());
        values.put(Tables.Transactions.DELETE_STATE, DeleteState.NONE);
        values.put(Tables.Transactions.SYNC_STATE, SyncState.LOCAL_CHANGES);
    }
}