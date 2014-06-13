package com.code44.finance.data.providers;

import android.net.Uri;
import android.provider.BaseColumns;

import com.code44.finance.data.db.Tables;

public class TransactionsProvider extends BaseModelProvider {
    public static Uri uriTransactions() {
        return uriModels(TransactionsProvider.class, Tables.Transactions.TABLE_NAME);
    }

    public static Uri uriTransaction(long transactionId) {
        return uriModel(TransactionsProvider.class, Tables.Transactions.TABLE_NAME, transactionId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Transactions.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable()
                + " inner join " + Tables.Accounts.TABLE_NAME + " as " + Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT
                + " on " + Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT + "." + BaseColumns._ID + "=" + Tables.Transactions.ACCOUNT_FROM_ID
                + " inner join " + Tables.Accounts.TABLE_NAME + " as " + Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT
                + " on " + Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT + "." + BaseColumns._ID + "=" + Tables.Transactions.ACCOUNT_TO_ID
                + " inner join " + Tables.Categories.TABLE_NAME + " on " + Tables.Categories.ID.getNameWithTable() + "=" + Tables.Transactions.CATEGORY_ID;
    }
}
