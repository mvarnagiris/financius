package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
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
                + " on " + Tables.Accounts.ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT) + "=" + Tables.Transactions.ACCOUNT_FROM_ID
                + " inner join " + Tables.Accounts.TABLE_NAME + " as " + Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT
                + " on " + Tables.Accounts.ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT) + "=" + Tables.Transactions.ACCOUNT_TO_ID
                + " inner join " + Tables.Categories.TABLE_NAME + " on " + Tables.Categories.ID.getNameWithTable() + "=" + Tables.Transactions.CATEGORY_ID
                + " left join " + Tables.Currencies.TABLE_NAME + " as " + Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY
                + " on " + Tables.Currencies.ID.getNameWithTable(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY) + "=" + Tables.Accounts.CURRENCY_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT)
                + " left join " + Tables.Currencies.TABLE_NAME + " as " + Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY
                + " on " + Tables.Currencies.ID.getNameWithTable(Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY) + "=" + Tables.Accounts.CURRENCY_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT);
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, long id, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, id, extras);

        //noinspection ConstantConditions
        final long accountFromId = values.getAsLong(Tables.Transactions.ACCOUNT_FROM_ID.getName());
        //noinspection ConstantConditions
        final long accountToId = values.getAsLong(Tables.Transactions.ACCOUNT_TO_ID.getName());
        final long systemAccountId = Account.getSystem().getId();
        if (accountFromId != systemAccountId) {
            updateAccountBalance(accountFromId);
        }

        if (accountToId != systemAccountId) {
            updateAccountBalance(accountToId);
        }
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, itemState, extras);
        updateAllAccountsBalances();
    }

    @Override
    protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        super.onAfterBulkInsertItems(uri, valuesArray, extras);
        updateAllAccountsBalances();
    }

    @Override
    protected Uri[] getOtherUrisToNotify() {
        return new Uri[]{AccountsProvider.uriAccounts()};
    }

    private void updateAccountBalance(long accountId) {
        final Cursor cursor = Query.create()
                .projection("sum( case" +
                        " when " + Tables.Transactions.ACCOUNT_FROM_ID + "=? then -" + Tables.Transactions.AMOUNT + "" +
                        " when " + Tables.Categories.TYPE + "=? then " + Tables.Transactions.AMOUNT + "*" + Tables.Transactions.EXCHANGE_RATE +
                        " else " + Tables.Transactions.AMOUNT + " end)")
                .args(String.valueOf(accountId), String.valueOf(Category.Type.TRANSFER.asInt()))
                .selection(Tables.Transactions.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.NORMAL.asInt()))
                .selection(" and " + Tables.Transactions.STATE + "=?", String.valueOf(Transaction.State.CONFIRMED.asInt()))
                .selection(" and (" + Tables.Transactions.ACCOUNT_FROM_ID + "=? or " + Tables.Transactions.ACCOUNT_TO_ID + "=?)", String.valueOf(accountId), String.valueOf(accountId))
                .from(database, Tables.Transactions.TABLE_NAME)
                .innerJoin(Tables.Categories.TABLE_NAME, Tables.Categories.ID.getNameWithTable() + "=" + Tables.Transactions.CATEGORY_ID)
                .execute();

        long balance = 0;
        if (cursor.moveToFirst()) {
            balance = cursor.getLong(0);
        }
        IOUtils.closeQuietly(cursor);

        final ContentValues values = new ContentValues();
        values.put(Tables.Accounts.BALANCE.getName(), balance);
        DataStore.update()
                .values(values)
                .withSelection(Tables.Accounts.ID + "=?", String.valueOf(accountId))
                .into(database, Tables.Accounts.TABLE_NAME);
    }

    private void updateAllAccountsBalances() {
        final Cursor cursor = Query.create()
                .projectionId(Tables.Accounts.ID)
                .selection(Tables.Accounts.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.NORMAL.asInt()))
                .from(database, Tables.Accounts.TABLE_NAME)
                .execute();
        if (cursor.moveToFirst()) {
            final int iId = cursor.getColumnIndex(Tables.Accounts.ID.getName());
            do {
                updateAccountBalance(cursor.getLong(iId));
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);
    }
}
