package com.code44.finance.api.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.Request;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.currencies.Currencies;
import com.code44.finance.backend.endpoint.tags.Tags;
import com.code44.finance.backend.endpoint.transactions.Transactions;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;

public class SyncRequest extends Request {
    private final Context context;
    private final DBHelper dbHelper;
    private final User user;
    private final GcmRegistration gcmRegistration;
    private final Currencies currenciesService;
    private final Categories categoriesService;
    private final Tags tagsService;
    private final Accounts accountsService;
    private final Transactions transactionsService;

    public SyncRequest(EventBus eventBus, Context context, DBHelper dbHelper, User user, GcmRegistration gcmRegistration, Currencies currenciesService, Categories categoriesService, Tags tagsService, Accounts accountsService, Transactions transactionsService) {
        super(eventBus);
        Preconditions.checkNotNull(eventBus, "EventBus cannot be null.");
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(dbHelper, "DBHelper cannot be null.");
        Preconditions.checkNotNull(user, "User cannot be null.");
        Preconditions.checkNotNull(gcmRegistration, "Gcm registration cannot be null.");
        Preconditions.checkNotNull(currenciesService, "Currencies service cannot be null.");
        Preconditions.checkNotNull(categoriesService, "Categories service cannot be null.");
        Preconditions.checkNotNull(tagsService, "Tags service cannot be null.");
        Preconditions.checkNotNull(accountsService, "Accounts service cannot be null.");
        Preconditions.checkNotNull(transactionsService, "Transactions service cannot be null.");

        this.context = context;
        this.dbHelper = dbHelper;
        this.user = user;
        this.gcmRegistration = gcmRegistration;
        this.currenciesService = currenciesService;
        this.categoriesService = categoriesService;
        this.tagsService = tagsService;
        this.accountsService = accountsService;
        this.transactionsService = transactionsService;
    }

    @Override
    protected void performRequest() throws Exception {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        pushCurrencies(database);
        getCurrencies();

        pushCategories(database);
        getCategories();

        pushTags(database);
        getTags();

        pushAccounts(database);
        getAccounts();

        pushTransactions(database);
        getTransactions();
    }

    private void pushCurrencies(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Currencies.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, CurrenciesProvider.uriCurrencies())
                .execute();
        final List<Currency> currencies = new ArrayList<>();
        do {
            currencies.add(Currency.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostCurrenciesRequest(gcmRegistration, currenciesService, currencies).run();
    }

    private void getCurrencies() throws Exception {
        new GetCurrenciesRequest(context, user, currenciesService).run();
    }

    private void pushCategories(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Categories.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Categories.LOCAL_ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, CategoriesProvider.uriCategories())
                .execute();
        final List<Category> categories = new ArrayList<>();
        do {
            categories.add(Category.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostCategoriesRequest(gcmRegistration, categoriesService, categories).run();
    }

    private void getCategories() throws Exception {
        new GetCategoriesRequest(context, user, categoriesService).run();
    }

    private void pushTags(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Tags.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Tags.LOCAL_ID)
                .projection(Tables.Tags.PROJECTION)
                .selection(Tables.Tags.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, TagsProvider.uriTags())
                .execute();
        final List<Tag> tags = new ArrayList<>();
        do {
            tags.add(Tag.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostTagsRequest(gcmRegistration, tagsService, tags).run();
    }

    private void getTags() throws Exception {
        new GetTagsRequest(context, user, tagsService).run();
    }

    private void pushAccounts(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Accounts.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Accounts.LOCAL_ID)
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

        new PostAccountsRequest(gcmRegistration, accountsService, accounts).run();
    }

    private void getAccounts() throws Exception {
        new GetAccountsRequest(context, user, accountsService).run();
    }

    private void pushTransactions(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Transactions.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Transactions.LOCAL_ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_FROM)
                .projection(Tables.Accounts.PROJECTION_ACCOUNT_TO)
                .projection(Tables.Currencies.PROJECTION_ACCOUNT_FROM)
                .projection(Tables.Currencies.PROJECTION_ACCOUNT_TO)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Transactions.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, TransactionsProvider.uriTransactions())
                .execute();
        final List<Transaction> transactions = new ArrayList<>();
        do {
            transactions.add(Transaction.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new PostTransactionsRequest(gcmRegistration, transactionsService, transactions).run();
    }

    private void getTransactions() throws Exception {
        new GetTransactionsRequest(context, user, transactionsService).run();
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
