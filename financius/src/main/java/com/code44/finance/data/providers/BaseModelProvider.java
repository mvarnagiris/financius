package com.code44.finance.data.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public abstract class BaseModelProvider extends BaseProvider {
    private static final int URI_ITEMS = 1;
    private static final int URI_ITEMS_ID = 2;

    public static Uri uriModels(Class<? extends BaseModelProvider> providerClass, String modelTable) {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(providerClass) + "/" + modelTable);
    }

    public static Uri uriModel(Class<? extends BaseModelProvider> providerClass, String modelTable, long modelId) {
        return ContentUris.withAppendedId(uriModels(providerClass, modelTable), modelId);
    }

    @Override
    public boolean onCreate() {
        final boolean result = super.onCreate();

        final String authority = getAuthority();
        final String mainTable = getModelTable();
        uriMatcher.addURI(authority, mainTable, URI_ITEMS);
        uriMatcher.addURI(authority, mainTable + "/#", URI_ITEMS_ID);

        return result;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ITEMS:
                return TYPE_LIST_BASE + getModelTable();
            case URI_ITEMS_ID:
                return TYPE_ITEM_BASE + getModelTable();
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
                cursor = queryItems(projection, selection, selectionArgs, sortOrder);
                break;

            case URI_ITEMS_ID:
                cursor = queryItem(uri, projection, selection, selectionArgs, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        final Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        cursor.moveToFirst();

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                count = database.update(getModelTable(), values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        ProviderUtils.notifyChangeIfNecessary(getContext(), uri);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override
    public int bulkInsert(Uri uri, @SuppressWarnings("NullableProblems") ContentValues[] valuesArray) {
        int count;
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                count = ProviderUtils.doArrayReplaceInTransaction(database, getModelTable(), valuesArray);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        ProviderUtils.notifyChangeIfNecessary(getContext(), uri);
        return count;
    }

    protected abstract String getModelTable();

    protected abstract String getQueryTables();

    public Cursor queryItems(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getQueryTables());

        return qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor queryItem(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getQueryTables());
        //noinspection ConstantConditions
        qb.appendWhere(getModelTable() + "." + BaseColumns._ID + "=" + uri.getPathSegments().get(1));

        return qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
    }
}
