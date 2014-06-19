package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.utils.IOUtils;

import java.util.Map;

public class AccountsProvider extends BaseModelProvider {
    private static final String EXTRA_BALANCE_DELTA = "balance_delta";

    public static Uri uriAccounts() {
        return uriModels(AccountsProvider.class, Tables.Accounts.TABLE_NAME);
    }

    public static Uri uriAccount(long accountId) {
        return uriModel(AccountsProvider.class, Tables.Accounts.TABLE_NAME, accountId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Accounts.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable() + " inner join " + Tables.Currencies.TABLE_NAME + " on " + Tables.Currencies.ID.getNameWithTable() + "=" + Tables.Accounts.CURRENCY_ID.getName();
    }

    @Override
    protected void onBeforeInsertItem(Uri uri, ContentValues values, Map<String, Object> outExtras) {
        super.onBeforeInsertItem(uri, values, outExtras);

        final long currentBalance;
        final long newBalance = values.getAsLong(Tables.Accounts.BALANCE.getName());
        if (values.containsKey(Tables.Accounts.ID.getName())) {
            currentBalance = getBalance(values.getAsLong(Tables.Accounts.ID.getName()));
        } else {
            currentBalance = 0;
        }
        outExtras.put(EXTRA_BALANCE_DELTA, newBalance - currentBalance);
        values.remove(Tables.Accounts.BALANCE.getName());
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, extras);

        long
    }

    private long getBalance(Long accountId) {
        final Cursor cursor = Query.get()
                .projection(Tables.Accounts.BALANCE.getName())
                .selection(Tables.Accounts.ID + "=?", String.valueOf(accountId))
                .asCursor(database, Tables.Accounts.TABLE_NAME);
        final long balance = cursor.getLong(0);
        IOUtils.closeQuietly(cursor);
        return balance;
    }

    private Transaction createBalanceTransaction(Account account, long balanceDelta) {
        long delta = newBalance - currentBalance;
        Transaction transaction = null;
        if (delta > 0) {
            transaction = new Transaction();
            transaction.setAccountTo();
        }
        return transaction;
    }
}
