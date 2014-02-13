package com.code44.finance.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.App;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.Tables;
import com.code44.finance.parsers.JTags;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BackupUtils
{
    public static final int BACKUP_VERSION = 5;

    public static JsonObject generateBackupJson()
    {
        final Context context = App.getAppContext();
        final JsonObject json = new JsonObject();

        // Add export object values
        final long timestamp = System.currentTimeMillis();
        json.addProperty(JTags.Export.DATE, DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));
        json.addProperty(JTags.Export.DATE_TS, timestamp);
        json.addProperty(JTags.Export.VERSION, BACKUP_VERSION);

        // Add currencies
        json.add(JTags.Currency.LIST, getCurrencies());

        // Add accounts
        json.add(JTags.Account.LIST, getAccounts());

        // Add categories
        json.add(JTags.Category.LIST, getCategories());

        // Add transactions
        json.add(JTags.Transaction.LIST, getTransactions());

        return json;
    }

    private static JsonArray getCurrencies()
    {
        JsonArray jsonArray = new JsonArray();
        //noinspection ConstantConditions
        Cursor c = DBHelper.get(App.getAppContext()).getReadableDatabase().query(Tables.Currencies.TABLE_NAME, null, null, null, null, null, null);
        try
        {
            if (c != null && c.moveToFirst())
            {
                final int iId = c.getColumnIndex(Tables.Currencies.ID);
                final int iServerId = c.getColumnIndex(Tables.Currencies.SERVER_ID);
                final int iCode = c.getColumnIndex(Tables.Currencies.CODE);
                final int iSymbol = c.getColumnIndex(Tables.Currencies.SYMBOL);
                final int iDecimals = c.getColumnIndex(Tables.Currencies.DECIMALS);
                final int iDecimalSeparator = c.getColumnIndex(Tables.Currencies.DECIMAL_SEPARATOR);
                final int iGroupSeparator = c.getColumnIndex(Tables.Currencies.GROUP_SEPARATOR);
                final int iSymbolFormat = c.getColumnIndex(Tables.Currencies.SYMBOL_FORMAT);
                final int iIsDefault = c.getColumnIndex(Tables.Currencies.IS_DEFAULT);
                final int iExchangeRate = c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE);
                final int iDeleteState = c.getColumnIndex(Tables.Currencies.DELETE_STATE);

                do
                {
                    JsonObject json = new JsonObject();
                    json.addProperty(JTags.Currency.ID, c.getLong(iId));
                    json.addProperty(JTags.Currency.SERVER_ID, c.getString(iServerId));
                    json.addProperty(JTags.Currency.CODE, c.getString(iCode));
                    json.addProperty(JTags.Currency.SYMBOL, c.getString(iSymbol));
                    json.addProperty(JTags.Currency.DECIMALS, c.getInt(iDecimals));
                    json.addProperty(JTags.Currency.DECIMAL_SEPARATOR, c.getString(iDecimalSeparator));
                    json.addProperty(JTags.Currency.GROUP_SEPARATOR, c.getString(iGroupSeparator));
                    json.addProperty(JTags.Currency.SYMBOL_FORMAT, c.getString(iSymbolFormat));
                    json.addProperty(JTags.Currency.IS_DEFAULT, c.getInt(iIsDefault));
                    json.addProperty(JTags.Currency.EXCHANGE_RATE, c.getDouble(iExchangeRate));
                    json.addProperty(JTags.Currency.DELETE_STATE, c.getInt(iDeleteState));
                    jsonArray.add(json);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return jsonArray;
    }

    private static JsonArray getAccounts()
    {
        JsonArray jsonArray = new JsonArray();
        //noinspection ConstantConditions
        Cursor c = DBHelper.get(App.getAppContext()).getReadableDatabase().query(Tables.Accounts.TABLE_NAME, null, null, null, null, null, null);
        try
        {
            if (c != null && c.moveToFirst())
            {
                final int iId = c.getColumnIndex(Tables.Accounts.ID);
                final int iServerId = c.getColumnIndex(Tables.Accounts.SERVER_ID);
                final int iCurrencyId = c.getColumnIndex(Tables.Accounts.CURRENCY_ID);
                final int iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
                final int iNote = c.getColumnIndex(Tables.Accounts.NOTE);
                final int iBalance = c.getColumnIndex(Tables.Accounts.BALANCE);
                final int iShowInTotals = c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS);
                final int iShowInSelection = c.getColumnIndex(Tables.Accounts.SHOW_IN_SELECTION);
                final int iOrigin = c.getColumnIndex(Tables.Accounts.ORIGIN);
                final int iDeleteState = c.getColumnIndex(Tables.Accounts.DELETE_STATE);

                do
                {
                    JsonObject json = new JsonObject();
                    json.addProperty(JTags.Account.ID, c.getLong(iId));
                    json.addProperty(JTags.Account.SERVER_ID, c.getString(iServerId));
                    json.addProperty(JTags.Account.CURRENCY_ID, c.getLong(iCurrencyId));
                    json.addProperty(JTags.Account.TITLE, c.getString(iTitle));
                    json.addProperty(JTags.Account.NOTE, c.getString(iNote));
                    json.addProperty(JTags.Account.BALANCE, c.getDouble(iBalance));
                    json.addProperty(JTags.Account.SHOW_IN_TOTALS, c.getInt(iShowInTotals));
                    json.addProperty(JTags.Account.SHOW_IN_SELECTION, c.getInt(iShowInSelection));
                    json.addProperty(JTags.Account.ORIGIN, c.getInt(iOrigin));
                    json.addProperty(JTags.Account.DELETE_STATE, c.getInt(iDeleteState));
                    jsonArray.add(json);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return jsonArray;
    }

    private static JsonArray getCategories()
    {
        JsonArray jsonArray = new JsonArray();
        //noinspection ConstantConditions
        Cursor c = DBHelper.get(App.getAppContext()).getReadableDatabase().query(Tables.Categories.TABLE_NAME, null, null, null, null, null, null);
        ;
        try
        {
            if (c != null && c.moveToFirst())
            {
                final int iId = c.getColumnIndex(Tables.Categories.ID);
                final int iServerId = c.getColumnIndex(Tables.Categories.SERVER_ID);
                final int iParentId = c.getColumnIndex(Tables.Categories.PARENT_ID);
                final int iTitle = c.getColumnIndex(Tables.Categories.TITLE);
                final int iColor = c.getColumnIndex(Tables.Categories.COLOR);
                final int iLevel = c.getColumnIndex(Tables.Categories.LEVEL);
                final int iType = c.getColumnIndex(Tables.Categories.TYPE);
                final int iOrigin = c.getColumnIndex(Tables.Categories.ORIGIN);
                final int iOrder = c.getColumnIndex(Tables.Categories.ORDER);
                final int iParentOrder = c.getColumnIndex(Tables.Categories.PARENT_ORDER);
                final int iDeleteState = c.getColumnIndex(Tables.Categories.DELETE_STATE);

                do
                {
                    JsonObject json = new JsonObject();
                    json.addProperty(JTags.Category.ID, c.getLong(iId));
                    json.addProperty(JTags.Category.SERVER_ID, c.getString(iServerId));
                    json.addProperty(JTags.Category.PARENT_ID, c.getLong(iParentId));
                    json.addProperty(JTags.Category.TITLE, c.getString(iTitle));
                    json.addProperty(JTags.Category.COLOR, c.getInt(iColor));
                    json.addProperty(JTags.Category.LEVEL, c.getInt(iLevel));
                    json.addProperty(JTags.Category.TYPE, c.getInt(iType));
                    json.addProperty(JTags.Category.ORIGIN, c.getInt(iOrigin));
                    json.addProperty(JTags.Category.ORDER, c.getInt(iOrder));
                    json.addProperty(JTags.Category.PARENT_ORDER, c.getInt(iParentOrder));
                    json.addProperty(JTags.Category.DELETE_STATE, c.getInt(iDeleteState));
                    jsonArray.add(json);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return jsonArray;
    }

    private static JsonArray getTransactions()
    {
        JsonArray jsonArray = new JsonArray();
        //noinspection ConstantConditions
        Cursor c = DBHelper.get(App.getAppContext()).getReadableDatabase().query(Tables.Transactions.TABLE_NAME, null, null, null, null, null, null);
        ;
        try
        {
            if (c != null && c.moveToFirst())
            {
                final int iId = c.getColumnIndex(Tables.Transactions.ID);
                final int iServerId = c.getColumnIndex(Tables.Transactions.SERVER_ID);
                final int iAccountFromId = c.getColumnIndex(Tables.Transactions.ACCOUNT_FROM_ID);
                final int iAccountToId = c.getColumnIndex(Tables.Transactions.ACCOUNT_TO_ID);
                final int iCategoryId = c.getColumnIndex(Tables.Transactions.CATEGORY_ID);
                final int iDate = c.getColumnIndex(Tables.Transactions.DATE);
                final int iAmount = c.getColumnIndex(Tables.Transactions.AMOUNT);
                final int iExchangeRate = c.getColumnIndex(Tables.Transactions.EXCHANGE_RATE);
                final int iNote = c.getColumnIndex(Tables.Transactions.NOTE);
                final int iState = c.getColumnIndex(Tables.Transactions.STATE);
                final int iShowInTotals = c.getColumnIndex(Tables.Transactions.SHOW_IN_TOTALS);
                final int iDeleteState = c.getColumnIndex(Tables.Transactions.DELETE_STATE);

                do
                {
                    JsonObject json = new JsonObject();
                    json.addProperty(JTags.Transaction.ID, c.getLong(iId));
                    json.addProperty(JTags.Transaction.SERVER_ID, c.getString(iServerId));
                    json.addProperty(JTags.Transaction.ACCOUNT_FROM_ID, c.getLong(iAccountFromId));
                    json.addProperty(JTags.Transaction.ACCOUNT_TO_ID, c.getLong(iAccountToId));
                    json.addProperty(JTags.Transaction.CATEGORY_ID, c.getLong(iCategoryId));
                    json.addProperty(JTags.Transaction.DATE, c.getLong(iDate));
                    json.addProperty(JTags.Transaction.AMOUNT, c.getDouble(iAmount));
                    json.addProperty(JTags.Transaction.EXCHANGE_RATE, c.getDouble(iExchangeRate));
                    json.addProperty(JTags.Transaction.NOTE, c.getString(iNote));
                    json.addProperty(JTags.Transaction.STATE, c.getInt(iState));
                    json.addProperty(JTags.Transaction.SHOW_IN_TOTALS, c.getInt(iShowInTotals));
                    json.addProperty(JTags.Transaction.DELETE_STATE, c.getInt(iDeleteState));
                    jsonArray.add(json);
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return jsonArray;
    }
}
