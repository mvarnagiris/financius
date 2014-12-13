package com.code44.finance.ui.transactions.autocomplete;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

public class SmartTransactionAutoComplete extends TransactionAutoComplete {
    private final Context context;
    private final List<Long> resultAmounts = new ArrayList<>();
    private final List<Account> resultAccountsFrom = new ArrayList<>();
    private final List<Account> resultAccountsTo = new ArrayList<>();
    private final List<Category> resultCategories = new ArrayList<>();
    private final List<Tag> resultTags = new ArrayList<>();

    protected SmartTransactionAutoComplete(Context context, Executor executor, TransactionAutoCompleteListener listener) {
        super(executor, listener);
        this.context = context.getApplicationContext();
    }

    @Override protected void autoComplete() {
        resultAmounts.clear();
        resultAccountsFrom.clear();
        resultAccountsTo.clear();
        resultCategories.clear();
        resultTags.clear();

        final Cursor cursor = query();
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }

        final TreeMap<Transaction, Float>
        do {
            final Transaction transaction = Transaction.from(cursor);


        } while (cursor.moveToNext());


        if (category == null && transactionType != TransactionType.Transfer) {
            findCategories(cursor);
        }

        if (tags == null) {
            findTags(cursor);
        }

        if (accountFrom == null && transactionType != TransactionType.Income) {
            findAccountsFrom(cursor);
        }

        if (accountTo == null && transactionType != TransactionType.Expense) {
            findAccountsTo(cursor);
        }

        if (amount == null) {
            findAmounts(cursor);
        }

        IOUtils.closeQuietly(cursor);
    }

    @Override protected List<Long> getAmounts() {
        return resultAmounts;
    }

    @Override protected List<Account> getAccountsFrom() {
        return resultAccountsFrom;
    }

    @Override protected List<Account> getAccountsTo() {
        return resultAccountsTo;
    }

    @Override protected List<Category> getCategories() {
        return resultCategories;
    }

    @Override protected List<Tag> getTags() {
        return resultTags;
    }

    private void findCategories(Cursor cursor) {
        cursor.moveToFirst();
        final Map<Category, Float> categories = new HashMap<>();
        do {
            final Transaction transaction = Transaction.from(cursor);


        } while (cursor.moveToNext());
    }

    private void findTags(Cursor cursor) {
        cursor.moveToFirst();
    }

    private void findAccountsFrom(Cursor cursor) {
        cursor.moveToFirst();
    }

    private void findAccountsTo(Cursor cursor) {
        cursor.moveToFirst();
    }

    private void findAmounts(Cursor cursor) {
        cursor.moveToFirst();
    }

    private Cursor query() {
        final boolean useNoteAsTemplate = true;
        final boolean queryOnlyTemplates = useNoteAsTemplate && !Strings.isEmpty(note);

        if (queryOnlyTemplates) {
            final Cursor cursor = queryTemplates();
            if (cursor != null) {
                return cursor;
            }
        }

        // Filter date
        final long fromDate = System.currentTimeMillis() - (DateUtils.DAY_IN_MILLIS * 90);
        final Query query = Tables.Transactions.getQuery()
                .selection(" and " + Tables.Transactions.DATE + ">?", String.valueOf(fromDate));

        return query.from(context, TransactionsProvider.uriTransactions()).execute();
    }

    private Cursor queryTemplates() {
        final Cursor cursor = getBaseQuery()
                .selection(" and " + Tables.Transactions.NOTE + "=?", note)
                .limit(1)
                .from(context, TransactionsProvider.uriTransactions())
                .execute();

        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            IOUtils.closeQuietly(cursor);
        }

        return null;
    }

    private Query getBaseQuery() {
        return Tables.Transactions.getQuery()
                .selection(" and " + Tables.Transactions.TYPE + "=?", transactionType.asString());
    }
}
