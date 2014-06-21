package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.utils.IOUtils;

import java.util.Map;

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

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, long id, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, id, extras);

        final Cursor cursor = Query.get()
                .projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_FROM)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_TO)
                .projection(Tables.Categories.PROJECTION)
                .asCursor(getContext(), uriTransaction(id));
        final Transaction transaction = Transaction.from(cursor);
        IOUtils.closeQuietly(cursor);

        if (transaction.getCategory().getType() == Category.Type.EXPENSE) {
            updateAccountBalance(transaction.getAccountFrom().getId());
        } else if (transaction.getCategory().getType() == Category.Type.INCOME) {
            updateAccountBalance(transaction.getAccountTo().getId());
        } else {
            updateAccountBalance(transaction.getAccountFrom().getId());
            updateAccountBalance(transaction.getAccountTo().getId());
        }
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, itemState, extras);
        updateAccountsBalance(selection, selectionArgs);
    }

    @Override
    protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        super.onAfterBulkInsertItems(uri, valuesArray, extras);
        updateAccountsBalance(null);
    }

    private void updateAccountBalance(long accountId) {
        updateAccountsBalance(Tables.Transactions.ID.getNameWithTable() + "=?", String.valueOf(transactionId));
    }

    private void updateAccountsBalance(String selection, String... selectionArgs) {
    }
}
