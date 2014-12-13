package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;

import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.transactions.autocomplete.TransactionAutoComplete;

import java.util.ArrayList;
import java.util.List;
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

        resultCategories.addAll(new CategoriesFinder(context, getTransactionType(), getDate(), getAccountFrom(), getAccountTo(), getCategory(), getTags(), getNote()).find());
    }

    @Override protected List<Long> getResultAmounts() {
        return resultAmounts;
    }

    @Override protected List<Account> getResultAccountsFrom() {
        return resultAccountsFrom;
    }

    @Override protected List<Account> getResultAccountsTo() {
        return resultAccountsTo;
    }

    @Override protected List<Category> getResultCategories() {
        return resultCategories;
    }

    @Override protected List<Tag> getResultTags() {
        return resultTags;
    }
}
