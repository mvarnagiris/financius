package com.code44.finance.services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import com.code44.finance.db.Tables;
import com.code44.finance.parsers.BackupParser;
import com.code44.finance.parsers.JTags;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.utils.StringUtils;
import com.google.gson.stream.JsonWriter;
import org.json.JSONObject;

import java.io.*;

public class BackupService extends AbstractService
{
    public static final String EXTRA_FILE_PATH = BackupService.class.getName() + ".EXTRA_FILE_PATH";
    public static final int EXPORT_VERSION = 1;
    public static final int RT_EXPORT_CSV = 1;
    public static final int RT_EXPORT_JSON = 2;
    public static final int RT_IMPORT_JSON = 3;
    private static final String ROOT_FOLDER_NAME = "Financius";
    private static final String CSV_FILE_NAME_PREFIX = "csv_";
    private static final String JSON_FILE_NAME_PREFIX = "backup_";

    public void rtExportCSV(Intent intent) throws Exception
    {
        File csvFile = getExportFile(CSV_FILE_NAME_PREFIX, ".csv");
        if (csvFile == null)
        {
            throw new IOException("Could not create file for CSV.");
        }

        Cursor c = null;
        Writer writer = null;
        try
        {
            // Prepare writer
            final String delimiter = ";";
            writer = new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8");
            final int dateFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
            final int timeFlags = DateUtils.FORMAT_SHOW_TIME;

            // Write Transactions
            c = getContentResolver().query(
                    TransactionsProvider.uriTransactions(getApplicationContext()),
                    new String[]{Tables.Transactions.DATE, Tables.Accounts.AccountFrom.S_TITLE, Tables.Accounts.AccountTo.S_TITLE,
                            Tables.Transactions.CATEGORY_ID, Tables.Categories.CategoriesChild.S_TITLE, Tables.Transactions.AMOUNT, Tables.Transactions.NOTE},
                    Tables.Transactions.DELETE_STATE + "<>?", new String[]{String.valueOf(Tables.DeleteState.DELETED)}, null);
            if (c != null && c.moveToFirst())
            {
                final int iDate = c.getColumnIndex(Tables.Transactions.DATE);
                final int iAccountFrom = c.getColumnIndex(Tables.Accounts.AccountFrom.TITLE);
                final int iAccountTo = c.getColumnIndex(Tables.Accounts.AccountTo.TITLE);
                final int iCategory = c.getColumnIndex(Tables.Categories.CategoriesChild.TITLE);
                final int iAmount = c.getColumnIndex(Tables.Transactions.AMOUNT);
                final int iNote = c.getColumnIndex(Tables.Transactions.NOTE);

                do
                {
                    writer.append(DateUtils.formatDateTime(this, c.getLong(iDate), dateFlags) + delimiter
                            + DateUtils.formatDateTime(this, c.getLong(iDate), timeFlags) + delimiter + c.getString(iAccountFrom) + delimiter
                            + c.getString(iAccountTo) + delimiter + c.getString(iCategory) + delimiter + String.format("%.2f", c.getDouble(iAmount))
                            + delimiter + c.getString(iNote) + "\n");
                }
                while (c.moveToNext());
            }

            // Finish writing
            writer.flush();

            new SingleMediaScanner(this, csvFile.getAbsolutePath());
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();

            try
            {
                if (writer != null)
                    writer.close();
            }
            catch (Exception e)
            {
                // Ignore
            }
        }
    }

    // Request type methods
    // --------------------------------------------------------------------------------------------------------------------------------

    public void rtExportJSON(Intent intent) throws Exception
    {
        File jsonFile = getExportFile(JSON_FILE_NAME_PREFIX, ".json");
        if (jsonFile == null)
            throw new IOException("Could not create file for JSON.");

        // Prepare writer
        final JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8"));
        writer.setIndent("  ");

        // Start
        writer.beginObject();

        // Add export object values
        final long timestamp = System.currentTimeMillis();
        writer.name(JTags.Export.DATE).value(
                DateUtils.formatDateTime(this, timestamp, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));
        writer.name(JTags.Export.DATE_TS).value(timestamp);
        writer.name(JTags.Export.VERSION).value(EXPORT_VERSION);

        // Add accounts
        writer.name(JTags.Account.LIST).beginArray();
        writeAccounts(writer);
        writer.endArray();

        // Add categories
        writer.name(JTags.Category.LIST).beginArray();
        writeCategories(writer);
        writer.endArray();

        // Add transactions
        writer.name(JTags.Transaction.LIST).beginArray();
        writeTransactions(writer);
        writer.endArray();

        // End
        writer.endObject();
        writer.close();

        new SingleMediaScanner(this, jsonFile.getAbsolutePath());
    }

