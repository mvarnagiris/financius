package com.code44.finance.data.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.utils.IOUtils;

import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;

import static org.junit.Assert.*;

public class BaseContentProviderTestCase {
    protected Context context;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.getShadowApplication().getApplicationContext();

        final SQLiteDatabase database = new DBHelper(context, null).getWritableDatabase();

        final CurrenciesProvider currenciesProvider = new CurrenciesProvider();
        currenciesProvider.onCreate();
        currenciesProvider.database = database;

        final AccountsProvider accountsProvider = new AccountsProvider();
        accountsProvider.onCreate();
        accountsProvider.database = database;

        final CategoriesProvider categoriesProvider = new CategoriesProvider();
        categoriesProvider.onCreate();
        categoriesProvider.database = database;

        final TransactionsProvider transactionsProvider = new TransactionsProvider();
        transactionsProvider.onCreate();
        transactionsProvider.database = database;

        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(CurrenciesProvider.class), currenciesProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(AccountsProvider.class), accountsProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(CategoriesProvider.class), categoriesProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(TransactionsProvider.class), transactionsProvider);
    }

    protected long insert(Uri uri, BaseModel model) {
        return ContentUris.parseId(DataStore.insert().model(model).into(uri));
    }

    protected int update(Uri uri, ContentValues values, String selection, String... selectionArgs) {
        return DataStore.update().withSelection(selection, selectionArgs).values(values).into(uri);
    }

    protected int delete(String mode, Uri uri, String selection, String... selectionArgs) {
        return DataStore.delete().selection(selection, selectionArgs).from(uriWithDeleteMode(uri, mode));
    }

    protected int bulkInsert(Uri uri, ContentValues... valuesArray) {
        return DataStore.bulkInsert().values(valuesArray).into(uri);
    }

    protected Cursor query(Uri uri, Query query) {
        return query.from(Robolectric.application, uri).execute();
    }

    protected void assertQuerySize(Uri uri, Query query, int expectedSize) {
        final Cursor cursor = query(uri, query);
        assertEquals(expectedSize, cursor.getCount());
        IOUtils.closeQuietly(cursor);
    }

    private Uri uriWithDeleteMode(Uri uri, String mode) {
        return ProviderUtils.withQueryParameter(uri, ProviderUtils.QueryParameterKey.DELETE_MODE, mode);
    }
}
