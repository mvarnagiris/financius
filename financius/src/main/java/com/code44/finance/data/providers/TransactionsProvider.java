package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.common.model.CategoryType;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.utils.IOUtils;

import java.util.Map;

public class TransactionsProvider extends BaseModelProvider {
    public static Uri uriTransactions() {
        return uriModels(TransactionsProvider.class, Tables.Transactions.TABLE_NAME);
    }

    public static Uri uriTransaction(String transactionServerId) {
        return uriModel(TransactionsProvider.class, Tables.Transactions.TABLE_NAME, transactionServerId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Transactions.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable()
                + " inner join " + Tables.Accounts.TABLE_NAME + " as " + Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT
                + " on " + Tables.Accounts.SERVER_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT) + "=" + Tables.Transactions.ACCOUNT_FROM_ID
                + " inner join " + Tables.Accounts.TABLE_NAME + " as " + Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT
                + " on " + Tables.Accounts.SERVER_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT) + "=" + Tables.Transactions.ACCOUNT_TO_ID
                + " inner join " + Tables.Categories.TABLE_NAME + " on " + Tables.Categories.SERVER_ID.getNameWithTable() + "=" + Tables.Transactions.CATEGORY_ID
                + " left join " + Tables.Currencies.TABLE_NAME + " as " + Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY
                + " on " + Tables.Currencies.SERVER_ID.getNameWithTable(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY) + "=" + Tables.Accounts.CURRENCY_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT)
                + " left join " + Tables.Currencies.TABLE_NAME + " as " + Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY
                + " on " + Tables.Currencies.SERVER_ID.getNameWithTable(Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY) + "=" + Tables.Accounts.CURRENCY_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT);
    }

    @Override
    protected Column getServerIdColumn() {
        return Tables.Transactions.SERVER_ID;
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, serverId, extras);

        final String accountFromId = values.getAsString(Tables.Transactions.ACCOUNT_FROM_ID.getName());
        final String accountToId = values.getAsString(Tables.Transactions.ACCOUNT_TO_ID.getName());
        final String systemAccountId = Account.getSystem().getServerId();
        if (!accountFromId.equals(systemAccountId)) {
            updateAccountBalance(accountFromId);
        }

        if (!accountToId.equals(systemAccountId)) {
            updateAccountBalance(accountToId);
        }
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, modelState, extras);
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

    private void updateAccountBalance(String accountId) {
        final Cursor cursor = Query.create()
                .projection("sum( case" +
                        " when " + Tables.Transactions.ACCOUNT_FROM_ID + "=? then -" + Tables.Transactions.AMOUNT + "" +
                        " when " + Tables.Categories.TYPE + "=? then " + Tables.Transactions.AMOUNT + "*" + Tables.Transactions.EXCHANGE_RATE +
                        " else " + Tables.Transactions.AMOUNT + " end)")
                .args(accountId, String.valueOf(CategoryType.TRANSFER.asInt()))
                .selection(Tables.Transactions.MODEL_STATE + "=?", String.valueOf(ModelState.NORMAL.asInt()))
                .selection(" and " + Tables.Transactions.STATE + "=?", String.valueOf(TransactionState.CONFIRMED.asInt()))
                .selection(" and (" + Tables.Transactions.ACCOUNT_FROM_ID + "=? or " + Tables.Transactions.ACCOUNT_TO_ID + "=?)", accountId, accountId)
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
                .projection(Tables.Accounts.SERVER_ID.getName())
                .selection(Tables.Accounts.MODEL_STATE + "=?", String.valueOf(ModelState.NORMAL.asInt()))
                .from(database, Tables.Accounts.TABLE_NAME)
                .execute();
        if (cursor.moveToFirst()) {
            final int iId = cursor.getColumnIndex(Tables.Accounts.SERVER_ID.getName());
            do {
                updateAccountBalance(cursor.getString(iId));
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);
    }
}
