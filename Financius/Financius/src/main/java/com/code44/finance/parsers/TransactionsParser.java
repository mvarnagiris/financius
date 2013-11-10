package com.code44.finance.parsers;

import android.content.ContentValues;
import android.content.Context;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.providers.TransactionsProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionsParser extends Parser
{
    public static final String KEY_TRANSACTIONS = "KEY_TRANSACTIONS";
    private static final String TEMP_ACCOUNT_FROM_SERVER_ID = "TEMP_ACCOUNT_FROM_SERVER_ID";
    private static final String TEMP_ACCOUNT_TO_SERVER_ID = "TEMP_ACCOUNT_TO_SERVER_ID";
    private static final String TEMP_CATEGORY_SERVER_ID = "TEMP_CATEGORY_SERVER_ID";

    @Override
    public void store(Context context, ParsedValues parsedValues)
    {
        // Transactions
        final ContentValues[] valuesArray = parsedValues.getArray(KEY_TRANSACTIONS);
        if (valuesArray != null && valuesArray.length > 0)
        {
            final Map<String, Long> accountsMap = getStringIDsMap(context, AccountsProvider.uriAccounts(context), null, null, Tables.Accounts.SERVER_ID,
                    Tables.Accounts.T_ID);
            replaceStringWithLong(valuesArray, accountsMap, TEMP_ACCOUNT_FROM_SERVER_ID, Tables.Transactions.ACCOUNT_FROM_ID);
            replaceStringWithLong(valuesArray, accountsMap, TEMP_ACCOUNT_TO_SERVER_ID, Tables.Transactions.ACCOUNT_TO_ID);
            replaceStringWithLong(valuesArray,
                    getStringIDsMap(context, CategoriesProvider.uriCategories(context), null, null, Tables.Categories.SERVER_ID, Tables.Categories.T_ID),
                    TEMP_CATEGORY_SERVER_ID, Tables.Transactions.CATEGORY_ID);
           // context.getContentResolver().bulkInsert(TransactionsProvider.uriBulkTransactions(context), valuesArray);
            context.getContentResolver().notifyChange(TransactionsProvider.uriTransactions(context), null);
        }
    }

    public void parseTransactions(List<ContentValues> valuesList, JSONArray json) throws Exception
    {
        ContentValues values;
        for (int i = 0; i < json.length(); i++)
        {
            values = new ContentValues();
            parseTransaction(values, json.getJSONObject(i));
            valuesList.add(values);
        }
    }

    public void parseTransaction(ContentValues values, JSONObject json) throws Exception
    {
//        values.put(Tables.Transactions.SERVER_ID, json.getString(JTags.Transaction.SERVER_ID));
//        values.put(TEMP_ACCOUNT_FROM_SERVER_ID, json.getString(JTags.Transaction.ACCOUNT_FROM_SERVER_ID));
//        values.put(TEMP_ACCOUNT_TO_SERVER_ID, json.getString(JTags.Transaction.ACCOUNT_TO_SERVER_ID));
//        values.put(TEMP_CATEGORY_SERVER_ID, json.getString(JTags.Transaction.CATEGORY_SERVER_ID));
//        values.put(Tables.Transactions.DATE, json.getLong(JTags.Transaction.DATE));
//        values.put(Tables.Transactions.AMOUNT, json.getDouble(JTags.Transaction.AMOUNT));
//        values.put(Tables.Transactions.NOTE, json.getString(JTags.Transaction.NOTE));
//        values.put(Tables.Transactions.TIMESTAMP, json.getLong(JTags.Transaction.TIMESTAMP));
//        values.put(Tables.Transactions.DELETE_STATE, json.getInt(JTags.Transaction.DELETE_STATE));
    }

    @Override
    protected void parse(Context context, ParsedValues parsedValues, Object info) throws Exception
    {
        // Prepare
        final List<ContentValues> transactionList = new ArrayList<ContentValues>();

        // Parse
        parseTransactions(transactionList, (JSONArray) info);
        parsedValues.putList(KEY_TRANSACTIONS, transactionList);
    }
}