package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.R;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.qualifiers.Expense;
import com.code44.finance.qualifiers.Income;
import com.code44.finance.utils.IOUtils;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class AccountsProvider extends BaseModelProvider {
    private static final String EXTRA_BALANCE_DELTA = "balance_delta";

    @Inject @Expense Category expenseCategory;
    @Inject @Income Category incomeCategory;

    public static Uri uriAccounts() {
        return uriModels(AccountsProvider.class, Tables.Accounts.TABLE_NAME);
    }

    public static Uri uriAccount(String accountServerId) {
        return uriModel(AccountsProvider.class, Tables.Accounts.TABLE_NAME, accountServerId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Accounts.TABLE_NAME;
    }

    @Override
    protected String getQueryTables(Uri uri) {
        return getModelTable() + " inner join " + Tables.Currencies.TABLE_NAME + " on " + Tables.Currencies.ID + "=" + Tables.Accounts.CURRENCY_ID;
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Accounts.ID;
    }

    @Override
    protected void onBeforeInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> outExtras) {
        super.onBeforeInsertItem(uri, values, serverId, outExtras);

        final long currentBalance = getCurrentBalance(values);
        //noinspection ConstantConditions
        final long newBalance = values.getAsLong(Tables.Accounts.BALANCE.getName());
        outExtras.put(EXTRA_BALANCE_DELTA, newBalance - currentBalance);
        values.remove(Tables.Accounts.BALANCE.getName());
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, serverId, extras);

        final Account account = new Account();
        account.setId(serverId);

        long balanceDelta = (long) extras.get(EXTRA_BALANCE_DELTA);
        final Transaction transaction = createBalanceTransaction(account, balanceDelta);
        if (transaction != null) {
            DataStore.insert().model(transaction).into(getContext(), TransactionsProvider.uriTransactions());
        }
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeUpdateItems(uri, values, selection, selectionArgs, outExtras);
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, modelState, outExtras);

        final List<String> affectedIds = getIdList(getIdColumn(), selection, selectionArgs);
        outExtras.put("affectedIds", affectedIds);
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, modelState, extras);

        //noinspection unchecked
        final List<String> affectedIds = (List<String>) extras.get("affectedIds");
        if (affectedIds.size() > 0) {
            final Uri transactionsUri = uriForDeleteFromItemState(TransactionsProvider.uriTransactions(), modelState);

            Query query = Query.create().selectionInClause(Tables.Transactions.ACCOUNT_FROM_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());

            query = Query.create().selectionInClause(Tables.Transactions.ACCOUNT_TO_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
        }
    }

    private long getCurrentBalance(ContentValues values) {
        final Long accountId = values.getAsLong(Tables.Accounts.ID.getName());
        if (accountId == null || accountId == 0) {
            return 0;
        }

        final Cursor cursor = Query.create()
                .projection(Tables.Accounts.BALANCE.getName())
                .selection(Tables.Accounts.ID + "=?", String.valueOf(accountId))
                .from(getDatabase(), Tables.Accounts.TABLE_NAME)
                .execute();
        final long balance = cursor.getLong(cursor.getColumnIndex(Tables.Accounts.BALANCE.getName()));
        IOUtils.closeQuietly(cursor);
        return balance;
    }

    private Transaction createBalanceTransaction(Account account, long balanceDelta) {
        Transaction transaction = null;

        if (balanceDelta > 0) {
            transaction = new Transaction();
            transaction.setAccountTo(account);
            transaction.setCategory(incomeCategory);
        } else if (balanceDelta < 0) {
            transaction = new Transaction();
            transaction.setAccountFrom(account);
            transaction.setCategory(expenseCategory);
        }

        if (transaction != null) {
            transaction.setAmount(Math.abs(balanceDelta));
            transaction.setDate(System.currentTimeMillis());
            transaction.setNote(getContext().getString(R.string.account_balance_update));
            transaction.setIncludeInReports(false);
        }

        return transaction;
    }
}
