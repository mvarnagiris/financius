package com.code44.finance.api.parsers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.App;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.Tables;
import com.code44.finance.parsers.JTags;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.BaseProvider;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.utils.BackupUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit.client.Response;

public class BackupParser implements BaseParser<JsonObject>
{
    @Override
    public void parse(JsonObject json, Response rawResponse) throws RuntimeException
    {
        final int exportVersion = json.get(JTags.Export.VERSION).getAsInt();
        if (exportVersion < 3)
            throw new RuntimeException("Backup version " + exportVersion + " is not supported. Latest version " + BackupUtils.BACKUP_VERSION + ".");

        // Parse
        ContentValues[] currenciesValues = parseCurrencies(json);
        ContentValues[] accountsValues = parseAccounts(json);
        ContentValues[] categoriesValues = parseCategories(json);
        ContentValues[] transactionsValues = parseTransactions(json);

        // Store
        final SQLiteDatabase db = DBHelper.get(App.getAppContext()).getWritableDatabase();
        try
        {
            //noinspection ConstantConditions
            db.beginTransaction();

            // Clear tables
            db.delete(Tables.Currencies.TABLE_NAME, null, null);
            db.delete(Tables.Accounts.TABLE_NAME, null, null);
            db.delete(Tables.Categories.TABLE_NAME, null, null);
            db.delete(Tables.Transactions.TABLE_NAME, null, null);

            // Insert values
            BaseProvider.doBulkInsert(db, Tables.Currencies.TABLE_NAME, currenciesValues);
            BaseProvider.doBulkInsert(db, Tables.Accounts.TABLE_NAME, accountsValues);
            BaseProvider.doBulkInsert(db, Tables.Categories.TABLE_NAME, categoriesValues);
            BaseProvider.doBulkInsert(db, Tables.Transactions.TABLE_NAME, transactionsValues);

            // Notify uris
            BaseProvider.notifyURIs(
                    CurrenciesProvider.uriCurrencies(),
                    AccountsProvider.uriAccounts(),
                    CategoriesProvider.uriCategories(),
                    TransactionsProvider.uriTransactions());

            db.setTransactionSuccessful();
        }
        finally
        {
            //noinspection ConstantConditions
            db.endTransaction();
        }
    }

    private ContentValues[] parseCurrencies(JsonObject json)
    {
        JsonArray jsonArray = json.getAsJsonArray(JTags.Currency.LIST);
        ContentValues[] valuesArray = new ContentValues[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++)
        {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            ContentValues values = new ContentValues();
            values.put(Tables.Currencies.ID, jsonObject.get(JTags.Currency.ID).getAsLong());
            values.put(Tables.Currencies.SERVER_ID, jsonObject.get(JTags.Currency.SERVER_ID).getAsString());
            values.put(Tables.Currencies.CODE, jsonObject.get(JTags.Currency.CODE).getAsString());
            values.put(Tables.Currencies.SYMBOL, jsonObject.get(JTags.Currency.SYMBOL).getAsString());
            values.put(Tables.Currencies.DECIMALS, jsonObject.get(JTags.Currency.DECIMALS).getAsInt());
            values.put(Tables.Currencies.DECIMAL_SEPARATOR, jsonObject.get(JTags.Currency.DECIMAL_SEPARATOR).getAsString());
            values.put(Tables.Currencies.GROUP_SEPARATOR, jsonObject.get(JTags.Currency.GROUP_SEPARATOR).getAsString());
            values.put(Tables.Currencies.SYMBOL_FORMAT, jsonObject.get(JTags.Currency.SYMBOL_FORMAT).getAsString());
            values.put(Tables.Currencies.IS_DEFAULT, jsonObject.get(JTags.Currency.IS_DEFAULT).getAsInt());
            values.put(Tables.Currencies.EXCHANGE_RATE, jsonObject.get(JTags.Currency.EXCHANGE_RATE).getAsDouble());
            values.put(Tables.Currencies.DELETE_STATE, jsonObject.get(JTags.Currency.DELETE_STATE).getAsInt());
            valuesArray[i] = values;
        }

        return valuesArray;
    }

