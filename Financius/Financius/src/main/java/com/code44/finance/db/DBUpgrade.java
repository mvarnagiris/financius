package com.code44.finance.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.code44.finance.R;

import java.util.UUID;

public class DBUpgrade
{
    public static void updateCategoriesOrder(SQLiteDatabase db)
    {
        Cursor c = null;
        final ContentValues values = new ContentValues();

        // Expenses
        try
        {
            c = db.query(
                    Tables.Categories.TABLE_NAME,
                    new String[]{Tables.Categories.T_ID, Tables.Categories.PARENT_ID, Tables.Categories.LEVEL},
                    Tables.Categories.DELETE_STATE + "=? and " + Tables.Categories.LEVEL + ">? and " + Tables.Categories.TYPE + "=?",
                    new String[]{String.valueOf(Tables.DeleteState.NONE), "0", String.valueOf(Tables.Categories.Type.EXPENSE)}, null, null,
                    "case " + Tables.Categories.LEVEL + " when 1 then " + Tables.Categories.T_ID + " else " + Tables.Categories.PARENT_ID + " end, " + Tables.Categories.LEVEL + ", " + Tables.Categories.TITLE);
            if (c != null && c.moveToFirst())
            {
                int order = 0;
                int parentOrder = -1;
                do
                {
                    if (c.getInt(2) == 1)
                    {
                        parentOrder++;
                        values.clear();
                        values.put(Tables.Categories.ORDER, parentOrder);
                        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                        order = 0;
                    }
                    else
                    {
                        values.clear();
                        values.put(Tables.Categories.ORDER, order++);
                        values.put(Tables.Categories.PARENT_ORDER, parentOrder);
                        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                    }
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Income
        try
        {
            c = db.query(
                    Tables.Categories.TABLE_NAME,
                    new String[]{Tables.Categories.T_ID, Tables.Categories.PARENT_ID, Tables.Categories.LEVEL},
                    Tables.Categories.DELETE_STATE + "=? and " + Tables.Categories.LEVEL + ">? and " + Tables.Categories.TYPE + "=?",
                    new String[]{String.valueOf(Tables.DeleteState.NONE), "0", String.valueOf(Tables.Categories.Type.INCOME)}, null, null,
                    "case " + Tables.Categories.LEVEL + " when 1 then " + Tables.Categories.T_ID + " else " + Tables.Categories.PARENT_ID + " end, " + Tables.Categories.LEVEL + ", " + Tables.Categories.TITLE);
            if (c != null && c.moveToFirst())
            {
                int order = 0;
                int parentOrder = -1;
                do
                {
                    if (c.getInt(2) == 1)
                    {
                        parentOrder++;
                        values.clear();
                        values.put(Tables.Categories.ORDER, parentOrder);
                        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                        order = 0;
                    }
                    else
                    {
                        values.clear();
                        values.put(Tables.Categories.ORDER, order++);
                        values.put(Tables.Categories.PARENT_ORDER, parentOrder);
                        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                    }
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    /**
     * 11 - v0.5
     *
     * @param db Database.
     */
    public static void upgradeV7(SQLiteDatabase db)
    {
        // Update database with server_id values
        Cursor c = null;
        ContentValues values = new ContentValues();

        try
        {
            db.beginTransaction();

            // Accounts
            c = db.query(Tables.Accounts.TABLE_NAME, new String[]{Tables.Accounts.ID, Tables.Accounts.SERVER_ID}, Tables.Accounts.ORIGIN + "=?",
                    new String[]{String.valueOf(Tables.Accounts.Origin.USER)}, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    if (TextUtils.isEmpty(c.getString(1)))
                    {
                        values.clear();
                        values.put(Tables.Accounts.SERVER_ID, UUID.randomUUID().toString());
                        db.update(Tables.Accounts.TABLE_NAME, values, Tables.Accounts.ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                    }
                }
                while (c.moveToNext());
            }
            if (c != null && !c.isClosed())
                c.close();

            // Categories
            c = db.query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.ID, Tables.Categories.SERVER_ID}, Tables.Categories.ORIGIN + "=?",
                    new String[]{String.valueOf(Tables.Categories.Origin.USER)}, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    if (TextUtils.isEmpty(c.getString(1)))
                    {
                        values.clear();
                        values.put(Tables.Categories.SERVER_ID, UUID.randomUUID().toString());
                        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                    }
                }
                while (c.moveToNext());
            }
            if (c != null && !c.isClosed())
                c.close();

            // Transactions
            c = db.query(Tables.Transactions.TABLE_NAME, new String[]{Tables.Transactions.ID, Tables.Transactions.SERVER_ID}, null, null, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    if (TextUtils.isEmpty(c.getString(1)))
                    {
                        values.clear();
                        values.put(Tables.Transactions.SERVER_ID, UUID.randomUUID().toString());
                        db.update(Tables.Transactions.TABLE_NAME, values, Tables.Transactions.ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                    }
                }
                while (c.moveToNext());
            }
            if (c != null && !c.isClosed())
                c.close();

            db.setTransactionSuccessful();
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
            db.endTransaction();
        }

        // Create indexes
        final String[] tables = {Tables.Accounts.TABLE_NAME, Tables.Categories.TABLE_NAME, Tables.Transactions.TABLE_NAME};
        for (String tableName : tables)
            createCommonIndex(db, tableName);
    }

    /**
     * 16 - v0.5.2
     *
     * @param db Database.
     */
    public static void upgradeV8(SQLiteDatabase db)
    {
        // Update server IDs for system accounts
        ContentValues values = new ContentValues();
        values.put(Tables.Accounts.SERVER_ID, String.valueOf(Tables.Accounts.IDs.INCOME_ID));
        db.update(Tables.Accounts.TABLE_NAME, values, Tables.Accounts.ID + "=?", new String[]{String.valueOf(Tables.Accounts.IDs.INCOME_ID)});
        values.clear();
        values.put(Tables.Accounts.SERVER_ID, String.valueOf(Tables.Accounts.IDs.EXPENSE_ID));
        db.update(Tables.Accounts.TABLE_NAME, values, Tables.Accounts.ID + "=?", new String[]{String.valueOf(Tables.Accounts.IDs.EXPENSE_ID)});

        // Update database with server_id values
        values = new ContentValues();
        Cursor c = null;

        try
        {
            db.beginTransaction();

            // Transactions
            c = db.query(Tables.Transactions.TABLE_NAME, new String[]{Tables.Transactions.ID, Tables.Transactions.SERVER_ID}, "ifnull("
                    + Tables.Transactions.SERVER_ID + ", '') = ''", null, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    if (TextUtils.isEmpty(c.getString(1)))
                    {
                        values.clear();
                        values.put(Tables.Transactions.SERVER_ID, UUID.randomUUID().toString());
                        db.update(Tables.Transactions.TABLE_NAME, values, Tables.Transactions.ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                    }
                }
                while (c.moveToNext());
            }
            if (c != null && !c.isClosed())
                c.close();

            values.clear();
            values.put(Tables.Categories.ORIGIN, Tables.Categories.Origin.USER);
            db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.LEVEL + ">?", new String[]{"0"});

            db.setTransactionSuccessful();
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
            db.endTransaction();
        }
    }

    /**
     * 19 - v0.6
     *
     * @param context Context.
     * @param db      Database.
     */
    public static void upgradeV9(Context context, SQLiteDatabase db)
    {
        // Add new columns
        db.execSQL("ALTER TABLE " + Tables.Categories.TABLE_NAME + " ADD COLUMN " + Tables.Categories.COLOR + " INTEGER DEFAULT 0;");

        // Set default colors for categories
        ContentValues values;

        values = new ContentValues();
        values.put(Tables.Categories.COLOR, context.getResources().getColor(R.color.f_green));
        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.ID + "=?", new String[]{String.valueOf(Tables.Categories.IDs.INCOME_ID)});

        values = new ContentValues();
        values.put(Tables.Categories.COLOR, context.getResources().getColor(R.color.f_red));
        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.ID + "=?", new String[]{String.valueOf(Tables.Categories.IDs.EXPENSE_ID)});

        values = new ContentValues();
        values.put(Tables.Categories.COLOR, context.getResources().getColor(R.color.f_yellow));
        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.ID + "=?", new String[]{String.valueOf(Tables.Categories.IDs.TRANSFER_ID)});

        TypedArray ta = context.getResources().obtainTypedArray(R.array.category_colors);
        //noinspection ConstantConditions
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++)
        {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        Cursor c = null;
        try
        {
            c = db.query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.T_ID}, Tables.Categories.LEVEL + "=?", new String[]{"1"}, null, null, null);
            if (c != null && c.moveToFirst())
            {
                String id;
                do
                {
                    values = new ContentValues();
                    values.put(Tables.Categories.COLOR, colors[c.getPosition() % colors.length]);
                    id = c.getString(0);
                    db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.ID + "=? or " + Tables.Categories.PARENT_ID + "=?", new String[]{id, id});
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    /**
     * 20 - v0.6.1
     *
     * @param db Database.
     */
    public static void upgradeV10(SQLiteDatabase db)
    {
        // Update database with server_id values
        Cursor c = null;
        ContentValues values = new ContentValues();

        try
        {
            db.beginTransaction();

            // Categories
            c = db.query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.ID}, Tables.Categories.LEVEL + ">?", new String[]{"0"}, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    values.clear();
                    values.put(Tables.Categories.SERVER_ID, UUID.randomUUID().toString());
                    db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                }
                while (c.moveToNext());
            }
            db.setTransactionSuccessful();
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
            db.endTransaction();
        }
    }

    /**
     * 22 - v0.6.3
     *
     * @param db Database.
     */
    public static void upgradeV11(SQLiteDatabase db)
    {
        // Add new columns
        db.execSQL("ALTER TABLE " + Tables.Transactions.TABLE_NAME + " ADD COLUMN " + Tables.Transactions.STATE + " INTEGER DEFAULT " + Tables.Transactions.State.CONFIRMED + ";");
    }

    /**
     * 25 - v0.7
     *
     * @param context Context.
     * @param db      Database.
     */
    public static void upgradeV12(Context context, SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();

        // Currencies
        db.execSQL(Tables.Currencies.createScript());
        final long mainCurrencyId = DBDefaults.insertDefaultCurrencies(db);

        // Accounts
        db.execSQL("ALTER TABLE " + Tables.Accounts.TABLE_NAME + " ADD COLUMN " + Tables.Accounts.CURRENCY_ID + " INTEGER;");
        db.execSQL("ALTER TABLE " + Tables.Accounts.TABLE_NAME + " ADD COLUMN " + Tables.Accounts.NOTE + " TEXT;");
        db.execSQL("ALTER TABLE " + Tables.Accounts.TABLE_NAME + " ADD COLUMN " + Tables.Accounts.SHOW_IN_TOTALS + " BOOLEAN;");
        db.execSQL("ALTER TABLE " + Tables.Accounts.TABLE_NAME + " ADD COLUMN " + Tables.Accounts.SHOW_IN_SELECTION + " BOOLEAN;");
        values.put(Tables.Accounts.CURRENCY_ID, mainCurrencyId);
        values.put(Tables.Accounts.SHOW_IN_TOTALS, true);
        values.put(Tables.Accounts.SHOW_IN_SELECTION, true);
        db.update(Tables.Accounts.TABLE_NAME, values, null, null);

        // Categories
        TypedArray ta = context.getResources().obtainTypedArray(R.array.category_colors);
        //noinspection ConstantConditions
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++)
        {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        Cursor c = null;
        try
        {
            c = db.query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.T_ID}, Tables.Categories.LEVEL + "=?", new String[]{"1"}, null, null, null);
            if (c != null && c.moveToFirst())
            {
                String id;
                do
                {
                    values = new ContentValues();
                    values.put(Tables.Categories.COLOR, colors[c.getPosition() % colors.length]);
                    id = c.getString(0);
                    db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.ID + "=? or " + Tables.Categories.PARENT_ID + "=?", new String[]{id, id});
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Transactions
        db.execSQL("ALTER TABLE " + Tables.Transactions.TABLE_NAME + " ADD COLUMN " + Tables.Transactions.EXCHANGE_RATE + " REAL;");
        db.execSQL("ALTER TABLE " + Tables.Transactions.TABLE_NAME + " ADD COLUMN " + Tables.Transactions.SHOW_IN_TOTALS + " BOOLEAN;");
        values.clear();
        values.put(Tables.Transactions.EXCHANGE_RATE, 1.0);
        values.put(Tables.Transactions.SHOW_IN_TOTALS, true);
        db.update(Tables.Transactions.TABLE_NAME, values, null, null);
    }

    /**
     * 26 - v0.7.1
     *
     * @param db Database.
     */
    public static void upgradeV13(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();

        // Accounts
        values.clear();
        values.put(Tables.Accounts.DELETE_STATE, Tables.DeleteState.DELETED);
        db.update(Tables.Accounts.TABLE_NAME, values, Tables.Accounts.DELETE_STATE + "<>?", new String[]{String.valueOf(Tables.DeleteState.NONE)});

        // Categories
        values.clear();
        values.put(Tables.Categories.DELETE_STATE, Tables.DeleteState.DELETED);
        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.DELETE_STATE + "<>?", new String[]{String.valueOf(Tables.DeleteState.NONE)});

        // Transactions
        values.clear();
        values.put(Tables.Transactions.DELETE_STATE, Tables.DeleteState.DELETED);
        db.update(Tables.Transactions.TABLE_NAME, values, Tables.Transactions.DELETE_STATE + "<>?", new String[]{String.valueOf(Tables.DeleteState.NONE)});
        values.clear();
        values.put(Tables.Transactions.EXCHANGE_RATE, 1.0);
        db.update(Tables.Transactions.TABLE_NAME, values, Tables.Transactions.EXCHANGE_RATE + "=?", new String[]{"0"});
    }

    /**
     * 32 - v0.9.1
     *
     * @param db Database.
     */
    public static void upgradeV15(SQLiteDatabase db)
    {
        db.execSQL("ALTER TABLE " + Tables.Categories.TABLE_NAME + " ADD COLUMN " + Tables.Categories.ORDER + " INTEGER;");
        db.execSQL("ALTER TABLE " + Tables.Categories.TABLE_NAME + " ADD COLUMN " + Tables.Categories.PARENT_ORDER + " INTEGER;");

        updateCategoriesOrder(db);
    }

    /**
     * 33 - v0.9.2
     *
     * @param db Database.
     */
    public static void upgradeV16(SQLiteDatabase db)
    {
        updateCategoriesOrder(db);
    }

    /**
     * 34 - v0.9.3
     *
     * @param db Database.
     */
    public static void upgradeV17(SQLiteDatabase db)
    {
        updateCategoriesOrder(db);
    }

    /**
     * 35 - v0.9.4
     *
     * @param db Database.
     */
    public static void upgradeV18(SQLiteDatabase db)
    {
        final ContentValues values = new ContentValues();

        // Clear 'null' comments in accounts
        values.put(Tables.Accounts.NOTE, "");
        db.update(Tables.Accounts.TABLE_NAME, values, Tables.Accounts.NOTE + "=?", new String[]{"null"});

        // Update transactions with server id where it's empty
        Cursor c = null;
        try
        {
            db.beginTransaction();

            // Transactions
            c = db.query(Tables.Transactions.TABLE_NAME, new String[]{Tables.Transactions.ID}, "IFNULL(" + Tables.Transactions.SERVER_ID + ", '') = ''", null, null, null, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    values.clear();
                    values.put(Tables.Transactions.SERVER_ID, UUID.randomUUID().toString());
                    db.update(Tables.Transactions.TABLE_NAME, values, Tables.Transactions.ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                }
                while (c.moveToNext());
            }
            db.setTransactionSuccessful();
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
            db.endTransaction();
        }
    }

    public static void createCommonIndexes(SQLiteDatabase db)
    {
        final String[] tables = {Tables.Currencies.TABLE_NAME, Tables.Accounts.TABLE_NAME, Tables.Categories.TABLE_NAME,
                Tables.Transactions.TABLE_NAME};

        for (String tableName : tables)
        {
            createCommonIndex(db, tableName);
        }
    }

    public static void createCommonIndex(SQLiteDatabase db, String tableName)
    {
        db.execSQL("create index " + tableName + "_" + Tables.SUFFIX_SERVER_ID + "_idx on " + tableName + "(" + tableName + "_" + Tables.SUFFIX_SERVER_ID + ")");
    }
}