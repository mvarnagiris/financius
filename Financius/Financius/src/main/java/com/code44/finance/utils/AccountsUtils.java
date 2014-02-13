package com.code44.finance.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.code44.finance.R;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.providers.TransactionsProvider;

import java.util.UUID;

public class AccountsUtils
{
    public static ContentValues getValues(long currencyId, String title, String note, double balance, boolean showInTotals, boolean showInSelection)
    {
        ContentValues values = new ContentValues();

        values.put(Tables.Accounts.CURRENCY_ID, currencyId);
        values.put(Tables.Accounts.TITLE, title);
        values.put(Tables.Accounts.NOTE, note);
        values.put(Tables.Accounts.BALANCE, balance);
        values.put(Tables.Accounts.SHOW_IN_TOTALS, showInTotals);
        values.put(Tables.Accounts.SHOW_IN_SELECTION, showInSelection);

        return values;
    }

    /**
     * Updates balance for given account. Queries transactions and makes a sum for balance. Only confirmed transactions are used.
     *
     * @param db        Database.
     * @param accountId Account Id.
     */
    public static void updateBalance(SQLiteDatabase db, long accountId)
    {
        final String accountIdStr = String.valueOf(accountId);
        Cursor c = null;
        try
        {
            c = db.rawQuery("select sum("
                    + "case when " + Tables.Transactions.ACCOUNT_FROM_ID + "=? then -" + Tables.Transactions.AMOUNT
                    + " when " + Tables.Categories.TYPE + "=? then " + Tables.Transactions.AMOUNT + "*" + Tables.Transactions.EXCHANGE_RATE
                    + " else " + Tables.Transactions.AMOUNT + " end)"
                    + " from " + Tables.Transactions.TABLE_NAME
                    + " inner join " + Tables.Categories.TABLE_NAME + " on " + Tables.Categories.T_ID + "=" + Tables.Transactions.CATEGORY_ID
                    + " where " + Tables.Transactions.DELETE_STATE + "=?"
                    + " and " + Tables.Transactions.STATE + "=?"
                    + " and (" + Tables.Transactions.ACCOUNT_FROM_ID + "=? or " + Tables.Transactions.ACCOUNT_TO_ID + "=?)",
                    new String[]{accountIdStr, String.valueOf(Tables.Categories.Type.TRANSFER), String.valueOf(Tables.DeleteState.NONE), String.valueOf(Tables.Transactions.State.CONFIRMED), accountIdStr, accountIdStr});

            if (c != null && c.moveToFirst())
            {
                final double balance = c.getDouble(0);

                final ContentValues values = new ContentValues();
                values.put(Tables.Accounts.BALANCE, balance);
                db.update(Tables.Accounts.TABLE_NAME, values, Tables.Accounts.ID + "=?", new String[]{accountIdStr});
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    /**
     * Updates balance for all accounts by querying transactions and making sums for balance.
     *
     * @param context Context.
     * @param db      Database.
     */
    public static void updateBalance(Context context, SQLiteDatabase db)
    {
        Cursor c = null;
        try
        {
            c = context.getContentResolver().query(AccountsProvider.uriAccounts(), new String[]{Tables.Accounts.T_ID}, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    updateBalance(db, c.getLong(0));
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    /**
     * Updates balance for appropriate accounts.
     *
     * @param context       Context.
     * @param db            Database.
     * @param accountFromId Account from Id.
     * @param accountToId   Account to Id.
     * @param categoryId    Category Id. This will be used to determine category type.
     */
    public static void updateBalance(Context context, SQLiteDatabase db, long accountFromId, long accountToId, long categoryId)
    {
        // Get category type
        int categoryType = -1;
        Cursor c = null;
        try
        {
            c = context.getContentResolver().query(CategoriesProvider.uriCategory(categoryId), new String[]{Tables.Categories.TYPE}, null, null,
                    null);
            if (c != null && c.moveToFirst())
                categoryType = c.getInt(0);
            else
                throw new SQLiteException("Category for transaction not found.");
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Update accounts
        if (categoryType == Tables.Categories.Type.TRANSFER)
        {
            updateBalance(db, accountFromId);
            updateBalance(db, accountToId);
        }
        else if (categoryType == Tables.Categories.Type.INCOME)
            updateBalance(db, accountToId);
        else if (categoryType == Tables.Categories.Type.EXPENSE)
            updateBalance(db, accountFromId);
        else
            throw new SQLiteException("Category has bad type.This should not happen.");
    }

    /**
     * Updates given account balance by creating transaction. Transaction is created using {@link TransactionsProvider} and it takes care of actually updating
     * {@link com.code44.finance.db.Tables.Accounts#BALANCE} value.
     *
     * @param context   Context.
     * @param accountId Account Id.
     * @param balance   New balance.
     */
    public static void updateBalanceWithTransaction(Context context, long accountId, String accountServerId, double balance)
    {
        // Find difference between current balance and new balance
        double currentBalance = 0;
        accountServerId = accountServerId == null ? "0" : accountServerId;
        Cursor c = null;
        try
        {
            c = context.getContentResolver().query(AccountsProvider.uriAccounts(), new String[]{Tables.Accounts.BALANCE}, Tables.Accounts.T_ID + "=? or " + Tables.Accounts.SERVER_ID + "=?", new String[]{String.valueOf(accountId), accountServerId}, null);
            if (c != null && c.moveToFirst())
                currentBalance = c.getDouble(0);
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
        double delta = balance - currentBalance;

        // Create new transaction if necessary
        if (Math.abs(delta) >= 0.01)
        {
            final long accountFromId;
            final long accountToId;
            final long categoryId;

            if (delta > 0)
            {
                accountFromId = Tables.Accounts.IDs.INCOME_ID;
                accountToId = accountId;
                categoryId = Tables.Categories.IDs.INCOME_ID;
            }
            else
            {
                accountFromId = accountId;
                accountToId = Tables.Accounts.IDs.EXPENSE_ID;
                categoryId = Tables.Categories.IDs.EXPENSE_ID;
            }

            final ContentValues values = TransactionsUtils.getValues(accountFromId, accountToId, categoryId, System.currentTimeMillis(), Math.abs(delta), 1.0, context.getString(R.string.account_balance_update), Tables.Transactions.State.CONFIRMED, false);
            values.put(Tables.Transactions.SERVER_ID, UUID.randomUUID().toString());
            context.getContentResolver().insert(TransactionsProvider.uriTransactions(), values);
        }
    }

    /**
     * Updates accounts for selection balance by creating transaction(s). Transaction(s) is(are) created using {@link TransactionsProvider} and it takes care of
     * actually updating {@link com.code44.finance.db.Tables.Accounts#BALANCE} value.
     *
     * @param context       Context.
     * @param selection     Selection.
     * @param selectionArgs Selection arguments.
     * @param newBalance    New balance for accounts in selection.
     */
    public static void updateBalanceWithTransaction(Context context, String selection, String[] selectionArgs, double newBalance)
    {
        Cursor c = null;
        try
        {
            //noinspection ConstantConditions
            c = DBHelper.get(context).getReadableDatabase().query(Tables.Accounts.TABLE_NAME, new String[]{Tables.Accounts.T_ID, Tables.Accounts.SERVER_ID}, selection, selectionArgs, null, null, null);
            if (c != null && c.moveToFirst())
            {
                long accountId;
                String serverId;
                do
                {
                    accountId = c.getLong(0);
                    serverId = c.getString(1);
                    updateBalanceWithTransaction(context, accountId, serverId, newBalance);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
    }
}