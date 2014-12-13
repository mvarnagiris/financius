package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.code44.finance.api.Api;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

@SuppressWarnings("UnusedParameters")
public abstract class BaseModelProvider extends BaseProvider {
    private static final int URI_ITEMS = 1;
    private static final int URI_ITEMS_ID = 2;

    @Inject Api api;

    public static Uri uriModels(Class<? extends BaseModelProvider> providerClass, String modelTable) {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(providerClass) + "/" + modelTable);
    }

    public static Uri uriModel(Class<? extends BaseModelProvider> providerClass, String modelTable, String modelServerId) {
        return Uri.withAppendedPath(uriModels(providerClass, modelTable), modelServerId);
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
        final String serverId = values.getAsString(getIdColumn().getName());
        if (Strings.isEmpty(serverId)) {
            throw new IllegalArgumentException("Server Id cannot be empty.");
        }

        final SQLiteDatabase database = getDatabase();
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
                try {
                    database.beginTransaction();

                    final Map<String, Object> extras = new HashMap<>();
                    onBeforeInsertItem(uri, values, serverId, extras);
                    insertItem(uri, values, serverId);
                    onAfterInsertItem(uri, values, serverId, extras);

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
        api.sync();

        return Uri.withAppendedPath(uri, serverId);
    }

    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase database = getDatabase();
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
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
        api.sync();

        return count;
    }

    @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        final SQLiteDatabase database = getDatabase();
        final int uriId = uriMatcher.match(uri);
        final ModelState modelState;
        switch (uriId) {
            case URI_ITEMS:
                final String deleteMode = uri.getQueryParameter(ProviderUtils.QueryParameterKey.DELETE_MODE.getKeyName());
                if (TextUtils.isEmpty(deleteMode)) {
                    throw new IllegalArgumentException("Uri " + uri + " must have query parameter " + ProviderUtils.QueryParameterKey.DELETE_MODE.getKeyName());
                }

                switch (deleteMode) {
                    case "delete":
                        modelState = ModelState.DeletedUndo;
                        break;
                    case "undo":
                        modelState = ModelState.Normal;
                        break;
                    case "commit":
                        modelState = ModelState.Deleted;
                        break;
                    default:
                        throw new IllegalArgumentException(ProviderUtils.QueryParameterKey.DELETE_MODE.getKeyName() + "=" + deleteMode + " is not supported.");
                }
                try {
                    database.beginTransaction();

                    final Map<String, Object> extras = new HashMap<>();
                    onBeforeDeleteItems(uri, selection, selectionArgs, modelState, extras);
                    count = deleteItems(uri, selection, selectionArgs, modelState);
                    onAfterDeleteItems(uri, selection, selectionArgs, modelState, extras);

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
        if (modelState != ModelState.DeletedUndo) {
            api.sync();
        }

        return count;
    }

    @Override public int bulkInsert(Uri uri, @SuppressWarnings("NullableProblems") ContentValues[] valuesArray) {
        int count;
        final SQLiteDatabase database = getDatabase();
        final int uriId = uriMatcher.match(uri);
        switch (uriId) {
            case URI_ITEMS:
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

    protected void onBeforeInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> outExtras) {
    }

    protected long insertItem(Uri uri, ContentValues values, String serverId) {
        if (values.containsKey(BaseColumns._ID)) {
            values.put(getModelTable() + "_" + Tables.SUFFIX_SYNC_STATE, SyncState.LocalChanges.asInt());
        } else {
            values.put(getModelTable() + "_" + Tables.SUFFIX_SYNC_STATE, SyncState.None.asInt());
        }

        final SQLiteDatabase database = getDatabase();
        return ProviderUtils.doUpdateOrInsert(database, getModelTable(), values, true);
    }

    protected void onAfterInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> extras) {
    }

    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
    }

    protected int updateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        values.put(getModelTable() + "_" + Tables.SUFFIX_SYNC_STATE, SyncState.LocalChanges.asInt());
        return getDatabase().update(getModelTable(), values, selection, selectionArgs);
    }

    protected void onAfterUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> extras) {
    }

    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> outExtras) {
    }

    protected int deleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState) {
        final ContentValues values = new ContentValues();
        values.put(getModelTable() + "_" + Tables.SUFFIX_MODEL_STATE, modelState.asInt());
        if (modelState == ModelState.Deleted) {
            values.put(getModelTable() + "_" + Tables.SUFFIX_SYNC_STATE, SyncState.LocalChanges.asInt());
        }

        final String whereClause;
        final String[] whereArgs;
        if (modelState == ModelState.DeletedUndo) {
            whereClause = selection;
            whereArgs = selectionArgs;
        } else {
            whereClause = getModelTable() + "_" + Tables.SUFFIX_MODEL_STATE + "=?";
            whereArgs = new String[]{String.valueOf(ModelState.DeletedUndo.asInt())};
        }

        return getDatabase().update(getModelTable(), values, whereClause, whereArgs);
    }

    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> extras) {
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

    protected List<String> getIdList(Column serverIdColumn, String selection, String[] selectionArgs) {
        final List<String> affectedIds = new ArrayList<>();

        final Query query = Query.create().projection(serverIdColumn.getName());
        if (!TextUtils.isEmpty(selection)) {
            query.selection(selection);
        }
        if (selectionArgs != null && selectionArgs.length > 0) {
            query.args(selectionArgs);
        }

        final Cursor cursor = query.from(getDatabase(), serverIdColumn.getTableName()).execute();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int iServerId = cursor.getColumnIndex(serverIdColumn.getName());
                    affectedIds.add(cursor.getString(iServerId));
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }

        return affectedIds;
    }

    protected Uri uriForDeleteFromItemState(Uri uri, ModelState modelState) {
        final String deleteMode;
        switch (modelState) {
            case Normal:
                deleteMode = "undo";
                break;

            case DeletedUndo:
                deleteMode = "delete";
                break;

            case Deleted:
                deleteMode = "commit";
                break;

            default:
                throw new IllegalArgumentException("ModelState " + modelState + " is not supported for delete.");
        }

        return ProviderUtils.withQueryParameter(uri, ProviderUtils.QueryParameterKey.DELETE_MODE, deleteMode);
    }
}
