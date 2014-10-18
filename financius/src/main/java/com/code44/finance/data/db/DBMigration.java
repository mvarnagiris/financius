package com.code44.finance.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.data.model.Tag;

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
            final String tempAccountsTable = v19MigrateAccounts(db, tempCurrenciesTable);
            final String tempCategoriesTable = v19MigrateCategories(db);
            final String tempTransactionsTable = v19MigrateTransactions(db);

            db.execSQL("drop table " + tempCurrenciesTable);
            db.execSQL("drop table " + tempAccountsTable);
            db.execSQL("drop table " + tempCategoriesTable);
            db.execSQL("drop table " + tempTransactionsTable);

            // TODO Recalculate accounts balance.

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
            final Currency currency = new Currency();
            currency.setModelState(ModelState.Normal);
            currency.setSyncState(SyncState.None);
            do {
                currency.setId(cursor.getString(0));
                currency.setCode(cursor.getString(1));
                currency.setSymbol(cursor.getString(2));
                final String symbolPositionOld = cursor.getString(6);
                final SymbolPosition symbolPosition = "LF".equals(symbolPositionOld) ? SymbolPosition.FarLeft :
                        "LC".equals(symbolPositionOld) ? SymbolPosition.CloseLeft :
                                "RC".equals(symbolPositionOld) ? SymbolPosition.CloseRight : SymbolPosition.FarRight;
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

    private static String v19MigrateAccounts(SQLiteDatabase db, String currencyTempTableName) {
        final String oldTableName = "accounts";
        final String tempTableName = "temp_" + oldTableName;
        db.execSQL("alter table " + oldTableName + " rename to " + tempTableName);
        db.execSQL(Tables.Accounts.createScript());
        DBHelper.createIndex(db, Tables.Accounts.ID);

        final String[] projection = {tempTableName + "_server_id", currencyTempTableName + "_server_id",
                tempTableName + "_title", tempTableName + "_note", tempTableName + "_show_in_totals"};
        final String selection = tempTableName + "_delete_state = 0 and " + tempTableName + "_origin = 1";
        final String tables = tempTableName + " inner join " + currencyTempTableName + " on " +
                currencyTempTableName + "._id=" + tempTableName + "_currency_id";
        final Cursor cursor = db.query(tables, projection, selection, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            final Currency currency = new Currency();
            final Account account = new Account();
            account.setModelState(ModelState.Normal);
            account.setSyncState(SyncState.None);
            account.setCurrency(currency);
            do {
                account.setId(cursor.getString(0));
                currency.setId(cursor.getString(1));
                account.setTitle(cursor.getString(2));
                account.setNote(cursor.getString(3));
                account.setIncludeInTotals(cursor.getInt(4) != 0);
                db.insert(Tables.Accounts.TABLE_NAME, null, account.asValues());
            } while (cursor.moveToNext());
        }

        return tempTableName;
    }

    private static String v19MigrateCategories(SQLiteDatabase db) {
        final String oldTableName = "categories";
        final String tempTableName = "temp_" + oldTableName;
        db.execSQL("alter table " + oldTableName + " rename to " + tempTableName);
        db.execSQL(Tables.Categories.createScript());
        DBHelper.createIndex(db, Tables.Categories.ID);

        final String[] projection = {tempTableName + "_server_id", tempTableName + "_title",
                tempTableName + "_color", tempTableName + "_type", tempTableName + "_order",
                tempTableName + "_level"};
        final String selection = tempTableName + "_delete_state = 0 and " + tempTableName + "_origin = 1";
        final Cursor cursor = db.query(tempTableName, projection, selection, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            final Category category = new Category();
            category.setModelState(ModelState.Normal);
            category.setSyncState(SyncState.None);
            final Tag tag = new Tag();
            tag.setModelState(ModelState.Normal);
            tag.setSyncState(SyncState.None);
            do {
                final int level = cursor.getInt(5);
                if (level == 1) {
                    category.setId(cursor.getString(0));
                    category.setTitle(cursor.getString(1));
                    category.setColor(cursor.getInt(2));
                    category.setSortOrder(cursor.getInt(4));
                    final int categoryType = cursor.getInt(3);
                    category.setTransactionType(categoryType == 0 ? TransactionType.Income : categoryType == 1 ? TransactionType.Expense : TransactionType.Transfer);
                    db.insert(Tables.Categories.TABLE_NAME, null, category.asValues());
                } else {
                    tag.setId(cursor.getString(0));
                    tag.setTitle(cursor.getString(1));
                    db.insert(Tables.Tags.TABLE_NAME, null, category.asValues());
                }
            } while (cursor.moveToNext());
        }

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
