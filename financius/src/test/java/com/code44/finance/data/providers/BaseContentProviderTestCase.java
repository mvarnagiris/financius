package com.code44.finance.data.providers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.DBDefaults;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.utils.IOUtils;

import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;

import static org.junit.Assert.*;

public class BaseContentProviderTestCase {
    protected Context context;
    protected ContentResolver contentResolver;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.getShadowApplication().getApplicationContext();

        final CurrenciesProvider currenciesProvider = new CurrenciesProvider();
        currenciesProvider.onCreate();

        final AccountsProvider accountsProvider = new AccountsProvider();
        accountsProvider.onCreate();

        final CategoriesProvider categoriesProvider = new CategoriesProvider();
        categoriesProvider.onCreate();

        final TransactionsProvider transactionsProvider = new TransactionsProvider();
        transactionsProvider.onCreate();

        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(CurrenciesProvider.class), currenciesProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(AccountsProvider.class), accountsProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(CategoriesProvider.class), categoriesProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(TransactionsProvider.class), transactionsProvider);
        contentResolver = context.getContentResolver();
    }

    @After
    public void tearDown() throws Exception {
        SQLiteDatabase database = DBHelper.get(Robolectric.getShadowApplication().getApplicationContext()).getWritableDatabase();

        database.delete(Tables.Transactions.TABLE_NAME, null, null);
        database.delete(Tables.Categories.TABLE_NAME, null, null);
        database.delete(Tables.Accounts.TABLE_NAME, null, null);
        database.delete(Tables.Currencies.TABLE_NAME, null, null);
        DBDefaults.addDefaults(Robolectric.getShadowApplication().getApplicationContext(), database);
    }

    protected long insert(Uri uri, BaseModel model) {
        //noinspection ConstantConditions
        return ContentUris.parseId(contentResolver.insert(uri, model.asContentValues()));
    }

    protected int update(Uri uri, ContentValues values, String selection, String... selectionArgs) {
        return contentResolver.update(uri, values, selection, selectionArgs);
    }

    protected int delete(Uri uri, String selection, String... selectionArgs) {
        return contentResolver.delete(uri, selection, selectionArgs);
    }

    protected Cursor query(Uri uri, Query query) {
        return contentResolver.query(uri, query.getProjection(), query.getSelection(), query.getSelectionArgs(), query.getSortOrder());
    }

    protected void assertQuerySize(Uri uri, Query query, int expectedSize) {
        final Cursor cursor = query(uri, query);
        assertEquals(expectedSize, cursor.getCount());
        IOUtils.closeQuietly(cursor);
    }

    protected Uri uriWithDeleteMode(Uri uri, String mode) {
        return ProviderUtils.withQueryParameter(uri, ProviderUtils.QueryParameterKey.DELETE_MODE, mode);
    }
}
