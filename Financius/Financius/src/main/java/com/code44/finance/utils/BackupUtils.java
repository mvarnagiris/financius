package com.code44.finance.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.Tables;
import com.code44.finance.parsers.JTags;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BackupUtils
{
    public static final int BACKUP_VERSION = 4;
    // -----------------------------------------------------------------------------------------------------------------
    public static final String BACKUP_FILE_PREFIX = "snapshot_";
    public static final String BACKUP_FILE_SUFFIX = ".json";
    public static final String BACKUP_MIME_TYPE = "application/json";
    // -----------------------------------------------------------------------------------------------------------------


    public static File generateBackupFile(Context context) throws Exception
    {
        context = context.getApplicationContext();
        File jsonFile = File.createTempFile("temp_" + System.currentTimeMillis(), "json");
        if (jsonFile == null)
            throw new IOException("Could not create file for JSON.");

        // Prepare writer
        final JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8"));
        writer.setIndent("  ");

        // Start
        writer.beginObject();

        // Add export object values
        final long timestamp = System.currentTimeMillis();
        writer.name(JTags.Export.DATE).value(DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));
        writer.name(JTags.Export.DATE_TS).value(timestamp);
        writer.name(JTags.Export.VERSION).value(BACKUP_VERSION);

        // Add currencies
        writer.name(JTags.Currency.LIST).beginArray();
        writeCurrencies(context, writer);
        writer.endArray();

        // Add accounts
        writer.name(JTags.Account.LIST).beginArray();
        writeAccounts(context, writer);
        writer.endArray();

        // Add categories
        writer.name(JTags.Category.LIST).beginArray();
        writeCategories(context, writer);
        writer.endArray();

        // Add transactions
        writer.name(JTags.Transaction.LIST).beginArray();
        writeTransactions(context, writer);
        writer.endArray();

        // End
        writer.endObject();
        writer.close();

        return jsonFile;
    }

    private static void writeCurrencies(Context context, JsonWriter writer) throws IOException
    {
        Cursor c = null;
        try
        {
            c = DBHelper.get(context).getReadableDatabase().query(Tables.Currencies.TABLE_NAME, null, null, null, null, null, null);

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
                final int iTimestamp = c.getColumnIndex(Tables.Currencies.TIMESTAMP);
                final int iSyncState = c.getColumnIndex(Tables.Currencies.SYNC_STATE);
                final int iDeleteState = c.getColumnIndex(Tables.Currencies.DELETE_STATE);

                do
                {
                    writer.beginObject();
                    writer.name(JTags.Currency.ID).value(c.getLong(iId));
                    writer.name(JTags.Currency.SERVER_ID).value(c.getString(iServerId));
                    writer.name(JTags.Currency.CODE).value(c.getString(iCode));
                    writer.name(JTags.Currency.SYMBOL).value(c.getString(iSymbol));
                    writer.name(JTags.Currency.DECIMALS).value(c.getInt(iDecimals));
                    writer.name(JTags.Currency.DECIMAL_SEPARATOR).value(c.getString(iDecimalSeparator));
                    writer.name(JTags.Currency.GROUP_SEPARATOR).value(c.getString(iGroupSeparator));
                    writer.name(JTags.Currency.SYMBOL_FORMAT).value(c.getString(iSymbolFormat));
                    writer.name(JTags.Currency.IS_DEFAULT).value(c.getInt(iIsDefault));
                    writer.name(JTags.Currency.EXCHANGE_RATE).value(c.getDouble(iExchangeRate));
                    writer.name(JTags.Currency.TIMESTAMP).value(c.getLong(iTimestamp));
                    writer.name(JTags.Currency.SYNC_STATE).value(c.getInt(iSyncState));
                    writer.name(JTags.Currency.DELETE_STATE).value(c.getInt(iDeleteState));
                    writer.endObject();
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

    private static void writeAccounts(Context context, JsonWriter writer) throws IOException
    {
        Cursor c = null;
        try
        {
            c = DBHelper.get(context).getReadableDatabase().query(Tables.Accounts.TABLE_NAME, null, null, null, null, null, null);

            if (c != null && c.moveToFirst())
            {
                final int iId = c.getColumnIndex(Tables.Accounts.ID);
                final int iServerId = c.getColumnIndex(Tables.Accounts.SERVER_ID);
                final int iCurrencyId = c.getColumnIndex(Tables.Accounts.CURRENCY_ID);
                final int iTypeResName = c.getColumnIndex(Tables.Accounts.TYPE_RES_NAME);
                final int iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
                final int iNote = c.getColumnIndex(Tables.Accounts.NOTE);
                final int iBalance = c.getColumnIndex(Tables.Accounts.BALANCE);
                final int iOverdraft = c.getColumnIndex(Tables.Accounts.OVERDRAFT);
                final int iShowInTotals = c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS);
                final int iShowInSelection = c.getColumnIndex(Tables.Accounts.SHOW_IN_SELECTION);
                final int iOrigin = c.getColumnIndex(Tables.Accounts.ORIGIN);
                final int iTimestamp = c.getColumnIndex(Tables.Accounts.TIMESTAMP);
                final int iSyncState = c.getColumnIndex(Tables.Accounts.SYNC_STATE);
                final int iDeleteState = c.getColumnIndex(Tables.Accounts.DELETE_STATE);

                do
                {
                    writer.beginObject();
                    writer.name(JTags.Account.ID).value(c.getLong(iId));
                    writer.name(JTags.Account.SERVER_ID).value(c.getString(iServerId));
                    writer.name(JTags.Account.CURRENCY_ID).value(c.getLong(iCurrencyId));
                    writer.name(JTags.Account.TYPE_RES_NAME).value(c.getString(iTypeResName));
                    writer.name(JTags.Account.TITLE).value(c.getString(iTitle));
                    writer.name(JTags.Account.NOTE).value(c.getString(iNote));
                    writer.name(JTags.Account.BALANCE).value(c.getDouble(iBalance));
                    writer.name(JTags.Account.OVERDRAFT).value(c.getDouble(iOverdraft));
                    writer.name(JTags.Account.SHOW_IN_TOTALS).value(c.getInt(iShowInTotals));
                    writer.name(JTags.Account.SHOW_IN_SELECTION).value(c.getInt(iShowInSelection));
                    writer.name(JTags.Account.ORIGIN).value(c.getInt(iOrigin));
                    writer.name(JTags.Account.TIMESTAMP).value(c.getLong(iTimestamp));
                    writer.name(JTags.Account.SYNC_STATE).value(c.getInt(iSyncState));
                    writer.name(JTags.Account.DELETE_STATE).value(c.getInt(iDeleteState));
                    writer.endObject();
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

    private static void writeCategories(Context context, JsonWriter writer) throws IOException
    {
        Cursor c = null;
        try
        {
            c = DBHelper.get(context).getReadableDatabase().query(Tables.Categories.TABLE_NAME, null, null, null, null, null, null);

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
                final int iTimestamp = c.getColumnIndex(Tables.Categories.TIMESTAMP);
                final int iSyncState = c.getColumnIndex(Tables.Categories.SYNC_STATE);
                final int iDeleteState = c.getColumnIndex(Tables.Categories.DELETE_STATE);

                do
                {
                    writer.beginObject();
                    writer.name(JTags.Category.ID).value(c.getLong(iId));
                    writer.name(JTags.Category.SERVER_ID).value(c.getString(iServerId));
                    writer.name(JTags.Category.PARENT_ID).value(c.getLong(iParentId));
                    writer.name(JTags.Category.TITLE).value(c.getString(iTitle));
                    writer.name(JTags.Category.COLOR).value(c.getInt(iColor));
                    writer.name(JTags.Category.LEVEL).value(c.getInt(iLevel));
                    writer.name(JTags.Category.TYPE).value(c.getInt(iType));
                    writer.name(JTags.Category.ORIGIN).value(c.getInt(iOrigin));
                    writer.name(JTags.Category.ORDER).value(c.getInt(iOrder));
                    writer.name(JTags.Category.PARENT_ORDER).value(c.getInt(iParentOrder));
                    writer.name(JTags.Category.TIMESTAMP).value(c.getLong(iTimestamp));
                    writer.name(JTags.Category.SYNC_STATE).value(c.getInt(iSyncState));
                    writer.name(JTags.Category.DELETE_STATE).value(c.getInt(iDeleteState));
                    writer.endObject();
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

    private static void writeTransactions(Context context, JsonWriter writer) throws IOException
    {
        Cursor c = null;
        try
        {
            c = DBHelper.get(context).getReadableDatabase().query(Tables.Transactions.TABLE_NAME, null, null, null, null, null, null);

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
                final int iTimestamp = c.getColumnIndex(Tables.Transactions.TIMESTAMP);
                final int iSyncState = c.getColumnIndex(Tables.Transactions.SYNC_STATE);
                final int iDeleteState = c.getColumnIndex(Tables.Transactions.DELETE_STATE);

                do
                {
                    writer.beginObject();
                    writer.name(JTags.Transaction.ID).value(c.getLong(iId));
                    writer.name(JTags.Transaction.SERVER_ID).value(c.getString(iServerId));
                    writer.name(JTags.Transaction.ACCOUNT_FROM_ID).value(c.getLong(iAccountFromId));
                    writer.name(JTags.Transaction.ACCOUNT_TO_ID).value(c.getLong(iAccountToId));
                    writer.name(JTags.Transaction.CATEGORY_ID).value(c.getLong(iCategoryId));
                    writer.name(JTags.Transaction.DATE).value(c.getLong(iDate));
                    writer.name(JTags.Transaction.AMOUNT).value(c.getDouble(iAmount));
                    writer.name(JTags.Transaction.EXCHANGE_RATE).value(c.getDouble(iExchangeRate));
                    writer.name(JTags.Transaction.NOTE).value(c.getString(iNote));
                    writer.name(JTags.Transaction.STATE).value(c.getInt(iState));
                    writer.name(JTags.Transaction.SHOW_IN_TOTALS).value(c.getInt(iShowInTotals));
                    writer.name(JTags.Transaction.TIMESTAMP).value(c.getLong(iTimestamp));
                    writer.name(JTags.Transaction.SYNC_STATE).value(c.getInt(iSyncState));
                    writer.name(JTags.Transaction.DELETE_STATE).value(c.getInt(iDeleteState));
                    writer.endObject();
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
