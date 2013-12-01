package com.code44.finance.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.DBUpgrade;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.BackupUtils;
import com.code44.finance.utils.CurrenciesHelper;
import com.code44.finance.utils.NotifyUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BackupParser extends Parser
{
    private static final String KEY_CURRENCIES = "KEY_CURRENCIES";
    private static final String KEY_ACCOUNTS = "KEY_ACCOUNTS";
    private static final String KEY_CATEGORIES = "KEY_CATEGORIES";
    private static final String KEY_TRANSACTIONS = "KEY_TRANSACTIONS";

    public BackupParser()
    {
    }

    private static void parseCurrencies(ParsedValues parsedValues, JSONObject json) throws JSONException
    {
        final List<ContentValues> list = new ArrayList<ContentValues>();
        final JSONArray jArray = json.getJSONArray(JTags.Currency.LIST);
        JSONObject jObject;
        ContentValues values;
        for (int i = 0; i < jArray.length(); i++)
        {
            jObject = jArray.getJSONObject(i);
            values = new ContentValues();
            values.put(Tables.Currencies.ID, jObject.getLong(JTags.Currency.ID));
            values.put(Tables.Currencies.SERVER_ID, jObject.getString(JTags.Currency.SERVER_ID));
            values.put(Tables.Currencies.CODE, jObject.getString(JTags.Currency.CODE));
            values.put(Tables.Currencies.SYMBOL, jObject.getString(JTags.Currency.SYMBOL));
            values.put(Tables.Currencies.DECIMALS, jObject.getInt(JTags.Currency.DECIMALS));
            values.put(Tables.Currencies.DECIMAL_SEPARATOR, jObject.getString(JTags.Currency.DECIMAL_SEPARATOR));
            values.put(Tables.Currencies.GROUP_SEPARATOR, jObject.getString(JTags.Currency.GROUP_SEPARATOR));
            values.put(Tables.Currencies.SYMBOL_FORMAT, jObject.getString(JTags.Currency.SYMBOL_FORMAT));
            values.put(Tables.Currencies.IS_DEFAULT, jObject.getInt(JTags.Currency.IS_DEFAULT));
            values.put(Tables.Currencies.EXCHANGE_RATE, jObject.getDouble(JTags.Currency.EXCHANGE_RATE));
            values.put(Tables.Currencies.TIMESTAMP, jObject.getLong(JTags.Currency.TIMESTAMP));
            values.put(Tables.Currencies.SYNC_STATE, jObject.getInt(JTags.Currency.SYNC_STATE));
            values.put(Tables.Currencies.DELETE_STATE, jObject.getInt(JTags.Currency.DELETE_STATE));
            list.add(values);
        }
        parsedValues.putList(KEY_CURRENCIES, list);
    }

    private static void parseAccounts(ParsedValues parsedValues, JSONObject json) throws JSONException
    {
        final List<ContentValues> list = new ArrayList<ContentValues>();
        final JSONArray jArray = json.getJSONArray(JTags.Account.LIST);
        JSONObject jObject;
        ContentValues values;
        for (int i = 0; i < jArray.length(); i++)
        {
            jObject = jArray.getJSONObject(i);
            values = new ContentValues();
            values.put(Tables.Accounts.ID, jObject.getLong(JTags.Account.ID));
            values.put(Tables.Accounts.SERVER_ID, jObject.getString(JTags.Account.SERVER_ID));
            values.put(Tables.Accounts.CURRENCY_ID, jObject.getLong(JTags.Account.CURRENCY_ID));
            values.put(Tables.Accounts.TITLE, jObject.getString(JTags.Account.TITLE));
            values.put(Tables.Accounts.NOTE, jObject.getString(JTags.Account.NOTE));
            values.put(Tables.Accounts.BALANCE, jObject.getDouble(JTags.Account.BALANCE));
            values.put(Tables.Accounts.SHOW_IN_TOTALS, jObject.getInt(JTags.Account.SHOW_IN_TOTALS));
            values.put(Tables.Accounts.SHOW_IN_SELECTION, jObject.getInt(JTags.Account.SHOW_IN_SELECTION));
            values.put(Tables.Accounts.ORIGIN, jObject.getInt(JTags.Account.ORIGIN));
            values.put(Tables.Accounts.TIMESTAMP, jObject.getLong(JTags.Account.TIMESTAMP));
            values.put(Tables.Accounts.SYNC_STATE, jObject.getInt(JTags.Account.SYNC_STATE));
            values.put(Tables.Accounts.DELETE_STATE, jObject.getInt(JTags.Account.DELETE_STATE));
            list.add(values);
        }
        parsedValues.putList(KEY_ACCOUNTS, list);
    }

    private static void parseCategories(ParsedValues parsedValues, JSONObject json, int exportVersion) throws JSONException
    {
        final List<ContentValues> list = new ArrayList<ContentValues>();
        final JSONArray jArray = json.getJSONArray(JTags.Category.LIST);
        JSONObject jObject;
        ContentValues values;
        for (int i = 0; i < jArray.length(); i++)
        {
            jObject = jArray.getJSONObject(i);
            values = new ContentValues();
            values.put(Tables.Categories.ID, jObject.getLong(JTags.Category.ID));
            values.put(Tables.Categories.SERVER_ID, jObject.getString(JTags.Category.SERVER_ID));
            values.put(Tables.Categories.PARENT_ID, jObject.getLong(JTags.Category.PARENT_ID));
            values.put(Tables.Categories.TITLE, jObject.getString(JTags.Category.TITLE));
            values.put(Tables.Categories.LEVEL, jObject.getInt(JTags.Category.LEVEL));
            values.put(Tables.Categories.TYPE, jObject.getInt(JTags.Category.TYPE));
            values.put(Tables.Categories.COLOR, jObject.getInt(JTags.Category.COLOR));
            values.put(Tables.Categories.ORIGIN, jObject.getInt(JTags.Category.ORIGIN));
            values.put(Tables.Categories.TIMESTAMP, jObject.getLong(JTags.Category.TIMESTAMP));
            values.put(Tables.Categories.SYNC_STATE, jObject.getInt(JTags.Category.SYNC_STATE));
            values.put(Tables.Categories.DELETE_STATE, jObject.getInt(JTags.Category.DELETE_STATE));
            if (exportVersion >= 4)
            {
                values.put(Tables.Categories.ORDER, jObject.getInt(JTags.Category.ORDER));
                values.put(Tables.Categories.PARENT_ORDER, jObject.getInt(JTags.Category.PARENT_ORDER));
            }
            list.add(values);
        }
        parsedValues.putList(KEY_CATEGORIES, list);
    }

    private static void parseTransactions(ParsedValues parsedValues, JSONObject json) throws JSONException
    {
        final List<ContentValues> list = new ArrayList<ContentValues>();
        final JSONArray jArray = json.getJSONArray(JTags.Transaction.LIST);
        JSONObject jObject;
        ContentValues values;
        for (int i = 0; i < jArray.length(); i++)
        {
            jObject = jArray.getJSONObject(i);
            values = new ContentValues();
            values.put(Tables.Transactions.ID, jObject.getLong(JTags.Transaction.ID));
            values.put(Tables.Transactions.SERVER_ID, jObject.getString(JTags.Transaction.SERVER_ID));
            values.put(Tables.Transactions.ACCOUNT_FROM_ID, jObject.getLong(JTags.Transaction.ACCOUNT_FROM_ID));
            values.put(Tables.Transactions.ACCOUNT_TO_ID, jObject.getLong(JTags.Transaction.ACCOUNT_TO_ID));
            values.put(Tables.Transactions.CATEGORY_ID, jObject.getLong(JTags.Transaction.CATEGORY_ID));
            values.put(Tables.Transactions.DATE, jObject.getLong(JTags.Transaction.DATE));
            values.put(Tables.Transactions.AMOUNT, jObject.getDouble(JTags.Transaction.AMOUNT));
            values.put(Tables.Transactions.NOTE, jObject.getString(JTags.Transaction.NOTE));
            values.put(Tables.Transactions.EXCHANGE_RATE, jObject.getDouble(JTags.Transaction.EXCHANGE_RATE));
            values.put(Tables.Transactions.STATE, jObject.getInt(JTags.Transaction.STATE));
            values.put(Tables.Transactions.SHOW_IN_TOTALS, jObject.getInt(JTags.Transaction.SHOW_IN_TOTALS));
            values.put(Tables.Transactions.TIMESTAMP, jObject.getLong(JTags.Transaction.TIMESTAMP));
            values.put(Tables.Transactions.SYNC_STATE, jObject.getInt(JTags.Transaction.SYNC_STATE));
            values.put(Tables.Transactions.DELETE_STATE, jObject.getInt(JTags.Transaction.DELETE_STATE));
            list.add(values);
        }
        parsedValues.putList(KEY_TRANSACTIONS, list);
    }

    @Override
    public void store(Context context, ParsedValues parsedValues)
    {
        final SQLiteDatabase db = DBHelper.getInstance(context).getWritableDatabase();
        ContentValues[] valuesArray;

        try
        {
            db.beginTransaction();

            // Clear tables
            db.delete(Tables.Currencies.TABLE_NAME, null, null);
            db.delete(Tables.Accounts.TABLE_NAME, null, null);
            db.delete(Tables.Categories.TABLE_NAME, null, null);
            db.delete(Tables.Transactions.TABLE_NAME, null, null);

            // Insert currency values
            valuesArray = parsedValues.getArray(KEY_CURRENCIES);
            for (int i = 0; i < valuesArray.length; i++)
                db.insert(Tables.Currencies.TABLE_NAME, null, valuesArray[i]);

            // Update default currency
            CurrenciesHelper.getDefault(context).update();

            // Insert account values
            valuesArray = parsedValues.getArray(KEY_ACCOUNTS);
            for (int i = 0; i < valuesArray.length; i++)
                db.insert(Tables.Accounts.TABLE_NAME, null, valuesArray[i]);

            // Insert category values
            valuesArray = parsedValues.getArray(KEY_CATEGORIES);
            for (int i = 0; i < valuesArray.length; i++)
                db.insert(Tables.Categories.TABLE_NAME, null, valuesArray[i]);

            // Generate order values for older backup versions
            if (valuesArray.length > 0 && !valuesArray[0].containsKey(Tables.Categories.ORDER))
                DBUpgrade.updateCategoriesOrder(db);

            // Insert transaction values
            valuesArray = parsedValues.getArray(KEY_TRANSACTIONS);
            for (int i = 0; i < valuesArray.length; i++)
                db.insert(Tables.Transactions.TABLE_NAME, null, valuesArray[i]);

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }

        NotifyUtils.notifyAll(context);
    }

    @Override
    protected void parse(Context context, ParsedValues parsedValues, Object info) throws Exception
    {
        JSONObject jObject = (JSONObject) info;

        final int exportVersion = jObject.getInt(JTags.Export.VERSION);
        if (exportVersion < 3)
            throw new Exception("Backup version " + jObject.getInt(JTags.Export.VERSION) + " is not supported. Latest version " + BackupUtils.BACKUP_VERSION + ".");

        parseCurrencies(parsedValues, jObject);
        parseAccounts(parsedValues, jObject);
        parseCategories(parsedValues, jObject, exportVersion);
        parseTransactions(parsedValues, jObject);
    }
}