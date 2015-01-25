package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.data.db.Column;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedParameters")
public abstract class BaseModelProvider extends BaseProvider {
    private static final int URI_ITEMS = 1;
    private static final int URI_ITEMS_ID = 2;

    public static Uri uriModels(Class<? extends BaseModelProvider> providerClass, String baseModelTable) {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(providerClass) + "/" + baseModelTable);
    }

    public static Uri uriModel(Class<? extends BaseModelProvider> providerClass, String baseModelTable, String baseModelId) {
        return Uri.withAppendedPath(uriModels(providerClass, baseModelTable), baseModelId);
    }

    @Override public boolean onCreate() {
        super.onCreate();

        final String authority = getAuthority();
        final String mainTable = getModelTable();
        uriMatcher.addURI(authority, mainTable, URI_ITEMS);
        uriMatcher.addURI(authority, mainTable + "/*", URI_ITEMS_ID);

        return true;
    }

    @Override public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ITEMS:
                return TYPE_LIST_BASE + getModelTable();
            case URI_ITEMS_ID:
                return TYPE_ITEM_BASE + getModelTable();
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final Cursor cursor;

        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                cursor = queryItems(uri, projection, selection, selectionArgs, sortOrder);
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

    @Override public Uri insert(Uri uri, ContentValues values) {
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                final SQLiteDatabase database = getDatabase();
                try {
                    database.beginTransaction();

                    final Map<String, Object> extras = new HashMap<>();
                    onBeforeInsertItem(uri, values, extras);
                    insertItem(uri, values);
                    onAfterInsertItem(uri, values, extras);

                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        ProviderUtils.notifyChangeIfNecessary(getContext(), uri);
        ProviderUtils.notifyUris(getContext(), getOtherUrisToNotify());

        return uri;
    }

    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                final SQLiteDatabase database = getDatabase();
                try {
                    database.beginTransaction();

                    final Map<String, Object> extras = new HashMap<>();
                    onBeforeUpdateItems(uri, values, selection, selectionArgs, extras);
                    count = updateItems(uri, values, selection, selectionArgs);
                    onAfterUpdateItems(uri, values, selection, selectionArgs, extras);

                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        ProviderUtils.notifyChangeIfNecessary(getContext(), uri);
        ProviderUtils.notifyUris(getContext(), getOtherUrisToNotify());

        return count;
    }

    @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                final SQLiteDatabase database = getDatabase();
                try {
                    database.beginTransaction();

                    final Map<String, Object> extras = new HashMap<>();
                    onBeforeDeleteItems(uri, selection, selectionArgs, extras);
                    count = deleteItems(uri, selection, selectionArgs, extras);
                    onAfterDeleteItems(uri, selection, selectionArgs, extras);

                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        ProviderUtils.notifyChangeIfNecessary(getContext(), uri);
        ProviderUtils.notifyUris(getContext(), getOtherUrisToNotify());

        return count;
    }

    @Override public int bulkInsert(Uri uri, @NonNull ContentValues[] valuesArray) {
        int count;
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                final SQLiteDatabase database = getDatabase();
                try {
                    database.beginTransaction();

                    final Map<String, Object> extras = new HashMap<>();
                    onBeforeBulkInsertItems(uri, valuesArray, extras);
                    count = bulkInsertItems(uri, valuesArray, extras);
                    onAfterBulkInsertItems(uri, valuesArray, extras);

                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        ProviderUtils.notifyChangeIfNecessary(getContext(), uri);
        ProviderUtils.notifyUris(getContext(), getOtherUrisToNotify());

        return count;
    }

    protected abstract String getModelTable();

    protected abstract String getQueryTables(Uri uri);

    protected abstract Column getIdColumn();

    protected Cursor queryItems(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getQueryTables(uri));

        final SQLiteDatabase database = getDatabase();
        return qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
    }

    protected Cursor queryItem(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(getQueryTables(uri));
        qb.appendWhere(getIdColumn() + "='" + uri.getPathSegments().get(1) + "'");

        final SQLiteDatabase database = getDatabase();
        return qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
    }

    protected void onBeforeInsertItem(Uri uri, ContentValues values, Map<String, Object> outExtras) {
    }

    protected long insertItem(Uri uri, ContentValues values) {
        return ProviderUtils.doUpdateOrInsert(getDatabase(), getModelTable(), values, true);
    }

    protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
    }

    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
    }

    protected int updateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return getDatabase().update(getModelTable(), values, selection, selectionArgs);
    }

    protected void onAfterUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> extras) {
    }

    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
    }

    protected int deleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
        return getDatabase().delete(getModelTable(), selection, selectionArgs);
    }

    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
    }

    protected void onBeforeBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> outExtras) {
    }

    protected int bulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        int count = 0;
        final SQLiteDatabase database = getDatabase();
        final String tableName = getModelTable();
        for (final ContentValues values : valuesArray) {
            onBeforeBulkInsertIteration(uri, values, extras);
            ProviderUtils.doUpdateOrInsert(database, tableName, values, false);
            onAfterBulkInsertIteration(uri, values, extras);
            count++;
        }
        return count;
    }

    protected void onBeforeBulkInsertIteration(Uri uri, ContentValues values, Map<String, Object> extras) {
    }

    protected void onAfterBulkInsertIteration(Uri uri, ContentValues values, Map<String, Object> extras) {
    }

    protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
    }

    protected Uri[] getOtherUrisToNotify() {
        return null;
    }
}
