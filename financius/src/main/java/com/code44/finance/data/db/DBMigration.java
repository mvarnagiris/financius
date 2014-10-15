package com.code44.finance.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.SyncState;

import java.util.UUID;

public final class DBMigration {
    private DBMigration() {
    }

    /**
     * 46 - v0.10.0
     */
    public static void upgradeV19(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            db.execSQL(Tables.Tags.createScript());
            DBHelper.createIndex(db, Tables.Tags.ID);
            db.execSQL(Tables.TransactionTags.createScript());

            v19EnsureIds(db, "currencies");
            v19EnsureIds(db, "accounts");
            v19EnsureIds(db, "categories");
            v19EnsureIds(db, "transactions");

            final String tempCurrenciesTable = v19MigrateCurrencies(db);
            final String tempAccountsTable = v19MigrateAccounts(db);
            final String tempCategoriesTable = v19MigrateCategories(db);
            final String tempTransactionsTable = v19MigrateTransactions(db);

            db.execSQL("drop table " + tempCurrenciesTable);
            db.execSQL("drop table " + tempAccountsTable);
            db.execSQL("drop table " + tempCategoriesTable);
            db.execSQL("drop table " + tempTransactionsTable);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // Currencies
        String tableName = "currencies";
        db.execSQL("alter table " + tableName + " rename to ");
    }

    private static void v19EnsureIds(SQLiteDatabase db, String tableName) {
        final String serverIdName = tableName + "_server_id";
        final Cursor cursor = db.query(tableName, new String[]{BaseColumns._ID}, null, null, null, null, null);
        final ContentValues values = new ContentValues();
        if (cursor != null && cursor.moveToFirst()) {
            values.put(serverIdName, UUID.randomUUID().toString());
            db.update(tableName, values, BaseColumns._ID + "=?", new String[]{String.valueOf(cursor.getLong(0))});
        }
    }

    private static String v19MigrateCurrencies(SQLiteDatabase db) {
        final String oldTableName = "currencies";
        final String tempTableName = "temp_" + oldTableName;
        db.execSQL("alter table " + oldTableName + " rename to " + tempTableName);
        db.execSQL(Tables.Currencies.createScript());
        DBHelper.createIndex(db, Tables.Currencies.ID);

        final String[] projection = {tempTableName + "_server_id", tempTableName + "_code",
                tempTableName + "_symbol", tempTableName + "_decimals",
                tempTableName + "_decimal_separator", tempTableName + "_group_separator",
                tempTableName + "_symbol_format", tempTableName + "_is_default",
                tempTableName + "_exchange_rate"};
        final String selection = tempTableName + "_delete_state = 0";
        final Cursor cursor = db.query(tempTableName, projection, selection, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final Currency currency = new Currency();
                currency.setId(cursor.getString(0));
                currency.setModelState(ModelState.NORMAL);
                currency.setSyncState(SyncState.NONE);
                currency.setCode(cursor.getString(1));
                currency.setSymbol(cursor.getString(2));
                final String symbolPositionOld = cursor.getString(6);
                final SymbolPosition symbolPosition = "LF".equals(symbolPositionOld) ? SymbolPosition.FAR_LEFT :
                        "LC".equals(symbolPositionOld) ? SymbolPosition.CLOSE_LEFT :
                                "RC".equals(symbolPositionOld) ? SymbolPosition.CLOSE_RIGHT : SymbolPosition.FAR_RIGHT;
                currency.setSymbolPosition(symbolPosition);
                currency.setDecimalSeparator(DecimalSeparator.fromSymbol(cursor.getString(4)));
                currency.setGroupSeparator(GroupSeparator.fromSymbol(cursor.getString(5)));
                currency.setDecimalCount(cursor.getInt(3));
                currency.setDefault(cursor.getInt(7) != 0);
                currency.setExchangeRate(cursor.getDouble(8));
                db.insert(Tables.Currencies.TABLE_NAME, null, currency.asValues());
            } while (cursor.moveToNext());
        }

        return tempTableName;
    }

    private static String v19MigrateAccounts(SQLiteDatabase db) {
        final String oldTableName = "accounts";
        final String tempTableName = "temp_" + oldTableName;
        db.execSQL("alter table " + oldTableName + " rename to " + tempTableName);
        db.execSQL(Tables.Accounts.createScript());
        DBHelper.createIndex(db, Tables.Accounts.ID);

        // TODO Migrate

        return tempTableName;
    }

    private static String v19MigrateCategories(SQLiteDatabase db) {
        final String oldTableName = "categories";
        final String tempTableName = "temp_" + oldTableName;
        db.execSQL("alter table " + oldTableName + " rename to " + tempTableName);
        db.execSQL(Tables.Categories.createScript());
        DBHelper.createIndex(db, Tables.Categories.ID);

        // TODO Migrate

        return tempTableName;
    }

    private static String v19MigrateTransactions(SQLiteDatabase db) {
        final String oldTableName = "transactions";
        final String tempTableName = "temp_" + oldTableName;
        db.execSQL("alter table " + oldTableName + " rename to " + tempTableName);
        db.execSQL(Tables.Transactions.createScript());
        DBHelper.createIndex(db, Tables.Transactions.ID);

        // TODO Migrate

        return tempTableName;
    }
}
