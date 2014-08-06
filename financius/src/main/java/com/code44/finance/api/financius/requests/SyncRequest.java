package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.api.User;
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
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;

public class SyncRequest extends FinanciusBaseRequest<Void> {
    public SyncRequest(Context context, User user) {
        super(null, context, user);
    }

    @Override
    protected Void performRequest() throws Exception {
        final SQLiteDatabase database = DBHelper.get(context).getWritableDatabase();

        pushCurrencies(database);
        getCurrencies();

        pushCategories(database);
        getCategories();

        pushAccounts(database);
        getAccounts();

        pushTransactions(database);
        getTransactions();

        return null;
    }

    @Override
    protected BaseRequestEvent<Void, ? extends BaseRequest<Void>> createEvent(Void result, Exception error, BaseRequestEvent.State state) {
        return new SyncRequestEvent(this, result, error, state);
    }

    private void pushCurrencies(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Currencies.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, CurrenciesProvider.uriCurrencies())
                .execute();
        final List<Currency> currencies = new ArrayList<>();
        do {
            currencies.add(Currency.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new SaveCurrenciesRequest(context, User.get(), currencies).call();
    }

    private void getCurrencies() throws Exception {
        new GetCurrenciesRequest(context, user).call();
    }

    private void pushCategories(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Categories.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Categories.ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, CategoriesProvider.uriCategories())
                .execute();
        final List<Category> categories = new ArrayList<>();
        do {
            categories.add(Category.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new SaveCateoriesRequest(context, User.get(), categories).call();
    }

    private void getCategories() throws Exception {
        new GetCategoriesRequest(context, user).call();
    }

    private void pushAccounts(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Accounts.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Accounts.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, AccountsProvider.uriAccounts())
                .execute();
        final List<Account> accounts = new ArrayList<>();
        do {
            accounts.add(Account.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new SaveAccountsRequest(context, User.get(), accounts).call();
    }

    private void getAccounts() throws Exception {
        new GetAccountsRequest(context, user).call();
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
                .from(context, AccountsProvider.uriAccounts())
                .execute();
        final List<Transaction> transactions = new ArrayList<>();
        do {
            transactions.add(Transaction.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new SaveTransactionsRequest(context, User.get(), transactions).call();
    }

    private void getTransactions() throws Exception {
        new GetTransactionsRequest(context, user).call();
    }

    private void markInProgress(SQLiteDatabase database, Column syncStateColumn) {
        final ContentValues values = new ContentValues();
        values.put(syncStateColumn.getName(), SyncState.IN_PROGRESS.asInt());
        DataStore.update()
                .values(values)
                .withSelection(syncStateColumn.getName() + "<>?", SyncState.SYNCED.asString())
                .into(database, syncStateColumn.getTableName());
    }

    public static class SyncRequestEvent extends BaseRequestEvent<Void, SyncRequest> {
        protected SyncRequestEvent(SyncRequest request, Void result, Exception error, State state) {
            super(request, result, error, state);
        }
    }
}
