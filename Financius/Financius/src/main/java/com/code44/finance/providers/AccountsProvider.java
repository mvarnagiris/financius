package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AccountsUtils;

import java.util.List;

public class AccountsProvider extends AbstractItemsProvider
{
    public static Uri uriAccounts(Context context)
    {
        return getContentUri(context);
    }

    public static Uri uriAccount(Context context, long accountId)
    {
        return ContentUris.withAppendedId(uriAccounts(context), accountId);
    }

    protected static Uri getContentUri(Context context)
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(context, AccountsProvider.class) + "/" + Tables.Accounts.TABLE_NAME);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Accounts.TABLE_NAME;
    }

    @Override
    protected String getJoinedTables()
    {
        return " inner join " + Tables.Currencies.TABLE_NAME + " on " + Tables.Currencies.T_ID + "=" + Tables.Accounts.CURRENCY_ID;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        // Get balance
        final Double balance = values.getAsDouble(Tables.Accounts.BALANCE);
        values.remove(Tables.Accounts.BALANCE);

        return balance;
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        super.onAfterInsert(uri, values, newId, objectFromBefore);

        // Create transaction if necessary
        final Double balance = (Double) objectFromBefore;
        if (balance != 0)
            AccountsUtils.updateBalanceWithTransaction(getContext(), newId, values.getAsString(Tables.Accounts.SERVER_ID), balance);
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // Get balance
        final Double balance = values.getAsDouble(Tables.Accounts.BALANCE);
        values.remove(Tables.Accounts.BALANCE);

        return balance;
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        super.onAfterUpdate(uri, values, selection, selectionArgs, updatedCount, objectFromBefore);

        // Create transactions if necessary
        final Double balance = (Double) objectFromBefore;
        AccountsUtils.updateBalanceWithTransaction(getContext(), selection, selectionArgs, balance);
    }

    @Override
    protected Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs)
    {
        return getItemIDs(selection, selectionArgs);
    }

    @Override
    protected void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Delete Transactions
        final List<Long> itemIDs = (List<Long>) objectFromBefore;
        if (itemIDs != null && itemIDs.size() > 0)
        {
            InClause inClause = InClause.getInClause(itemIDs, Tables.Transactions.ACCOUNT_FROM_ID);
            getContext().getContentResolver().delete(TransactionsProvider.uriTransactions(getContext()), inClause.getSelection(), inClause.getSelectionArgs());

            inClause = InClause.getInClause(itemIDs, Tables.Transactions.ACCOUNT_TO_ID);
            getContext().getContentResolver().delete(TransactionsProvider.uriTransactions(getContext()), inClause.getSelection(), inClause.getSelectionArgs());
        }
    }
}