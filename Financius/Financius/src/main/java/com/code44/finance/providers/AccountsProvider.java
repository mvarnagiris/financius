package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import com.code44.finance.App;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AccountsUtils;

import java.util.List;

public class AccountsProvider extends BaseItemsProvider
{
    public static Uri uriAccounts()
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(App.getAppContext(), AccountsProvider.class) + "/" + Tables.Accounts.TABLE_NAME);
    }

    public static Uri uriAccount(long accountId)
    {
        return ContentUris.withAppendedId(uriAccounts(), accountId);
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
        if (values.containsKey(Tables.Accounts.BALANCE))
        {
            final Double balance = values.getAsDouble(Tables.Accounts.BALANCE);
            values.remove(Tables.Accounts.BALANCE);
            return balance;
        }

        return null;
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        // Create transaction if necessary
        final Double balance = (Double) objectFromBefore;
        if (balance != null && balance != 0)
            AccountsUtils.updateBalanceWithTransaction(getContext(), newId, values.getAsString(Tables.Accounts.SERVER_ID), balance);

        notifyURIs(uriAccounts());
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // Get balance
        if (values.containsKey(Tables.Accounts.BALANCE))
        {
            final Double balance = values.getAsDouble(Tables.Accounts.BALANCE);
            values.remove(Tables.Accounts.BALANCE);
            return balance;
        }

        return null;
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Create transactions if necessary
        if (objectFromBefore != null)
        {
            final Double balance = (Double) objectFromBefore;
            AccountsUtils.updateBalanceWithTransaction(getContext(), selection, selectionArgs, balance);
        }

        notifyURIs(CurrenciesProvider.uriCurrencies(), AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions());
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
        //noinspection unchecked
        final List<Long> itemIDs = (List<Long>) objectFromBefore;
        if (itemIDs != null && itemIDs.size() > 0)
        {
            InClause inClause = InClause.getInClause(itemIDs, Tables.Transactions.ACCOUNT_FROM_ID);
            //noinspection ConstantConditions
            getContext().getContentResolver().delete(TransactionsProvider.uriTransactions(), inClause.getSelection(), inClause.getSelectionArgs());

            inClause = InClause.getInClause(itemIDs, Tables.Transactions.ACCOUNT_TO_ID);
            getContext().getContentResolver().delete(TransactionsProvider.uriTransactions(), inClause.getSelection(), inClause.getSelectionArgs());
        }

        notifyURIs(CurrenciesProvider.uriCurrencies(), AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions());
    }

    @Override
    protected Object onBeforeBulkInsert(Uri uri, ContentValues[] valuesArray)
    {
        return null;
    }

    @Override
    protected void onAfterBulkInsert(Uri uri, ContentValues[] valuesArray, Object objectFromBefore)
    {
    }

    @Override
    protected void checkValues(ContentValues values, int operation)
    {
        final boolean required = operation == OPERATION_INSERT || operation == OPERATION_BULK_INSERT;
        checkId(values, Tables.Accounts.CURRENCY_ID, required);
        checkString(values, Tables.Accounts.TITLE, required, false);
        if (required && !values.containsKey(Tables.Accounts.SHOW_IN_TOTALS))
            values.put(Tables.Accounts.SHOW_IN_TOTALS, true);
        if (required && !values.containsKey(Tables.Accounts.SHOW_IN_SELECTION))
            values.put(Tables.Accounts.SHOW_IN_SELECTION, true);
        if (required && !values.containsKey(Tables.Accounts.ORIGIN))
            values.put(Tables.Accounts.ORIGIN, Tables.Accounts.Origin.USER);
    }
}