package com.code44.finance.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class DataStore {
    private final Context context;
    private final Uri uri;
    private final SQLiteDatabase database;
    private final String table;
    private final List<ContentValues> valuesList;

    private DataStore(Context context, Uri uri, SQLiteDatabase database, String table) {
        this.context = context;
        this.uri = uri;
        this.database = database;
        this.table = table;
        this.valuesList = new ArrayList<>();
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

    public static int doArrayUpdateInTransaction(SQLiteDatabase db, String tableName, String selection, String[] selectionArgs, ContentValues[] valuesArray) {
        int count = 0;
        try {
            db.beginTransaction();
            count = doArrayUpdate(db, tableName, selection, selectionArgs, valuesArray);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.endTransaction();
        }

        return count;
    }

    public static int doArrayUpdate(SQLiteDatabase db, String tableName, String selection, String[] selectionArgs, ContentValues[] valuesArray) {
        int count = 0;
        for (final ContentValues values : valuesArray) {
            db.update(tableName, values, selection, selectionArgs);
            count++;
        }
        return count;
    }

    public static DataStore with(Context context, Uri uri) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null.");
        }

        if (uri == null) {
            throw new NullPointerException("Uri cannot be null.");
        }

        return new DataStore(context, uri, null, null);
    }

    public static DataStore with(SQLiteDatabase database, String table) {
        if (database == null) {
            throw new NullPointerException("Database cannot be null.");
        }

        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("Table cannot be empty.");
        }

        return new DataStore(null, null, database, table);
    }

    public DataStore values(ContentValues values) {
        if (values == null) {
            throw new NullPointerException("Values cannot be null.");
        }

        valuesList.add(values);
        return this;
    }

    public DataStore values(ContentValues... valuesArray) {
        if (valuesArray == null || valuesArray.length == 0) {
            throw new IllegalArgumentException("Values array cannot be empty.");
        }

        values(Arrays.asList(valuesArray));
        return this;
    }

    public DataStore values(Collection<ContentValues> valuesCollection) {
        if (valuesCollection == null || valuesCollection.isEmpty()) {
            throw new IllegalArgumentException("Values collection cannot be empty.");
        }

        valuesList.addAll(valuesCollection);
        return this;
    }

    public DataStore clear() {
        valuesList.clear();
        return this;
    }

    public DataStore insert() {
        final ContentValues[] valuesArray = getValuesArray();
        if (context == null) {
            doArrayReplaceInTransaction(database, table, valuesArray);
        } else {
            context.getContentResolver().bulkInsert(uri, valuesArray);
        }

        return clear();
    }

    public DataStore update(String selection, String... selectionArgs) {
        final ContentValues[] valuesArray = getValuesArray();
        if (context == null) {
            doArrayUpdateInTransaction(database, table, selection, selectionArgs, valuesArray);
        } else {
            for (ContentValues values : valuesArray) {
                context.getContentResolver().update(uri, values, selection, selectionArgs);
            }
        }

        return clear();
    }

    private ContentValues[] getValuesArray() {
        return valuesList.toArray(new ContentValues[valuesList.size()]);
    }
}