    private ContentValues[] parseAccounts(JsonObject json)
    {
        JsonArray jsonArray = json.getAsJsonArray(JTags.Account.LIST);
        ContentValues[] valuesArray = new ContentValues[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++)
        {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            ContentValues values = new ContentValues();
            values.put(Tables.Accounts.ID, jsonObject.get(JTags.Account.ID).getAsLong());
            values.put(Tables.Accounts.SERVER_ID, jsonObject.get(JTags.Account.SERVER_ID).getAsString());
            values.put(Tables.Accounts.CURRENCY_ID, jsonObject.get(JTags.Account.CURRENCY_ID).getAsLong());
            values.put(Tables.Accounts.TITLE, jsonObject.get(JTags.Account.TITLE).getAsString());
            values.put(Tables.Accounts.NOTE, jsonObject.get(JTags.Account.NOTE).getAsString());
            values.put(Tables.Accounts.BALANCE, jsonObject.get(JTags.Account.BALANCE).getAsDouble());
            values.put(Tables.Accounts.SHOW_IN_TOTALS, jsonObject.get(JTags.Account.SHOW_IN_TOTALS).getAsInt());
            values.put(Tables.Accounts.SHOW_IN_SELECTION, jsonObject.get(JTags.Account.SHOW_IN_SELECTION).getAsInt());
            values.put(Tables.Accounts.ORIGIN, jsonObject.get(JTags.Account.ORIGIN).getAsInt());
            values.put(Tables.Accounts.DELETE_STATE, jsonObject.get(JTags.Account.DELETE_STATE).getAsInt());
            valuesArray[i] = values;
        }

        return valuesArray;
    }

    private ContentValues[] parseCategories(JsonObject json)
    {
        JsonArray jsonArray = json.getAsJsonArray(JTags.Category.LIST);
        ContentValues[] valuesArray = new ContentValues[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++)
        {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            ContentValues values = new ContentValues();
            values.put(Tables.Categories.ID, jsonObject.get(JTags.Category.ID).getAsLong());
            values.put(Tables.Categories.SERVER_ID, jsonObject.get(JTags.Category.SERVER_ID).getAsString());
            values.put(Tables.Categories.PARENT_ID, jsonObject.get(JTags.Category.PARENT_ID).getAsLong());
            values.put(Tables.Categories.TITLE, jsonObject.get(JTags.Category.TITLE).getAsString());
            values.put(Tables.Categories.LEVEL, jsonObject.get(JTags.Category.LEVEL).getAsInt());
            values.put(Tables.Categories.TYPE, jsonObject.get(JTags.Category.TYPE).getAsInt());
            values.put(Tables.Categories.COLOR, jsonObject.get(JTags.Category.COLOR).getAsInt());
            values.put(Tables.Categories.ORIGIN, jsonObject.get(JTags.Category.ORIGIN).getAsInt());
            values.put(Tables.Categories.DELETE_STATE, jsonObject.get(JTags.Category.DELETE_STATE).getAsInt());
            if (json.get(JTags.Export.VERSION).getAsInt() >= 4)
            {
                values.put(Tables.Categories.ORDER, jsonObject.get(JTags.Category.ORDER).getAsInt());
                values.put(Tables.Categories.PARENT_ORDER, jsonObject.get(JTags.Category.PARENT_ORDER).getAsInt());
            }
            valuesArray[i] = values;
        }

        return valuesArray;
    }

    private ContentValues[] parseTransactions(JsonObject json)
    {
        JsonArray jsonArray = json.getAsJsonArray(JTags.Transaction.LIST);
        ContentValues[] valuesArray = new ContentValues[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++)
        {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            ContentValues values = new ContentValues();
            values.put(Tables.Transactions.ID, jsonObject.get(JTags.Transaction.ID).getAsLong());
            values.put(Tables.Transactions.SERVER_ID, jsonObject.get(JTags.Transaction.SERVER_ID).getAsString());
            values.put(Tables.Transactions.ACCOUNT_FROM_ID, jsonObject.get(JTags.Transaction.ACCOUNT_FROM_ID).getAsLong());
            values.put(Tables.Transactions.ACCOUNT_TO_ID, jsonObject.get(JTags.Transaction.ACCOUNT_TO_ID).getAsLong());
            values.put(Tables.Transactions.CATEGORY_ID, jsonObject.get(JTags.Transaction.CATEGORY_ID).getAsLong());
            values.put(Tables.Transactions.DATE, jsonObject.get(JTags.Transaction.DATE).getAsLong());
            values.put(Tables.Transactions.AMOUNT, jsonObject.get(JTags.Transaction.AMOUNT).getAsDouble());
            values.put(Tables.Transactions.NOTE, jsonObject.get(JTags.Transaction.NOTE).getAsString());
            values.put(Tables.Transactions.EXCHANGE_RATE, jsonObject.get(JTags.Transaction.EXCHANGE_RATE).getAsDouble());
            values.put(Tables.Transactions.STATE, jsonObject.get(JTags.Transaction.STATE).getAsInt());
            values.put(Tables.Transactions.SHOW_IN_TOTALS, jsonObject.get(JTags.Transaction.SHOW_IN_TOTALS).getAsInt());
            values.put(Tables.Transactions.DELETE_STATE, jsonObject.get(JTags.Transaction.DELETE_STATE).getAsInt());
            valuesArray[i] = values;
        }

        return valuesArray;
    }
}
