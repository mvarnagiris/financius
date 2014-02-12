package com.code44.finance.parsers;

import android.content.ContentValues;
import android.content.Context;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AccountsParser extends Parser
{
    public static final String KEY_ACCOUNTS = "KEY_ACCOUNTS";

    public void parseAccounts(List<ContentValues> valuesList, JSONArray json) throws Exception
    {
        ContentValues values;
        for (int i = 0; i < json.length(); i++)
        {
            values = new ContentValues();
            parseAccount(values, json.getJSONObject(i));
            valuesList.add(values);
        }
    }

    public void parseAccount(ContentValues values, JSONObject json) throws Exception
    {
        values.put(Tables.Accounts.SERVER_ID, json.getString(JTags.Account.SERVER_ID));
        values.put(Tables.Accounts.TITLE, json.getString(JTags.Account.TITLE));
        values.put(Tables.Accounts.ORIGIN, json.getInt(JTags.Account.ORIGIN));
        values.put(Tables.Accounts.DELETE_STATE, json.getInt(JTags.Account.DELETE_STATE));
    }

    @Override
    public void store(Context context, ParsedValues parsedValues)
    {
        // Accounts
        final ContentValues[] valuesArray = parsedValues.getArray(KEY_ACCOUNTS);
        if (valuesArray != null && valuesArray.length > 0)
        {
            //context.getContentResolver().bulkInsert(AccountsProvider.uriBulkAccounts(context), valuesArray);
        }

        // Notify
        context.getContentResolver().notifyChange(AccountsProvider.uriAccounts(), null);
    }

    @Override
    protected void parse(Context context, ParsedValues parsedValues, Object info) throws Exception
    {
        // Prepare
        final List<ContentValues> accountList = new ArrayList<ContentValues>();

        // Parse
        parseAccounts(accountList, (JSONArray) info);
        parsedValues.putList(KEY_ACCOUNTS, accountList);
    }
}