    public void rtImportJSON(Intent intent) throws Exception
    {
        // Get values
        final String filePath = intent.getStringExtra(EXTRA_FILE_PATH);

        // Prepare file
        final File file = new File(filePath);
        if (!file.exists())
            throw new IOException("File does not exist.");
        else if (!file.canRead())
            throw new IOException("Cannot read file.");

        Log.i("TASD", filePath);
        // Read file
        InputStream is = null;
        JSONObject jObject = null;
        try
        {
            is = new FileInputStream(file);
            jObject = new JSONObject(StringUtils.readInputStream(is));
        }
        finally
        {
            if (is != null)
                is.close();
        }

        // Parse JSON
        if (jObject != null)
            new BackupParser().parseAndStore(this, jObject);

        // Notify
        getContentResolver().notifyChange(AccountsProvider.uriAccounts(this), null);
        getContentResolver().notifyChange(CategoriesProvider.uriCategories(this), null);
        getContentResolver().notifyChange(TransactionsProvider.uriTransactions(this), null);
    }

    @Override
    protected void handleRequest(Intent intent, int requestType, long startTime, long lastSuccessfulWorkTime) throws Exception
    {
        switch (requestType)
        {
            case RT_EXPORT_CSV:
                rtExportCSV(intent);
                break;

            case RT_EXPORT_JSON:
                rtExportJSON(intent);
                break;

            case RT_IMPORT_JSON:
                rtImportJSON(intent);
                break;
        }
    }

    // Private methods
    // --------------------------------------------------------------------------------------------------------------------------------

    private File getExportFile(String filePrefix, String fileExtention) throws IOException
    {
        File dir = null;
        File exportFile = null;

        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
            dir = new File(Environment.getExternalStorageDirectory(), ROOT_FOLDER_NAME);
        else
            throw new IOException("External memory is not mounted or is read only.");

        if (dir != null)
        {
            if (!dir.exists())
                dir.mkdirs();
            exportFile = new File(dir, filePrefix + System.currentTimeMillis() + fileExtention);
        }

        return exportFile;
    }

    private void writeAccounts(JsonWriter writer) throws IOException
    {
//        Cursor c = null;
//        try
//        {
//            c = getContentResolver().query(
//                    AccountsProvider.uriAccounts(this),
//                    new String[]{Tables.Accounts.SERVER_ID, Tables.Accounts.TITLE, Tables.Accounts.ORIGIN, Tables.Accounts.TIMESTAMP,
//                            Tables.Accounts.DELETE_STATE}, Tables.Accounts.ORIGIN + "<>?", new String[]{String.valueOf(Tables.Accounts.Origin.SYSTEM)},
//                    null);
//
//            if (c != null && c.moveToFirst())
//            {
//                final int iServerId = c.getColumnIndex(Tables.Accounts.SERVER_ID);
//                final int iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
//                final int iOrigin = c.getColumnIndex(Tables.Accounts.ORIGIN);
//                final int iTimestamp = c.getColumnIndex(Tables.Accounts.TIMESTAMP);
//                final int iDeleteState = c.getColumnIndex(Tables.Accounts.DELETE_STATE);
//
//                do
//                {
//                    writer.beginObject();
//                    writer.name(JTags.Account.SERVER_ID).value(c.getString(iServerId));
//                    writer.name(JTags.Account.TITLE).value(c.getString(iTitle));
//                    writer.name(JTags.Account.ORIGIN).value(c.getInt(iOrigin));
//                    writer.name(JTags.Account.TIMESTAMP).value(c.getLong(iTimestamp));
//                    writer.name(JTags.Account.DELETE_STATE).value(c.getInt(iDeleteState));
//                    writer.endObject();
//                }
//                while (c.moveToNext());
//            }
//        }
//        finally
//        {
//            if (c != null && !c.isClosed())
//                c.close();
//        }
    }

    private void writeCategories(JsonWriter writer) throws IOException
    {
//        Cursor c = null;
//        try
//        {
//            c = getContentResolver().query(
//                    CategoriesProvider.uriCategories(this),
//                    new String[]{Tables.Categories.SERVER_ID, Tables.Categories.PARENT_ID, Tables.Categories.TITLE, Tables.Categories.LEVEL,
//                            Tables.Categories.TYPE, Tables.Categories.ORIGIN, Tables.Categories.TIMESTAMP, Tables.Categories.DELETE_STATE},
//                    Tables.Categories.LEVEL + ">?",
//                    new String[]{"0"},
//                    "case " + Tables.Categories.LEVEL + " when 1 then " + Tables.Categories.T_ID + " else " + Tables.Categories.PARENT_ID + " end, "
//                            + Tables.Categories.LEVEL + ", " + Tables.Categories.TITLE);
//
//            if (c != null && c.moveToFirst())
//            {
//                final int iServerId = c.getColumnIndex(Tables.Categories.SERVER_ID);
//                final int iTitle = c.getColumnIndex(Tables.Categories.TITLE);
//                final int iLevel = c.getColumnIndex(Tables.Categories.LEVEL);
//                final int iType = c.getColumnIndex(Tables.Categories.TYPE);
//                final int iOrigin = c.getColumnIndex(Tables.Categories.ORIGIN);
//                final int iTimestamp = c.getColumnIndex(Tables.Categories.TIMESTAMP);
//                final int iDeleteState = c.getColumnIndex(Tables.Categories.DELETE_STATE);
//
//                int level = -1;
//                int newLevel = -1;
//
//                do
//                {
//                    newLevel = c.getInt(iLevel);
//                    if (newLevel == 1)
//                    {
//                        // Is parent category.
//                        if (level == 2)
//                        {
//                            // Had a section before. Need to close array.
//                            writer.endArray();
//                            writer.endObject();
//                        }
//                        else if (level == 1)
//                        {
//                            writer.endObject();
//                        }
//                    }
//                    else
//                    {
//                        // Is child category.
//                        if (level == 1)
//                        {
//                            // Had parent before. Need to start array.
//                            writer.name(JTags.Category.LIST).beginArray();
//                        }
//                    }
//
//                    writer.beginObject();
//                    writer.name(JTags.Category.SERVER_ID).value(c.getString(iServerId));
//                    writer.name(JTags.Category.TITLE).value(c.getString(iTitle));
//                    writer.name(JTags.Category.TYPE).value(c.getInt(iType));
//                    writer.name(JTags.Category.ORIGIN).value(c.getInt(iOrigin));
//                    writer.name(JTags.Category.TIMESTAMP).value(c.getLong(iTimestamp));
//                    writer.name(JTags.Category.DELETE_STATE).value(c.getInt(iDeleteState));
//                    if (newLevel == 2)
//                        writer.endObject();
//
//                    level = newLevel;
//                }
//                while (c.moveToNext());
//
//                if (level == 1)
//                {
//                    writer.endObject();
//                }
//                else if (level == 2)
//                {
//                    // Had a section before. Need to close array.
//                    writer.endArray();
//                    writer.endObject();
//                }
//            }
//        }
//        finally
//        {
//            if (c != null && !c.isClosed())
//                c.close();
//        }
    }

