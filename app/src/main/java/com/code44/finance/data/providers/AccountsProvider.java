package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.R;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.IOUtils;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

public class AccountsProvider extends ModelProvider {
    private static final String EXTRA_BALANCE_DELTA = "balance_delta";

    public static Uri uriAccounts() {
        return uriModels(AccountsProvider.class, Tables.Accounts.TABLE_NAME);
    }

    public static Uri uriAccount(String accountServerId) {
        return uriModel(AccountsProvider.class, Tables.Accounts.TABLE_NAME, accountServerId);
    }

    @Override protected String getModelTable() {
        return Tables.Accounts.TABLE_NAME;
    }

    @Override protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override protected Column getIdColumn() {
        return Tables.Accounts.ID;
    }

    @Override protected void onBeforeInsertItem(Uri uri, ContentValues values, Map<String, Object> outExtras) {
        super.onBeforeInsertItem(uri, values, outExtras);

        final long currentBalance = getCurrentBalance(values);
        //noinspection ConstantConditions
        final long newBalance = values.getAsLong(Tables.Accounts.BALANCE.getName());
        outExtras.put(EXTRA_BALANCE_DELTA, newBalance - currentBalance);
        values.remove(Tables.Accounts.BALANCE.getName());
    }

    @Override protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, extras);

        final Account account = new Account();
        account.setId(values.getAsString(getIdColumn().getName()));

        long balanceDelta = (long) extras.get(EXTRA_BALANCE_DELTA);
        final Transaction transaction = createBalanceTransaction(account, balanceDelta);
        if (transaction != null) {
            DataStore.insert().model(transaction).into(getContext(), TransactionsProvider.uriTransactions());
        }
    }

    @Override protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, extras);

        final List<String> affectedIds = getColumnValues(extras);
        final ModelState modelState = getModelState(extras);
        if (affectedIds.size() > 0) {
            final Uri transactionsUri = uriForDeleteFromModelState(TransactionsProvider.uriTransactions(), modelState);

            Query query = Query.create()
                    .selection(Tables.Transactions.MODEL_STATE + "<>? and ", ModelState.Deleted.asString())
                    .selectionInClause(Tables.Transactions.ACCOUNT_FROM_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());

            query = Query.create()
                    .selection(Tables.Transactions.MODEL_STATE + "<>? and ", ModelState.Deleted.asString())
                    .selectionInClause(Tables.Transactions.ACCOUNT_TO_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
        }
    }

    @Override protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, outExtras);
        putColumnToExtras(outExtras, getIdColumn(), selection, selectionArgs);
    }

    private long getCurrentBalance(ContentValues values) {
        final String accountId = values.getAsString(Tables.Accounts.ID.getName());
        if (Strings.isNullOrEmpty(accountId)) {
            return 0;
        }

        final Cursor cursor = Query.create()
                .projection(Tables.Accounts.BALANCE.getName())
                .selection(Tables.Accounts.ID + "=?", String.valueOf(accountId))
                .from(getDatabase(), Tables.Accounts.TABLE_NAME)
                .execute();
        final long balance = cursor.moveToFirst() ? cursor.getLong(cursor.getColumnIndex(Tables.Accounts.BALANCE.getName())) : 0;
        IOUtils.closeQuietly(cursor);
        return balance;
    }

    private Transaction createBalanceTransaction(Account account, long balanceDelta) {
        Transaction transaction = null;

        if (balanceDelta > 0) {
            transaction = new Transaction();
            transaction.setAccountTo(account);
            transaction.setTransactionType(TransactionType.Income);
        } else if (balanceDelta < 0) {
            transaction = new Transaction();
            transaction.setAccountFrom(account);
            transaction.setTransactionType(TransactionType.Expense);
        }

        if (transaction != null) {
            transaction.setAmount(Math.abs(balanceDelta));
            transaction.setNote(getContext().getString(R.string.account_balance_update));
            transaction.setIncludeInReports(false);
            transaction.setTransactionState(TransactionState.Confirmed);
        }

        return transaction;
    }
}
