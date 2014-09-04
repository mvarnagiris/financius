package com.code44.finance.data.backup;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BackupDataImporter extends FileDataImporter {
    private final Context context;
    private final DBHelper dbHelper;

    public BackupDataImporter(Context context, DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override public void importData(File file) throws Exception {
        final JsonObject json = fileToJson(file);
        final int backupVersion = validateAndGetBackupVersion(json);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            database.beginTransaction();

            cleanDatabase();

            importCurrencies(json, backupVersion);
            importCategories(json, backupVersion);
            importTags(json, backupVersion);
            importAccounts(json, backupVersion);
            importTransactions(json, backupVersion);

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private JsonObject fileToJson(File file) {
        return null;
    }

    private int validateAndGetBackupVersion(JsonObject json) throws Exception {
        return 0;
    }

    private void cleanDatabase() {

    }

    private void importCurrencies(JsonObject json, int backupVersion) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("currencies");
        final Currency model = new Currency();
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            model.setCode(modelJson.get("code").getAsString());
            model.setSymbol(modelJson.get("symbol").getAsString());
            model.setSymbolPosition(SymbolPosition.fromInt(modelJson.get("symbol_position").getAsInt()));
            model.setDecimalSeparator(DecimalSeparator.fromSymbol(modelJson.get("decimal_separator").getAsString()));
            model.setGroupSeparator(GroupSeparator.fromSymbol(modelJson.get("group_separator").getAsString()));
            model.setDecimalCount(modelJson.get("decimal_count").getAsInt());
            model.setDefault(modelJson.get("is_default").getAsBoolean());
            model.setExchangeRate(modelJson.get("exchange_rate").getAsDouble());
            valuesList.add(model.asValues());
        }
        insert(valuesList, CurrenciesProvider.uriCurrencies());
    }

    private void importCategories(JsonObject json, int backupVersion) {

    }

    private void importTags(JsonObject json, int backupVersion) {

    }

    private void importAccounts(JsonObject json, int backupVersion) {

    }

    private void importTransactions(JsonObject json, int backupVersion) {

    }

    private void updateBaseModel(BaseModel model, JsonObject json) {
        model.setId(json.get("id").getAsString());
        model.setModelState(ModelState.fromInt(json.get("model_state").getAsInt()));
        model.setSyncState(SyncState.fromInt(json.get("sync_state").getAsInt()));
    }

    private void insert(List<ContentValues> valuesList, Uri uri) {
        DataStore.bulkInsert().values(valuesList).into(context, uri);
    }
}
