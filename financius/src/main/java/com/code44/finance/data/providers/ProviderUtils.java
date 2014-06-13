package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

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
            db.replace(tableName, null, values);
            count++;
        }
        return count;
    }

    public static void notifyChangeIfNecessary(Context context, Uri uri) {
        boolean notifyUriChanged = uri.getBooleanQueryParameter(QueryParameterKey.NOTIFY_URI_CHANGED.getKeyName(), true);
        if (notifyUriChanged) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    public static enum QueryParameterKey {
        /**
         * Possible values: {@code "true"} and {@code "false"}.
         */
        NOTIFY_URI_CHANGED("notifyUriChanged");

        private final String keyName;

        private QueryParameterKey(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return keyName;
        }
    }
}
