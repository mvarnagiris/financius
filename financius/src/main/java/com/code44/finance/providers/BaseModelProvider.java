package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.db.model.BaseModel;
import com.code44.finance.db.model.Currency;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public abstract class BaseModelProvider<T extends BaseModel> extends BaseProvider {
    private static final int URI_ITEMS = 1;
    private static final int URI_ITEMS_ID = 2;

    @Override
    public boolean onCreate() {
        final boolean result = super.onCreate();

        final String authority = getAuthority();
        final String mainTable = getModelClass().getSimpleName();
        uriMatcher.addURI(authority, mainTable, URI_ITEMS);
        uriMatcher.addURI(authority, mainTable + "/#", URI_ITEMS_ID);

        return result;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ITEMS:
                return TYPE_LIST_BASE + getModelClass().getSimpleName();
            case URI_ITEMS_ID:
                return TYPE_ITEM_BASE + getModelClass().getSimpleName();
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Cursor cursor;

        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                cursor = queryItems();
                break;

            case URI_ITEMS_ID:
                cursor = queryItem(uri);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        final Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                final long newId = cupboard().withDatabase(database).put(getModelClass(), values);
                return ContentUris.withAppendedId(uri, newId);

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                return cupboard().withDatabase(database).update(getModelClass(), values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                return cupboard().withDatabase(database).delete(getModelClass(), selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    protected abstract Class<T> getModelClass();

    public Cursor queryItems() {
        return cupboard().withDatabase(database).query(Currency.class).getCursor();
    }

    public Cursor queryItem(Uri uri) {
        return cupboard().withDatabase(database).query(Currency.class).byId(ContentUris.parseId(uri)).getCursor();
    }
}
