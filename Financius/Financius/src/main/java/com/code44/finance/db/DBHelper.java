package com.code44.finance.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database creation and upgrades.
 *
 * @author Mantas Varnagiris
 */
public class DBHelper extends SQLiteOpenHelper
{
    private static final String NAME = "finance.db";
    private static final int VERSION = 18;
    private static DBHelper instance = null;
    private Context context;

    private DBHelper(Context context)
    {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    /**
     * Uses single instance to have only one connection to database.
     *
     * @param context Context.
     * @return Database connection.
     */
    public static DBHelper getInstance(Context context)
    {
        if (instance == null)
            instance = new DBHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL(Tables.Currencies.CREATE_SCRIPT);
            db.execSQL(Tables.Accounts.CREATE_SCRIPT);
            db.execSQL(Tables.Categories.CREATE_SCRIPT);
            db.execSQL(Tables.Transactions.CREATE_SCRIPT);
            db.execSQL(Tables.Budgets.CREATE_SCRIPT);
            db.execSQL(Tables.BudgetCategories.CREATE_SCRIPT);

            DBDefaults.insertDefaults(context, db);

            DBUpgrade.createCommonIndexes(db);
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        switch (oldVersion)
        {
            case 6: // 11 - v0.5
                DBUpgrade.upgradeV7(context, db);
            case 7: // 16 - v0.5.2
                DBUpgrade.upgradeV8(context, db);
            case 8: // 19 - v0.6
                DBUpgrade.upgradeV9(context, db);
            case 9: // 20 - v0.6.1
                DBUpgrade.upgradeV10(context, db);
            case 10: // 22 - v0.6.3
                DBUpgrade.upgradeV11(context, db);
            case 11: // 25 - v0.7
                DBUpgrade.upgradeV12(context, db);
            case 12: // 26 - v0.7.1
                DBUpgrade.upgradeV13(context, db);
            case 13: // 32 - v0.9.0
            case 14: // 32 - v0.9.1
                DBUpgrade.upgradeV15(context, db);
            case 15: // 33 - v0.9.2
                DBUpgrade.upgradeV16(context, db);
            case 16: // 34 - v0.9.3
                DBUpgrade.upgradeV17(context, db);
            case 17: // 35 - v0.9.4
                DBUpgrade.upgradeV18(context, db);
        }
    }
}