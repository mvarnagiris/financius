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
import com.code44.finance.data.model.Account;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionsProvider extends BaseModelProvider {
    public static final String URI_PARAM_JOIN_TABLE = "join_table";
    public static final String URI_VALUE_JOIN_TABLE_ACCOUNTS_FROM = "accounts_from";
    public static final String URI_VALUE_JOIN_TABLE_ACCOUNTS_TO = "accounts_to";
    public static final String URI_VALUE_JOIN_TABLE_CATEGORIES = "categories";
    public static final String URI_VALUE_JOIN_TABLE_CURRENCIES_FROM = "currencies_from";
    public static final String URI_VALUE_JOIN_TABLE_CURRENCIES_TO = "currencies_to";

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
    protected String getQueryTables(Uri uri) {
        final List<String> joinTables = new ArrayList<>();
        if (uri.getQueryParameterNames().contains(URI_PARAM_JOIN_TABLE)) {
            // Join specific tables
            joinTables.addAll(uri.getQueryParameters(URI_PARAM_JOIN_TABLE));
        } else {
            // Join all the things!
            joinTables.add(URI_VALUE_JOIN_TABLE_ACCOUNTS_FROM);
            joinTables.add(URI_VALUE_JOIN_TABLE_ACCOUNTS_TO);
            joinTables.add(URI_VALUE_JOIN_TABLE_CATEGORIES);
            joinTables.add(URI_VALUE_JOIN_TABLE_CURRENCIES_FROM);
            joinTables.add(URI_VALUE_JOIN_TABLE_CURRENCIES_TO);
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(getModelTable());

        if (joinTables.contains(URI_VALUE_JOIN_TABLE_ACCOUNTS_FROM)) {
            sb.append(" inner join ").append(Tables.Accounts.TABLE_NAME).append(" as ").append(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT)
                    .append(" on ").append(Tables.Accounts.ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT)).append("=").append(Tables.Transactions.ACCOUNT_FROM_ID);
        }

        if (joinTables.contains(URI_VALUE_JOIN_TABLE_ACCOUNTS_TO)) {
            sb.append(" inner join ").append(Tables.Accounts.TABLE_NAME).append(" as ").append(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT)
                    .append(" on ").append(Tables.Accounts.ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT)).append("=").append(Tables.Transactions.ACCOUNT_TO_ID);
        }

        if (joinTables.contains(URI_VALUE_JOIN_TABLE_CATEGORIES)) {
            sb.append(" inner join ").append(Tables.Categories.TABLE_NAME)
                    .append(" on ").append(Tables.Categories.ID.getNameWithTable()).append("=").append(Tables.Transactions.CATEGORY_ID);
        }

        if (joinTables.contains(URI_VALUE_JOIN_TABLE_CURRENCIES_FROM)) {
            sb.append(" left join ").append(Tables.Currencies.TABLE_NAME).append(" as ").append(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY)
                    .append(" on ").append(Tables.Currencies.ID.getNameWithTable(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY)).append("=").append(Tables.Accounts.CURRENCY_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT));
        }

        if (joinTables.contains(URI_VALUE_JOIN_TABLE_CURRENCIES_TO)) {
            sb.append(" left join ").append(Tables.Currencies.TABLE_NAME).append(" as ").append(Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY)
                    .append(" on ").append(Tables.Currencies.ID.getNameWithTable(Tables.Currencies.TEMP_TABLE_NAME_TO_CURRENCY)).append("=").append(Tables.Accounts.CURRENCY_ID.getNameWithTable(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT));
        }

        return sb.toString();
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Transactions.ID;
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, serverId, extras);

        final String accountFromId = values.getAsString(Tables.Transactions.ACCOUNT_FROM_ID.getName());
        final String accountToId = values.getAsString(Tables.Transactions.ACCOUNT_TO_ID.getName());
        final String systemAccountId = Account.getSystem().getId();
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
                .args(accountId, CategoryType.TRANSFER.asString())
                .selection(Tables.Transactions.MODEL_STATE + "=?", ModelState.NORMAL.asString())
                .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.CONFIRMED.asString())
                .selection(" and (" + Tables.Transactions.ACCOUNT_FROM_ID + "=? or " + Tables.Transactions.ACCOUNT_TO_ID + "=?)", accountId, accountId)
                .from(getDatabase(), Tables.Transactions.TABLE_NAME)
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
                .withSelection(Tables.Accounts.ID + "=?", accountId)
                .into(getDatabase(), Tables.Accounts.TABLE_NAME);
    }

    private void updateAllAccountsBalances() {
        final Cursor cursor = Query.create()
                .projection(Tables.Accounts.ID.getName())
                .selection(Tables.Accounts.MODEL_STATE + "=?", String.valueOf(ModelState.NORMAL.asInt()))
                .from(getDatabase(), Tables.Accounts.TABLE_NAME)
                .execute();
        if (cursor.moveToFirst()) {
            final int iId = cursor.getColumnIndex(Tables.Accounts.ID.getName());
            do {
                updateAccountBalance(cursor.getString(iId));
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);
    }
}
