package com.code44.finance.api.requests;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.api.Request;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.db.model.SyncState;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SyncRequest extends Request {
    @Inject DBHelper dbHelper;

    @Override
    protected void performRequest() throws Exception {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        pushCurrencies(database);
        getCurrencies();

        pushCategories(database);
        getCategories();

        pushAccounts(database);
        getAccounts();

        pushTransactions(database);
        getTransactions();
    }

    private void pushCurrencies(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Currencies.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(CurrenciesProvider.uriCurrencies())
                .execute();
        final List<Currency> currencies = new ArrayList<>();
        do {
            currencies.add(Currency.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostCurrenciesRequest(currencies).run();
    }

    private void getCurrencies() throws Exception {
        new GetCurrenciesRequest().run();
    }

    private void pushCategories(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Categories.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Categories.ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(CategoriesProvider.uriCategories())
                .execute();
        final List<Category> categories = new ArrayList<>();
        do {
            categories.add(Category.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostCategoriesRequest(categories).run();
    }

    private void getCategories() throws Exception {
        new GetCategoriesRequest().run();
    }

    private void pushAccounts(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Accounts.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Accounts.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(AccountsProvider.uriAccounts())
                .execute();
        final List<Account> accounts = new ArrayList<>();
        do {
            accounts.add(Account.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostAccountsRequest(accounts).run();
    }

    private void getAccounts() throws Exception {
        new GetAccountsRequest().run();
    }

    private void pushTransactions(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Transactions.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_FROM)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_TO)
                .projection(Tables.Currencies.PROJECTION_ACCOUNT_FROM)
                .projection(Tables.Currencies.PROJECTION_ACCOUNT_TO)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Transactions.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(TransactionsProvider.uriTransactions())
                .execute();
        final List<Transaction> transactions = new ArrayList<>();
        do {
            transactions.add(Transaction.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostTransactionsRequest(transactions).run();
    }

    private void getTransactions() throws Exception {
        new GetTransactionsRequest().run();
    }

    private void markInProgress(SQLiteDatabase database, Column syncStateColumn) {
        final ContentValues values = new ContentValues();
        values.put(syncStateColumn.getName(), SyncState.IN_PROGRESS.asInt());
        DataStore.update()
                .values(values)
                .withSelection(syncStateColumn.getName() + "<>?", SyncState.SYNCED.asString())
                .into(database, syncStateColumn.getTableName());
    }
}
