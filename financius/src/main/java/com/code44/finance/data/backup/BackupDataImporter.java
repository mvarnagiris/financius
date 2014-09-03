package com.code44.finance.data.backup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.data.db.DBHelper;
import com.google.gson.JsonObject;

import java.io.File;

public class BackupDataImporter extends FileDataImporter {
    private final Context context;
    private final DBHelper dbHelper;

    public BackupDataImporter(Context context, DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override public void importData(File file) throws Exception {
        final JsonObject json = fileToJson(file);
        validate(json);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.beginTransaction();

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private JsonObject fileToJson(File file) {
        return null;
    }

    private void validate(JsonObject json) throws Exception {

    }

    private void importCurrencies(JsonObject json) {

    }
}
