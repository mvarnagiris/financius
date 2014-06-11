package com.code44.finance.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Pair;

import com.code44.finance.db.Column;

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

    public static void updateTimestamp(Column timestampColumn, long timestamp, ContentValues[] valuesArray) {
        for (ContentValues values : valuesArray) {
            values.put(timestampColumn.getName(), timestamp);
        }
    }

    @SuppressWarnings("unchecked")
    public static Uri appendParams(Uri uri, Pair<String, String>... parameters) {
        Uri.Builder builder = uri.buildUpon();
        for (Pair<String, String> param : parameters) {
            builder.appendQueryParameter(param.first, param.second);
        }
        return builder.build();
    }
}
