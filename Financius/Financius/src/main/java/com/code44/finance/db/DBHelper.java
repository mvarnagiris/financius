package com.code44.finance.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String NAME = "finance.db";
    private static final int VERSION = 18;
    // -----------------------------------------------------------------------------------------------------------------
    private static DBHelper singleton = null;
    // -----------------------------------------------------------------------------------------------------------------
    private Context context;

    private DBHelper(Context context)
    {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    public static DBHelper get(Context context)
    {
        if (singleton == null)
            singleton = new DBHelper(context);
        return singleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL(Tables.Currencies.createScript());
            db.execSQL(Tables.Accounts.createScript());
            db.execSQL(Tables.Categories.createScript());
            db.execSQL(Tables.Transactions.createScript());

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
                DBUpgrade.upgradeV7(db);
            case 7: // 16 - v0.5.2
                DBUpgrade.upgradeV8(db);
            case 8: // 19 - v0.6
                DBUpgrade.upgradeV9(context, db);
            case 9: // 20 - v0.6.1
                DBUpgrade.upgradeV10(db);
            case 10: // 22 - v0.6.3
                DBUpgrade.upgradeV11(db);
            case 11: // 25 - v0.7
                DBUpgrade.upgradeV12(context, db);
            case 12: // 26 - v0.7.1
                DBUpgrade.upgradeV13(db);
            case 13: // 32 - v0.9.0
            case 14: // 32 - v0.9.1
                DBUpgrade.upgradeV15(db);
            case 15: // 33 - v0.9.2
                DBUpgrade.upgradeV16(db);
            case 16: // 34 - v0.9.3
                DBUpgrade.upgradeV17(db);
            case 17: // 35 - v0.9.4
                DBUpgrade.upgradeV18(db);
        }
    }
}