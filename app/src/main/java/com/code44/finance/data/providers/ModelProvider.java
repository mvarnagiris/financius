package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.code44.finance.api.endpoints.EndpointsApi;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public abstract class ModelProvider extends BaseModelProvider {
    private static final String EXTRA_MODEL_STATE = "EXTRA_MODEL_STATE";
    private static final String EXTRA_COLUMN_VALUES = "EXTRA_COLUMN_VALUES";

    @Inject User user;
    @Inject EndpointsApi endpointsApi;

    @Override public Uri insert(Uri uri, ContentValues values) {
        final Uri newUri = super.insert(uri, values);
        sync();
        return newUri;
    }

    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int count = super.update(uri, values, selection, selectionArgs);
        sync();
        return count;
    }

    @Override protected long insertItem(Uri uri, ContentValues values) {
        if (values.containsKey(BaseColumns._ID)) {
            values.put(getModelTable() + "_" + Tables.SUFFIX_SYNC_STATE, SyncState.LocalChanges.asInt());
        } else {
            values.put(getModelTable() + "_" + Tables.SUFFIX_SYNC_STATE, SyncState.None.asInt());
        }

        return super.insertItem(uri, values);
    }

    @Override protected int updateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        values.put(getModelTable() + "_" + Tables.SUFFIX_SYNC_STATE, SyncState.LocalChanges.asInt());
        return super.updateItems(uri, values, selection, selectionArgs);
    }

    @Override protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, outExtras);

        final String deleteMode = uri.getQueryParameter(ProviderUtils.QueryParameterKey.DELETE_MODE.getKeyName());
        if (TextUtils.isEmpty(deleteMode)) {
            throw new IllegalArgumentException("Uri " + uri + " must have query parameter " + ProviderUtils.QueryParameterKey.DELETE_MODE.getKeyName());
        }

        final ModelState modelState;
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

        outExtras.put(EXTRA_MODEL_STATE, modelState);
    }

    @Override protected int deleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
        final ModelState modelState = getModelState(extras);
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

        int count = getDatabase().update(getModelTable(), values, whereClause, whereArgs);
        if (modelState == ModelState.Deleted) {
            sync();
        }

        return count;
    }

    protected void putColumnToExtras(Map<String, Object> extras, Column column, String selection, String[] selectionArgs) {
        final List<String> affectedRecordsColumnValues = new ArrayList<>();

        final Query query = Query.create().projection(column.getName());
        if (!TextUtils.isEmpty(selection)) {
            query.selection(selection);
        }
        if (selectionArgs != null && selectionArgs.length > 0) {
            query.args(selectionArgs);
        }

        final Cursor cursor = query.from(getDatabase(), column.getTableName()).execute();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int iColumn = cursor.getColumnIndex(column.getName());
                    affectedRecordsColumnValues.add(cursor.getString(iColumn));
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }

        extras.put(EXTRA_COLUMN_VALUES, affectedRecordsColumnValues);
    }

    protected ModelState getModelState(Map<String, Object> extras) {
        return (ModelState) extras.get(EXTRA_MODEL_STATE);
    }

    protected List<String> getColumnValues(Map<String, Object> extras) {
        //noinspection unchecked
        return (List<String>) extras.get(EXTRA_COLUMN_VALUES);
    }

    protected Uri uriForDeleteFromModelState(Uri uri, ModelState modelState) {
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

    private void sync() {
        if (user.isLoggedIn()) {
            endpointsApi.syncModels();
        }
    }
}
