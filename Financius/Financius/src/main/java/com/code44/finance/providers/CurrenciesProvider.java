package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;

import java.util.List;

public class CurrenciesProvider extends AbstractItemsProvider
{
    public static Uri uriCurrencies(Context context)
    {
        return getContentUri(context);
    }

    public static Uri uriCurrency(Context context, long currencyId)
    {
        return ContentUris.withAppendedId(uriCurrencies(context), currencyId);
    }

    protected static Uri getContentUri(Context context)
    {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(context, CurrenciesProvider.class) + "/" + Tables.Currencies.TABLE_NAME);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Currencies.TABLE_NAME;
    }

    @Override
    protected Object onBeforeInsert(Uri uri, ContentValues values)
    {
        // Find the current count of currencies
        int count = 0;
        Cursor c = null;
        try
        {
            c = queryItems(uri, new String[]{Tables.Currencies.T_ID}, null, null, null);
            if (c != null && c.moveToFirst())
                count = c.getCount();
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Mark other currencies as not default if necessary
        if (values.containsKey(Tables.Currencies.IS_DEFAULT) && values.getAsBoolean(Tables.Currencies.IS_DEFAULT) && count > 0)
        {
            // Only when creating new default currency and other default currency is already created

            // Mark other currencies as not default
            final ContentValues tempValues = new ContentValues();
            tempValues.put(Tables.Currencies.IS_DEFAULT, false);
            db.update(Tables.Currencies.TABLE_NAME, tempValues, null, null);
        }

        // If it's the first currency, then it must be default
        if (count == 0)
            values.put(Tables.Currencies.IS_DEFAULT, true);

        return super.onBeforeInsert(uri, values);
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        super.onAfterInsert(uri, values, newId, objectFromBefore);
        AmountUtils.onCurrencyUpdated(getContext(), newId);
        CurrenciesHelper.getDefault(getContext()).update();
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        // Find if currency we are editing is main or not
        boolean isMain = false;
        Cursor c = null;
        try
        {
            c = queryItems(uri, new String[]{Tables.Currencies.IS_DEFAULT}, selection, selectionArgs, null);
            if (c != null && c.moveToFirst())
                isMain = c.getInt(0) != 0;
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        if (!values.getAsBoolean(Tables.Currencies.IS_DEFAULT))
        {
            // Do not allow to make currency not default
            values.remove(Tables.Currencies.IS_DEFAULT);
        }
        else if (!isMain)
        {
            // Setting new currency as default. Mark other currencies as not default and reset exchange rates
            final ContentValues tempValues = new ContentValues();
            tempValues.put(Tables.Currencies.IS_DEFAULT, false);
            tempValues.put(Tables.Currencies.EXCHANGE_RATE, 1.0);
            db.update(Tables.Currencies.TABLE_NAME, tempValues, null, null);
        }

        return super.onBeforeUpdate(uri, values, selection, selectionArgs);
    }

    @Override
    protected void onAfterUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        super.onAfterUpdate(uri, values, selection, selectionArgs, updatedCount, objectFromBefore);

        // Update currency formats
        Cursor c = null;
        try
        {
            c = queryItems(uri, new String[]{Tables.Currencies.T_ID}, selection, selectionArgs, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    AmountUtils.onCurrencyUpdated(getContext(), c.getLong(0));
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        CurrenciesHelper.getDefault(getContext()).update();
    }

    @Override
    protected Object onBeforeDelete(Uri uri, String selection, String[] selectionArgs)
    {
        return getItemIDs(selection, selectionArgs);
    }

    @Override
    protected void onAfterDelete(Uri uri, String selection, String[] selectionArgs, int updatedCount, Object objectFromBefore)
    {
        // Make one random currency as default, if default currency was deleted. Also reset exchange rates
        Cursor c = null;
        try
        {
            c = db.query(Tables.Currencies.TABLE_NAME, new String[]{Tables.Currencies.T_ID, Tables.Currencies.IS_DEFAULT}, Tables.Currencies.DELETE_STATE + "=?", new String[] {String.valueOf(Tables.DeleteState.NONE)}, null, null, Tables.Currencies.IS_DEFAULT + " desc");
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
        final List<Long> itemIDs = (List<Long>) objectFromBefore;
        if (itemIDs != null && itemIDs.size() > 0)
        {
            final InClause inClause = InClause.getInClause(itemIDs, Tables.Accounts.CURRENCY_ID);
            getContext().getContentResolver().delete(AccountsProvider.uriAccounts(getContext()), Tables.Accounts.ORIGIN + "<>" + Tables.Accounts.Origin.SYSTEM + " and " + inClause.getSelection(), inClause.getSelectionArgs());
        }

        CurrenciesHelper.getDefault(getContext()).update();
    }
}