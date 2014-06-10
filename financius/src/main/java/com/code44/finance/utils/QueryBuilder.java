package com.code44.finance.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class QueryBuilder {
    private final ContentResolver contentResolver;
    private final Uri uri;
    private final SQLiteDatabase database;
    private final String table;

    private String[] projection;
    private String selection;
    private String[] selectionArgs;
    private String sortOrder;

    private QueryBuilder(ContentResolver contentResolver, Uri uri, SQLiteDatabase database, String table) {
        this.contentResolver = contentResolver;
        this.uri = uri;
        this.database = database;
        this.table = table;
    }

    public static QueryBuilder with(ContentResolver contentResolver, Uri uri) {
        return new QueryBuilder(contentResolver, uri, null, null);
    }

    public static QueryBuilder with(SQLiteDatabase database, String table) {
        return new QueryBuilder(null, null, database, table);
    }

    public QueryBuilder projection(String... projection) {
        this.projection = projection;
        return this;
    }

    public QueryBuilder selection(String selection, String... selectionArgs) {
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        return this;
    }

    public QueryBuilder sortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public Cursor query() {
        Cursor cursor;
        if (database == null) {
            cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } else {
            cursor = database.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        }

        if (cursor == null) {
            cursor = new MatrixCursor(projection, 0);
        }
        cursor.moveToFirst();

        return cursor;
    }
}