    private void writeTransactions(JsonWriter writer) throws IOException
    {
//        Cursor c = null;
//        try
//        {
//            c = getContentResolver().query(
//                    TransactionsProvider.uriTransactions(getApplicationContext()),
//                    new String[]{Tables.Transactions.SERVER_ID, Tables.Transactions.DATE, Tables.Accounts.AccountFrom.S_SERVER_ID,
//                            Tables.Accounts.AccountTo.S_SERVER_ID, Tables.Categories.CategoriesChild.S_SERVER_ID, Tables.Transactions.AMOUNT,
//                            Tables.Transactions.NOTE, Tables.Transactions.TIMESTAMP, Tables.Transactions.DELETE_STATE}, null, null, null);
//
//            if (c != null && c.moveToFirst())
//            {
//                final int iServerId = c.getColumnIndex(Tables.Transactions.SERVER_ID);
//                final int iAccountFromServerId = c.getColumnIndex(Tables.Accounts.AccountFrom.SERVER_ID);
//                final int iAccountToServerId = c.getColumnIndex(Tables.Accounts.AccountTo.SERVER_ID);
//                final int iCategoryServerId = c.getColumnIndex(Tables.Categories.CategoriesChild.SERVER_ID);
//                final int iDate = c.getColumnIndex(Tables.Transactions.DATE);
//                final int iAmount = c.getColumnIndex(Tables.Transactions.AMOUNT);
//                final int iNote = c.getColumnIndex(Tables.Transactions.NOTE);
//                final int iTimestamp = c.getColumnIndex(Tables.Transactions.TIMESTAMP);
//                final int iDeleteState = c.getColumnIndex(Tables.Transactions.DELETE_STATE);
//
//                do
//                {
//                    writer.beginObject();
//                    writer.name(JTags.Transaction.SERVER_ID).value(c.getString(iServerId));
//                    writer.name(JTags.Transaction.ACCOUNT_FROM_SERVER_ID).value(c.getString(iAccountFromServerId));
//                    writer.name(JTags.Transaction.ACCOUNT_TO_SERVER_ID).value(c.getString(iAccountToServerId));
//                    writer.name(JTags.Transaction.CATEGORY_SERVER_ID).value(c.getString(iCategoryServerId));
//                    writer.name(JTags.Transaction.DATE).value(c.getLong(iDate));
//                    writer.name(JTags.Transaction.AMOUNT).value(Math.round(c.getDouble(iAmount) * 100.0) / 100.0);
//                    writer.name(JTags.Transaction.NOTE).value(c.getString(iNote));
//                    writer.name(JTags.Transaction.TIMESTAMP).value(c.getLong(iTimestamp));
//                    writer.name(JTags.Transaction.DELETE_STATE).value(c.getInt(iDeleteState));
//                    writer.endObject();
//                }
//                while (c.moveToNext());
//            }
//        }
//        finally
//        {
//            if (c != null && !c.isClosed())
//                c.close();
//        }
    }

    // Media scanner
    // --------------------------------------------------------------------------------------------------------------------------------

    private static class SingleMediaScanner implements MediaScannerConnectionClient
    {
        private MediaScannerConnection mediaScannerConnection;
        private String path;

        public SingleMediaScanner(Context context, String path)
        {
            this.path = path;
            mediaScannerConnection = new MediaScannerConnection(context, this);
            mediaScannerConnection.connect();
        }

        @Override
        public void onMediaScannerConnected()
        {
            mediaScannerConnection.scanFile(path, null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri)
        {
            mediaScannerConnection.disconnect();
        }
    }
}