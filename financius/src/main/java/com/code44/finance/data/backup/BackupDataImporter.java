package com.code44.finance.data.backup;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BackupDataImporter extends FileDataImporter {
    private static final int MIN_VALID_VERSION = 6;

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

            cleanDatabase(database);

            importCurrencies(json);
            importCategories(json);
            importTags(json);
            importAccounts(json);
            importTransactions(json);

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private JsonObject fileToJson(File file) throws FileNotFoundException {
        final JsonParser parser = new JsonParser();
        final JsonElement jsonElement = parser.parse(new FileReader(file));
        return jsonElement.getAsJsonObject();
    }

    private void validate(JsonObject json) throws Exception {
        final int version = json.get("version").getAsInt();
        if (version < MIN_VALID_VERSION) {
            throw new IllegalArgumentException("Backup version " + version + " is not supported anymore.");
        }
    }

    private void cleanDatabase(SQLiteDatabase database) {
        database.delete(Tables.Currencies.TABLE_NAME, null, null);
        database.delete(Tables.Categories.TABLE_NAME, null, null);
        database.delete(Tables.Tags.TABLE_NAME, null, null);
        database.delete(Tables.Accounts.TABLE_NAME, null, null);
        database.delete(Tables.Transactions.TABLE_NAME, null, null);
        database.delete(Tables.TransactionTags.TABLE_NAME, null, null);
    }

    private void importCurrencies(JsonObject json) {
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

    private void importCategories(JsonObject json) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("categories");
        final Category model = new Category();
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            model.setTitle(modelJson.get("title").getAsString());
            model.setColor(modelJson.get("color").getAsInt());
            model.setTransactionType(TransactionType.fromInt(modelJson.get("transaction_type").getAsInt()));
            model.setSortOrder(modelJson.get("sort_order").getAsInt());
            valuesList.add(model.asValues());
        }
        insert(valuesList, CategoriesProvider.uriCategories());
    }

    private void importTags(JsonObject json) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("tags");
        final Tag model = new Tag();
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            model.setTitle(modelJson.get("title").getAsString());
            valuesList.add(model.asValues());
        }
        insert(valuesList, TagsProvider.uriTags());
    }

    private void importAccounts(JsonObject json) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("accounts");
        final Account model = new Account();
        final Currency currency = new Currency();
        model.setCurrency(currency);
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            currency.setId(modelJson.get("currency_id").getAsString());
            model.setTitle(modelJson.get("title").getAsString());
            model.setNote(modelJson.get("note").getAsString());
            model.setBalance(modelJson.get("balance").getAsLong());
            model.setIncludeInTotals(modelJson.get("include_in_totals").getAsBoolean());
            valuesList.add(model.asValues());
        }
        insert(valuesList, AccountsProvider.uriAccounts());
    }

    private void importTransactions(JsonObject json) {
        final List<ContentValues> valuesList = new ArrayList<>();
        final JsonArray modelsJson = json.getAsJsonArray("transactions");
        final Transaction model = new Transaction();
        final Account accountFrom = new Account();
        final Account accountTo = new Account();
        final Category category = new Category();
        final List<Tag> tags = new ArrayList<>();
        final Set<Tag> tagCache = new HashSet<>();
        model.setAccountFrom(accountFrom);
        model.setAccountTo(accountTo);
        model.setCategory(category);
        model.setTags(tags);
        for (int i = 0, size = modelsJson.size(); i < size; i++) {
            final JsonObject modelJson = modelsJson.get(i).getAsJsonObject();
            updateBaseModel(model, modelJson);
            accountFrom.setId(modelJson.get("account_from_id").getAsString());
            accountTo.setId(modelJson.get("account_to_id").getAsString());
            category.setId(modelJson.get("category_id").getAsString());
            tagCache.addAll(tags);
            tags.clear();
            final JsonArray tagsJson = modelJson.get("tag_ids").getAsJsonArray();
            for (int tagI = 0, tagSize = tagsJson.size(); tagI < tagSize; tagI++) {
                final Tag tag = getTagInstance(tagCache);
                tag.setId(tagsJson.get(tagI).getAsString());
                tags.add(tag);
            }
            model.setDate(modelJson.get("date").getAsLong());
            model.setAmount(modelJson.get("amount").getAsLong());
            model.setExchangeRate(modelJson.get("exchange_rate").getAsDouble());
            model.setNote(modelJson.get("note").getAsString());
            model.setTransactionState(TransactionState.fromInt(modelJson.get("transaction_state").getAsInt()));
            model.setTransactionType(TransactionType.fromInt(modelJson.get("transaction_type").getAsInt()));
            model.setIncludeInReports(modelJson.get("include_in_reports").getAsBoolean());
            valuesList.add(model.asValues());
        }
        insert(valuesList, TransactionsProvider.uriTransactions());
    }

    private Tag getTagInstance(Set<Tag> tagCache) {
        if (tagCache.size() > 0) {
            final Iterator<Tag> iterator = tagCache.iterator();
            final Tag tag = iterator.next();
            iterator.remove();
            return tag;
        } else {
            return new Tag();
        }
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
