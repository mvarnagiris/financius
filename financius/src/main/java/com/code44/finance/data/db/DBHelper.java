package com.code44.finance.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.code44.finance.money.CurrenciesManager;

public class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "finance.db";
    private static final int VERSION = 23;

    private final Context context;
    private final CurrenciesManager currenciesManager;

    public DBHelper(Context context, CurrenciesManager currenciesManager) {
        this(context, NAME, currenciesManager);
    }

    public DBHelper(Context context, String name, CurrenciesManager currenciesManager) {
        super(context, name, null, VERSION);
        this.context = context;
        this.currenciesManager = currenciesManager;
    }

    public static void createIndex(SQLiteDatabase db, Column serverIdColumn) {
        db.execSQL("create index " + serverIdColumn.getName() + "_idx ON " + serverIdColumn.getTableName() + "(" + serverIdColumn.getName() + ");");
    }

    @Override public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(Tables.ExchangeRates.createScript());
        db.execSQL(Tables.CurrencyFormats.createScript());
        db.execSQL(Tables.Accounts.createScript());
        db.execSQL(Tables.Categories.createScript());
        db.execSQL(Tables.Tags.createScript());
        db.execSQL(Tables.Transactions.createScript());
        db.execSQL(Tables.TransactionTags.createScript());

        // Create indexes
        createIndex(db, Tables.CurrencyFormats.ID);
        createIndex(db, Tables.Accounts.ID);
        createIndex(db, Tables.Categories.ID);
        createIndex(db, Tables.Tags.ID);
        createIndex(db, Tables.Transactions.ID);

        // Add defaults
        addDefaults(db, currenciesManager);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 18:
                DBMigration.upgradeV19(db);
            case 19:
                DBMigration.upgradeV20(db);
            case 20:
                DBMigration.upgradeV21(db);
            case 21:
                DBMigration.upgradeV22(db);
            case 22:
                DBMigration.upgradeV23(db, currenciesManager);
        }
    }

    public void clear() {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(Tables.CurrencyFormats.TABLE_NAME, null, null);
        // TODO Clear database for the user
    }

    private void addDefaults(SQLiteDatabase database, CurrenciesManager currenciesManager) {
        new DBDefaults(context, database, currenciesManager).addDefaults();
    }
}
