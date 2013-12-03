package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.code44.finance.FinanciusApp;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;

import java.util.List;

public class CurrenciesProvider extends AbstractItemsProvider
{
    public static Uri uriCurrencies()
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(FinanciusApp.getAppContext(), CurrenciesProvider.class) + "/" + Tables.Currencies.TABLE_NAME);
    }

    public static Uri uriCurrency(long currencyId)
    {
        return ContentUris.withAppendedId(uriCurrencies(), currencyId);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Currencies.TABLE_NAME;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        // Update IS_DEFAULT field
        //noinspection ConstantConditions
        final boolean isDefault = values.getAsBoolean(Tables.Currencies.IS_DEFAULT);

        // Find the current count of currencies
        int count = 0;
        Cursor c = null;
        try
        {
            c = queryItems(new String[]{Tables.Currencies.T_ID}, null, null, null);
            if (c != null && c.moveToFirst())
                count = c.getCount();
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Mark other currencies as not default if necessary
        if (isDefault && count > 0)
        {
            // Only when creating new default currency and other default currency is already created

            // Mark other currencies as not default
            clearDefaultCurrency();
        }

        // If it's the first currency, then it must be default
        if (count == 0)
            values.put(Tables.Currencies.IS_DEFAULT, true);

        return null;
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        AmountUtils.onCurrencyUpdated(newId);
        CurrenciesHelper.getDefault().update();

        // Notify
        notifyURIs(uriCurrencies());
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // Check if we are trying to change IS_DEFAULT value
        if (values.containsKey(Tables.Currencies.IS_DEFAULT))
        {
            //noinspection ConstantConditions
            boolean isDefault = values.getAsBoolean(Tables.Currencies.IS_DEFAULT);
            if (!isDefault)
            {
                // Changing to NOT default. Do not allow to make currency not default.
                throw new IllegalArgumentException(Tables.Currencies.IS_DEFAULT + " cannot be set to false for currency that has value true. To change default currency you must update another currency to have " + Tables.Currencies.IS_DEFAULT + "=true.");
            }
            else
            {
                // Changing to default
                boolean isEditingMain = false;

                // Find if currency we are editing is main or not and how many currencies we are trying to change.
                Cursor c = null;
                try
                {
                    c = queryItems(new String[]{Tables.Currencies.IS_DEFAULT}, selection, selectionArgs, null);
                    if (c != null && c.moveToFirst())
                    {
                        if (c.getCount() > 1)
                            throw new IllegalArgumentException("Cannot make more than 1 currency as default.");

                        isEditingMain = c.getInt(0) != 0;
                    }
                }
                finally
                {
                    if (c != null && !c.isClosed())
                        c.close();
                }

                if (!isEditingMain)
                {
                    // Setting new currency as default. Mark other currencies as not default and reset exchange rates
                    clearDefaultCurrency();
                }
            }
        }

        return null;
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Update currency formats
        Cursor c = null;
        try
        {
            c = queryItems(new String[]{Tables.Currencies.T_ID}, selection, selectionArgs, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    AmountUtils.onCurrencyUpdated(c.getLong(0));
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
        CurrenciesHelper.getDefault().update();

        // Notify
        notifyURIs(CurrenciesProvider.uriCurrencies(), AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions(), BudgetsProvider.uriBudgets());
    }

    @Override
    protected Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs)
    {
        return getItemIDs(selection, selectionArgs);
    }

    @Override
    protected void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Check if default currency was deleted.
        Cursor c = null;
        try
        {
            c = db.query(Tables.Currencies.TABLE_NAME, new String[]{Tables.Currencies.T_ID, Tables.Currencies.IS_DEFAULT}, Tables.Currencies.DELETE_STATE + "=?", new String[]{String.valueOf(Tables.DeleteState.NONE)}, null, null, Tables.Currencies.IS_DEFAULT + " desc");
            if (c != null && c.moveToFirst())
            {
                // First currency should be default by sort order. If it's not, then there is no default currency. Need to make new currency a default currency.
                if (c.getInt(c.getColumnIndex(Tables.Currencies.IS_DEFAULT)) == 0)
                {
                    // New default currency
                    ContentValues values = new ContentValues();
                    values.put(Tables.Currencies.IS_DEFAULT, true);
                    db.update(Tables.Currencies.TABLE_NAME, values, Tables.Currencies.ID + "=?", new String[]{String.valueOf(c.getLong(c.getColumnIndex(Tables.Currencies.ID)))});

                    // Reset exchange rates
                    values.clear();
                    values.put(Tables.Currencies.EXCHANGE_RATE, 1.0);
                    db.update(Tables.Currencies.TABLE_NAME, values, null, null);
                }
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Delete accounts
        //noinspection unchecked
        final List<Long> itemIDs = (List<Long>) objectFromBefore;
        if (itemIDs != null && itemIDs.size() > 0)
        {
            final InClause inClause = InClause.getInClause(itemIDs, Tables.Accounts.CURRENCY_ID);
            //noinspection ConstantConditions
            getContext().getContentResolver().delete(AccountsProvider.uriAccounts(), Tables.Accounts.ORIGIN + "<>" + Tables.Accounts.Origin.SYSTEM + " and " + inClause.getSelection(), inClause.getSelectionArgs());
        }

        CurrenciesHelper.getDefault().update();

        // Notify
        notifyURIs(CurrenciesProvider.uriCurrencies(), AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions(), BudgetsProvider.uriBudgets());
    }

    @Override
    protected Object onBeforeBulkInsert(Uri uri, ContentValues[] valuesArray)
    {
        final BulkInsertStatus status = new BulkInsertStatus();

        // Find current default currency
        Cursor c = null;
        try
        {
            c = queryItems(new String[]{Tables.Currencies.T_ID}, Tables.Currencies.IS_DEFAULT + "=?", new String[]{"1"}, null);
            if (c != null && c.moveToFirst())
            {
                status.setOldDefaultCurrencyId(c.getLong(0));
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return status;
    }

    @Override
    protected void onAfterBulkInsert(Uri uri, ContentValues[] valuesArray, Object objectFromBefore)
    {
        final BulkInsertStatus status = (BulkInsertStatus) objectFromBefore;
        long newDefaultCurrencyId = 0;
        if (status.getOldDefaultCurrencyId() > 0)
        {
            // Find out if default currency is changing.
            Cursor c = null;
            try
            {
                c = queryItems(new String[]{Tables.Currencies.T_ID}, Tables.Currencies.IS_DEFAULT + "=?", new String[]{"1"}, null);
                if (c != null && c.moveToFirst())
                {
                    do
                    {
                        if (c.getLong(0) != status.getOldDefaultCurrencyId())
                        {
                            newDefaultCurrencyId = c.getLong(0);
                            break;
                        }
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

        // Ensure that there is only one default currency
        if (newDefaultCurrencyId > 0)
        {
            // Clear default currencies and exchange rates.
            clearDefaultCurrency();

            // Set new default currency.
            final ContentValues values = new ContentValues();
            values.put(Tables.Currencies.IS_DEFAULT, true);
            db.update(Tables.Currencies.TABLE_NAME, values, Tables.Currencies.T_ID + "=?", new String[]{String.valueOf(newDefaultCurrencyId)});
        }

        // Notify
        notifyURIs(CurrenciesProvider.uriCurrencies(), AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions(), BudgetsProvider.uriBudgets());
    }

    @Override
    protected void checkValues(ContentValues values, int operation)
    {
        final boolean required = operation == OPERATION_INSERT || operation == OPERATION_BULK_INSERT;
        checkString(values, Tables.Currencies.CODE, required);
        checkInt(values, Tables.Currencies.DECIMALS, required, 0, 2);
        checkString(values, Tables.Currencies.DECIMAL_SEPARATOR, required, ",", ".", "");
        checkString(values, Tables.Currencies.GROUP_SEPARATOR, required, ",", ".", " ", "");
        checkString(values, Tables.Currencies.SYMBOL_FORMAT, required, Tables.Currencies.SymbolFormat.LEFT_CLOSE, Tables.Currencies.SymbolFormat.LEFT_FAR, Tables.Currencies.SymbolFormat.RIGHT_CLOSE, Tables.Currencies.SymbolFormat.RIGHT_FAR);
        checkDouble(values, Tables.Currencies.EXCHANGE_RATE, required, 0, Double.MAX_VALUE);

        if (required && !values.containsKey(Tables.Currencies.IS_DEFAULT))
            values.put(Tables.Currencies.IS_DEFAULT, false);
    }

    private void clearDefaultCurrency()
    {
        final ContentValues values = new ContentValues();
        values.put(Tables.Currencies.IS_DEFAULT, false);
        values.put(Tables.Currencies.EXCHANGE_RATE, 1);
        db.update(Tables.Currencies.TABLE_NAME, values, null, null);
    }

    private static class BulkInsertStatus
    {
        private long oldDefaultCurrencyId = -1;

        public long getOldDefaultCurrencyId()
        {
            return oldDefaultCurrencyId;
        }

        public void setOldDefaultCurrencyId(long oldDefaultCurrencyId)
        {
            this.oldDefaultCurrencyId = oldDefaultCurrencyId;
        }
    }
}