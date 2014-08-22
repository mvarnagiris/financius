package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.code44.finance.data.db.Tables;

public final class ProviderUtils {
    private ProviderUtils() {
    }

    public static int doArrayReplaceInTransaction(SQLiteDatabase db, String tableName, ContentValues[] valuesArray) {
        int count = 0;
        try {
            db.beginTransaction();
            count = doArrayReplace(db, tableName, valuesArray);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.endTransaction();
        }

        return count;
    }

    public static int doArrayReplace(SQLiteDatabase db, String tableName, ContentValues[] valuesArray) {
        int count = 0;
        for (final ContentValues values : valuesArray) {
            doUpdateOrInsert(db, tableName, values, false);
            count++;
        }
        return count;
    }

    public static long doUpdateOrInsert(SQLiteDatabase db, String tableName, ContentValues values, boolean returnNewId) {
        // Get id columns
        final String idColumn = BaseColumns._ID;
        final String serverIdColumn = tableName + "_" + Tables.SUFFIX_ID;

        // Get ids
        final Long id = values.getAsLong(idColumn);
        final String serverId = values.getAsString(serverIdColumn);
        long newId = id != null ? id : 0;

        // Find value to check for update
        final boolean tryUpdate;
        final String columnToCheck;
        String valueToCheck;
        if (newId > 0) {
            // We have local ID. Will try to update.
            tryUpdate = true;
            columnToCheck = idColumn;
            valueToCheck = String.valueOf(newId);
        } else if (!TextUtils.isEmpty(serverId)) {
            // Have server ID. Will try to update.
            tryUpdate = true;
            columnToCheck = serverIdColumn;
            valueToCheck = serverId;
        } else {
            // No IDs. Will not try to update.
            tryUpdate = false;
            columnToCheck = "";
            valueToCheck = "";
        }

        // Update or insert
        if (!tryUpdate || db.update(tableName, values, columnToCheck + "=?", new String[]{valueToCheck}) == 0) {
            newId = db.insert(tableName, null, values);
            if (newId <= 0) {
                throw new SQLException("Failed to insert values " + values.toString() + " into " + tableName);
            }
        }

        // Get local ID if necessary
        if (newId == 0 && returnNewId && !TextUtils.isEmpty(serverId)) {
            newId = getLocalId(db, tableName, serverId);
        }

        return newId;
    }

    public static long getLocalId(SQLiteDatabase db, String tableName, String serverId) {
        long localId = 0;
        Cursor c = null;
        try {
            c = db.query(tableName, new String[]{BaseColumns._ID}, tableName + "_" + Tables.SUFFIX_ID + "=?", new String[]{serverId}, null, null, null);
            if (c != null && c.moveToFirst()) {
                localId = c.getLong(0);
            }
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return localId;
    }

    public static void notifyChangeIfNecessary(Context context, Uri uri) {
        notifyUris(context, uri);
    }

    public static void notifyUris(Context context, Uri... uris) {
        if (uris == null) {
            return;
        }

        for (Uri uri : uris) {
            boolean notifyUriChanged = uri.getBooleanQueryParameter(QueryParameterKey.NOTIFY_URI_CHANGED.getKeyName(), true);
            if (notifyUriChanged) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }
    }

    public static Uri withQueryParameter(Uri uri, QueryParameterKey parameterKey, String value) {
        return uri.buildUpon().appendQueryParameter(parameterKey.getKeyName(), value).build();
    }

    public static enum QueryParameterKey {
        /**
         * Possible values: {@code "true"} and {@code "false"}.
         */
        NOTIFY_URI_CHANGED("notifyUriChanged"),

        /**
         * Possible values: {@code "delete"}, {@code "undo"} and {@code "commit"}.
         */
        DELETE_MODE("deleteMode");

        private final String keyName;

        private QueryParameterKey(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return keyName;
        }
    }
}
