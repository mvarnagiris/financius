package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import com.code44.finance.App;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AccountsUtils;

public class TransactionsProvider extends BaseItemsProvider
{
    public static Uri uriTransactions()
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(App.getAppContext(), TransactionsProvider.class) + "/" + Tables.Transactions.TABLE_NAME);
    }

    public static Uri uriTransaction(long transactionId)
    {
        return ContentUris.withAppendedId(uriTransactions(), transactionId);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Transactions.TABLE_NAME;
    }

    @Override
    protected String getJoinedTables()
    {
        return " inner join " + Tables.Accounts.TABLE_NAME + " as " + Tables.Accounts.AccountFrom.TABLE_NAME + " on " + Tables.Accounts.AccountFrom.T_ID + "=" + Tables.Transactions.ACCOUNT_FROM_ID
                + " inner join " + Tables.Accounts.TABLE_NAME + " as " + Tables.Accounts.AccountTo.TABLE_NAME + " on " + Tables.Accounts.AccountTo.T_ID + "=" + Tables.Transactions.ACCOUNT_TO_ID
                + " inner join " + Tables.Categories.TABLE_NAME + " as " + Tables.Categories.CategoriesChild.TABLE_NAME + " on " + Tables.Categories.CategoriesChild.T_ID + "=" + Tables.Transactions.CATEGORY_ID
                + " left join " + Tables.Categories.TABLE_NAME + " as " + Tables.Categories.CategoriesParent.TABLE_NAME + " on " + Tables.Categories.CategoriesParent.T_ID + "=" + Tables.Categories.CategoriesChild.T_PARENT_ID
                + " left join " + Tables.Currencies.TABLE_NAME + " as " + Tables.Currencies.CurrencyFrom.TABLE_NAME + " on " + Tables.Currencies.CurrencyFrom.T_ID + "=" + Tables.Accounts.AccountFrom.T_CURRENCY_ID
                + " left join " + Tables.Currencies.TABLE_NAME + " as " + Tables.Currencies.CurrencyTo.TABLE_NAME + " on " + Tables.Currencies.CurrencyTo.T_ID + "=" + Tables.Accounts.AccountTo.T_CURRENCY_ID;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        return null;
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        //noinspection ConstantConditions
        AccountsUtils.updateBalance(getContext(), db, values.getAsLong(Tables.Transactions.ACCOUNT_FROM_ID), values.getAsLong(Tables.Transactions.ACCOUNT_TO_ID), values.getAsLong(Tables.Transactions.CATEGORY_ID));

        notifyURIs(AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return null;
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        AccountsUtils.updateBalance(getContext(), db);

        notifyURIs(AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs)
    {
        return null;
    }

    @Override
    protected void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        AccountsUtils.updateBalance(getContext(), db);

        notifyURIs(AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected Object onBeforeBulkInsert(Uri uri, ContentValues[] valuesArray)
    {
        return null;
    }

    @Override
    protected void onAfterBulkInsert(Uri uri, ContentValues[] valuesArray, Object objectFromBefore)
    {
        notifyURIs(AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected void checkValues(ContentValues values, int operation)
    {
        final boolean required = operation == OPERATION_INSERT || operation == OPERATION_BULK_INSERT;
        checkId(values, Tables.Transactions.ACCOUNT_FROM_ID, required);
        checkId(values, Tables.Transactions.ACCOUNT_TO_ID, required);
        checkId(values, Tables.Transactions.CATEGORY_ID, required);
        checkLong(values, Tables.Transactions.DATE, required);
        checkDouble(values, Tables.Transactions.AMOUNT, required, 0.01, Double.MAX_VALUE);
        checkDouble(values, Tables.Transactions.EXCHANGE_RATE, required, 0, Double.MAX_VALUE);
        checkInt(values, Tables.Transactions.STATE, required, 0, 1);
        if (required && !values.containsKey(Tables.Transactions.SHOW_IN_TOTALS))
            values.put(Tables.Transactions.SHOW_IN_TOTALS, true);
    }
}