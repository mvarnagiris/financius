package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AccountsUtils;

public class TransactionsProvider extends AbstractItemsProvider
{
    public static Uri uriTransactions(Context context)
    {
        return getContentUri(context);
    }

    public static Uri uriTransaction(Context context, long transactionId)
    {
        return ContentUris.withAppendedId(uriTransactions(context), transactionId);
    }

    protected static Uri getContentUri(Context context)
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(context, TransactionsProvider.class) + "/" + Tables.Transactions.TABLE_NAME);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Transactions.TABLE_NAME;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        return null;
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
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        AccountsUtils.updateBalance(getContext(), db, values.getAsLong(Tables.Transactions.ACCOUNT_FROM_ID), values.getAsLong(Tables.Transactions.ACCOUNT_TO_ID), values.getAsLong(Tables.Transactions.CATEGORY_ID));
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
    }
}