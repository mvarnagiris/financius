package com.code44.finance.data.backup;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CsvDataExporter extends DataExporter {
    private static final String SEPARATOR = ";";
    private static final String TAG_SEPARATOR = ",";

    private final Context context;

    public CsvDataExporter(OutputStream outputStream, Context context) {
        super(outputStream);
        this.context = context;
    }

    @Override public void exportData(OutputStream outputStream) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        final Cursor cursor = Tables.Transactions.getQuery().from(context, TransactionsProvider.uriTransactions()).execute();
        try {
            if (cursor.moveToFirst()) {
                writeCsv(writer, cursor);
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }

        writer.close();
    }

    private void writeCsv(BufferedWriter writer, Cursor cursor) throws IOException {
        // 29 September 2014; 09:44; Expense; Confirmed; Account from; Account to; Category; Tag1, Tag2;
        final StringBuilder outputLine = new StringBuilder();
        do {
            final Transaction transaction = Transaction.from(cursor);
            final DateTime dateTime = new DateTime(transaction.getDate());
            outputLine.setLength(0);
            outputLine.append(DateUtils.formatDateTime(context, dateTime, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
            outputLine.append(SEPARATOR).append(DateUtils.formatDateTime(context, dateTime, DateUtils.FORMAT_SHOW_TIME));
            outputLine.append(SEPARATOR).append(transaction.getTransactionType());
            outputLine.append(SEPARATOR).append(transaction.getTransactionState());
            outputLine.append(SEPARATOR).append(transaction.getNote());
            outputLine.append(SEPARATOR).append(getAccountFrom(transaction));
            outputLine.append(SEPARATOR).append(getAccountTo(transaction));
            outputLine.append(SEPARATOR).append(getCategory(transaction));
            outputLine.append(SEPARATOR).append(getTags(transaction));
            outputLine.append(SEPARATOR).append(transaction.getAmount());
            outputLine.append(SEPARATOR).append(getCurrencyCode(transaction));
            outputLine.append(SEPARATOR).append(transaction.getExchangeRate());
            writer.write(outputLine.toString());
            writer.newLine();

        } while (cursor.moveToNext());
    }

    private String getAccountFrom(Transaction transaction) {
        final Account account = transaction.getAccountFrom();
        return account != null && account.hasId() ? account.getTitle() : "";
    }

    private String getAccountTo(Transaction transaction) {
        final Account account = transaction.getAccountTo();
        return account != null && account.hasId() ? account.getTitle() : "";
    }

    private String getCategory(Transaction transaction) {
        final Category category = transaction.getCategory();
        return category != null && category.hasId() ? category.getTitle() : "";
    }

    private String getTags(Transaction transaction) {
        if (transaction.getTags().size() > 0) {
            final StringBuilder tags = new StringBuilder();
            for (Tag tag : transaction.getTags()) {
                if (tags.length() > 0) {
                    tags.append(TAG_SEPARATOR);
                }
                tags.append(tag.getTitle());
            }
            return tags.toString();
        }
        return "";
    }

    private String getCurrencyCode(Transaction transaction) {
        if (transaction.getTransactionType() == TransactionType.Income) {
            return transaction.getAccountTo().getCurrency().getCode();
        } else {
            return transaction.getAccountFrom().getCurrency().getCode();
        }
    }
}
