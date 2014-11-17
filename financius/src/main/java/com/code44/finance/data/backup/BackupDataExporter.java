package com.code44.finance.data.backup;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class BackupDataExporter extends DataExporter {
    public static final int VERSION = 7;

    private static final String CHARSET_NAME = "UTF-8";

    private final Context context;

    public BackupDataExporter(OutputStream outputStream, Context context) {
        super(outputStream);
        this.context = context;
    }

    @Override public void exportData(OutputStream outputStream) throws Exception {
        final JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, CHARSET_NAME));
        writer.setIndent("  ");

        writer.beginObject();

        writeMetaData(writer);

        writer.name("currencies").beginArray();
        writeCurrencies(writer);
        writer.endArray();

        writer.name("categories").beginArray();
        writeCategories(writer);
        writer.endArray();

        writer.name("tags").beginArray();
        writeTags(writer);
        writer.endArray();

        writer.name("accounts").beginArray();
        writeAccounts(writer);
        writer.endArray();

        writer.name("transactions").beginArray();
        writeTransactions(writer);
        writer.endArray();

        writer.endObject();
        writer.close();
    }

    private void writeMetaData(JsonWriter writer) throws IOException {
        writer.name("version").value(VERSION);
        writer.name("timestamp").value(System.currentTimeMillis());
    }

    private void writeCurrencies(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(CurrenciesProvider.uriCurrencies(), Tables.Currencies.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final Currency currency = new Currency();
                do {
                    currency.updateFrom(cursor, null);

                    writer.beginObject();
                    writeBaseModel(currency, writer);
                    writer.name("code").value(currency.getCode());
                    writer.name("symbol").value(currency.getSymbol());
                    writer.name("symbol_position").value(currency.getSymbolPosition().asInt());
                    writer.name("decimal_separator").value(currency.getDecimalSeparator().symbol());
                    writer.name("group_separator").value(currency.getGroupSeparator().symbol());
                    writer.name("decimal_count").value(currency.getDecimalCount());
                    writer.name("is_default").value(currency.isDefault());
                    writer.name("exchange_rate").value(currency.getExchangeRate());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeCategories(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(CategoriesProvider.uriCategories(), Tables.Categories.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final Category category = new Category();
                do {
                    category.updateFrom(cursor, null);

                    writer.beginObject();
                    writeBaseModel(category, writer);
                    writer.name("title").value(category.getTitle());
                    writer.name("color").value(category.getColor());
                    writer.name("transaction_type").value(category.getTransactionType().asInt());
                    writer.name("sort_order").value(category.getSortOrder());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeTags(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(TagsProvider.uriTags(), Tables.Tags.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final Tag tag = new Tag();
                do {
                    tag.updateFrom(cursor, null);

                    writer.beginObject();
                    writeBaseModel(tag, writer);
                    writer.name("title").value(tag.getTitle());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeAccounts(JsonWriter writer) throws IOException {
        final Cursor cursor = getCursor(AccountsProvider.uriAccounts(), Tables.Accounts.PROJECTION);
        try {
            if (cursor.moveToFirst()) {
                final Account account = new Account();
                do {
                    account.updateFrom(cursor, null);

                    writer.beginObject();
                    writeBaseModel(account, writer);
                    writer.name("currency_id").value(account.getCurrency().getId());
                    writer.name("title").value(account.getTitle());
                    writer.name("note").value(account.getNote());
                    writer.name("balance").value(account.getBalance());
                    writer.name("include_in_totals").value(account.includeInTotals());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private void writeTransactions(JsonWriter writer) throws IOException {
        final Cursor cursor = Tables.Transactions.getQuery().clearSelection().clearArgs().selection("1=1").from(context, TransactionsProvider.uriTransactions()).execute();
        try {
            if (cursor.moveToFirst()) {
                final Transaction transaction = new Transaction();
                do {
                    transaction.updateFrom(cursor, null);
                    transaction.prepareForDb();

                    writer.beginObject();
                    writeBaseModel(transaction, writer);
                    writer.name("account_from_id").value(transaction.getAccountFrom() != null ? transaction.getAccountFrom().getId() : null);
                    writer.name("account_to_id").value(transaction.getAccountTo() != null ? transaction.getAccountTo().getId() : null);
                    writer.name("category_id").value(transaction.getCategory() != null ? transaction.getCategory().getId() : null);
                    writer.name("tag_ids").beginArray();
                    for (Tag tag : transaction.getTags()) {
                        writer.value(tag.getId());
                    }
                    writer.endArray();
                    writer.name("date").value(transaction.getDate());
                    writer.name("amount").value(transaction.getAmount());
                    writer.name("exchange_rate").value(transaction.getExchangeRate());
                    writer.name("note").value(transaction.getNote());
                    writer.name("transaction_state").value(transaction.getTransactionState().asInt());
                    writer.name("transaction_type").value(transaction.getTransactionType().asInt());
                    writer.name("include_in_reports").value(transaction.includeInReports());
                    writer.endObject();
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    private Cursor getCursor(Uri uri, String... projection) {
        return Query.create().projection(projection).from(context, uri).execute();
    }

    private void writeBaseModel(Model model, JsonWriter writer) throws IOException {
        writer.name("id").value(model.getId());
        writer.name("model_state").value(model.getModelState().asInt());
        writer.name("sync_state").value(model.getSyncState().asInt());
    }
}
