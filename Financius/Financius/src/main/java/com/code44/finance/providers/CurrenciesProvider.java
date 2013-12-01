package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
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
        checkValues(values, true);

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
            final ContentValues tempValues = new ContentValues();
            tempValues.put(Tables.Currencies.IS_DEFAULT, false);
            db.update(Tables.Currencies.TABLE_NAME, tempValues, null, null);
        }

        // If it's the first currency, then it must be default
        if (count == 0)
            values.put(Tables.Currencies.IS_DEFAULT, true);

        return null;
    }

    @Override
    protected void onAfterInsert(Uri uri, ContentValues values, long newId, Object objectFromBefore)
    {
        AmountUtils.onCurrencyUpdated(getContext(), newId);
        CurrenciesHelper.getDefault(getContext()).update();

        // Notify
        notifyURIs(new Uri[]{uriCurrencies(getContext())});
    }

    @Override
    protected Object onBeforeUpdate(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        checkValues(values, false);

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
                    final ContentValues tempValues = new ContentValues();
                    tempValues.put(Tables.Currencies.IS_DEFAULT, false);
                    tempValues.put(Tables.Currencies.EXCHANGE_RATE, 1.0);
                    db.update(Tables.Currencies.TABLE_NAME, tempValues, null, null);
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

        // Notify
        notifyURIs(new Uri[]{
                CurrenciesProvider.uriCurrencies(getContext()),
                AccountsProvider.uriAccounts(getContext()),
                TransactionsProvider.uriTransactions(getContext()),
                BudgetsProvider.uriBudgets(getContext())});
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
            getContext().getContentResolver().delete(AccountsProvider.uriAccounts(getContext()), Tables.Accounts.ORIGIN + "<>" + Tables.Accounts.Origin.SYSTEM + " and " + inClause.getSelection(), inClause.getSelectionArgs());
        }

        CurrenciesHelper.getDefault(getContext()).update();
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

    private void checkValues(ContentValues values, boolean isInsert)
    {
        if (isInsert || values.containsKey(Tables.Currencies.CODE))
            checkString(values, Tables.Currencies.CODE);

        if (isInsert || values.containsKey(Tables.Currencies.DECIMALS))
        {
            final Integer decimals = values.getAsInteger(Tables.Currencies.DECIMALS);
            if (decimals == null || decimals < 0 || decimals > 2)
                throw new IllegalArgumentException(Tables.Currencies.DECIMALS + " must be 0 <= decimals <= 2");
        }

        if (isInsert || values.containsKey(Tables.Currencies.DECIMAL_SEPARATOR))
            checkString(values, Tables.Currencies.DECIMAL_SEPARATOR);

        if (isInsert || values.containsKey(Tables.Currencies.SYMBOL_FORMAT))
        {
            final String symbolFormat = values.getAsString(Tables.Currencies.SYMBOL_FORMAT);
            if (TextUtils.isEmpty(symbolFormat) || !(Tables.Currencies.SymbolFormat.LEFT_CLOSE.equals(symbolFormat)
                    || Tables.Currencies.SymbolFormat.LEFT_FAR.equals(symbolFormat)
                    || Tables.Currencies.SymbolFormat.RIGHT_CLOSE.equals(symbolFormat)
                    || Tables.Currencies.SymbolFormat.RIGHT_FAR.equals(symbolFormat)))
                throw new IllegalArgumentException(Tables.Currencies.SYMBOL_FORMAT + " must be equal to one of these values: " + Tables.Currencies.SymbolFormat.LEFT_CLOSE + ", " + Tables.Currencies.SymbolFormat.LEFT_FAR + ", " + Tables.Currencies.SymbolFormat.RIGHT_CLOSE + ", " + Tables.Currencies.SymbolFormat.RIGHT_FAR);
        }

        if (isInsert || values.containsKey(Tables.Currencies.EXCHANGE_RATE))
        {
            final Double exchangeRate = values.getAsDouble(Tables.Currencies.EXCHANGE_RATE);
            if (exchangeRate == null || exchangeRate < 0)
                throw new IllegalArgumentException(Tables.Currencies.EXCHANGE_RATE + " must be > 0.");
        }

        if (isInsert && !values.containsKey(Tables.Currencies.IS_DEFAULT))
            values.put(Tables.Currencies.IS_DEFAULT, false);
    }
}