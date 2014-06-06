package com.code44.finance.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.code44.finance.db.model.Account;
import com.code44.finance.db.model.Category;
import com.code44.finance.db.model.Currency;
import com.code44.finance.db.model.Transaction;

import nl.qbusict.cupboard.CupboardFactory;

public class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "financius.db";
    private static final int VERSION = 1;

    private static DBHelper singleton;

    // Register models
    static {
        CupboardFactory.cupboard().register(Currency.class);
        CupboardFactory.cupboard().register(Account.class);
        CupboardFactory.cupboard().register(Category.class);
        CupboardFactory.cupboard().register(Transaction.class);
    }

    private DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public static synchronized DBHelper get(Context context) {
        if (singleton == null)
            singleton = new DBHelper(context);
        return singleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CupboardFactory.cupboard().withDatabase(db).createTables();
        DBDefaults.addDefaults(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CupboardFactory.cupboard().withDatabase(db).upgradeTables();
    }
}
