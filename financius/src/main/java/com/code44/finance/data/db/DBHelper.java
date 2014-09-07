package com.code44.finance.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "financius.db";
    private static final int VERSION = 1;

    private final Context context;

    public DBHelper(Context context) {
        this(context, NAME);
    }

    public DBHelper(Context context, String name) {
        super(context, name, null, VERSION);
        this.context = context;
    }

    @Override public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(Tables.Currencies.createScript());
        db.execSQL(Tables.Accounts.createScript());
        db.execSQL(Tables.Categories.createScript());
        db.execSQL(Tables.Tags.createScript());
        db.execSQL(Tables.Transactions.createScript());
        db.execSQL(Tables.TransactionTags.createScript());

        // Create indexes
        createIndex(db, Tables.Currencies.ID);
        createIndex(db, Tables.Accounts.ID);
        createIndex(db, Tables.Categories.ID);
        createIndex(db, Tables.Tags.ID);
        createIndex(db, Tables.Transactions.ID);

        // Add defaults
        addDefaults(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void clear() {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(Tables.Currencies.TABLE_NAME, null, null);
        // TODO Clear database for the user
    }

    private void addDefaults(SQLiteDatabase database) {
        new DBDefaultsManager(context, database).addDefaults();
    }

    private void createIndex(SQLiteDatabase db, Column serverIdColumn) {
        db.execSQL("create index " + serverIdColumn.getName() + "_idx ON " + serverIdColumn.getTableName() + "(" + serverIdColumn.getName() + ");");
    }
}
