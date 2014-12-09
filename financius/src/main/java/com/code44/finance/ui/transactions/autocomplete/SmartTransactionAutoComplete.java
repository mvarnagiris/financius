package com.code44.finance.ui.transactions.autocomplete;

import android.database.Cursor;

import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class SmartTransactionAutoComplete extends TransactionAutoComplete {
    private List<Long> amounts = Collections.emptyList();
    private List<Account> accountsFrom = Collections.emptyList();
    private List<Account> accountsTo = Collections.emptyList();
    private List<Category> categories = Collections.emptyList();
    private List<Tag> tags = Collections.emptyList();

    protected SmartTransactionAutoComplete(Executor executor, TransactionAutoCompleteListener listener) {
        super(executor, listener);
    }

    @Override protected void autoComplete() {
        final Cursor cursor = query();
        if (cursor == null || !cursor.moveToFirst()) {
            amounts = Collections.emptyList();
            accountsFrom = Collections.emptyList();
            accountsTo = Collections.emptyList();
            categories = Collections.emptyList();
            tags = Collections.emptyList();
            return;
        }

        do {
            final Transaction transaction = Transaction.from(cursor);

        } while (cursor.moveToNext());
    }

    @Override protected List<Long> getAmounts() {
        return amounts;
    }

    @Override protected List<Account> getAccountsFrom() {
        return accountsFrom;
    }

    @Override protected List<Account> getAccountsTo() {
        return accountsTo;
    }

    @Override protected List<Category> getCategories() {
        return categories;
    }

    @Override protected List<Tag> getTags() {
        return tags;
    }

    private Cursor query() {
        // TODO Implement.
        return null;
    }
}
