package com.code44.finance.ui.transactions.edit;

import android.content.Context;
import android.support.annotation.NonNull;

import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

class TransactionAutoComplete implements Callable<AutoCompleteResult> {
    private final Context context;
    private final AutoCompleteInput autoCompleteInput;

    public TransactionAutoComplete(@NonNull Context context, @NonNull AutoCompleteInput autoCompleteInput) {
        checkNotNull(context, "Context cannot be null.");
        this.context = context.getApplicationContext();
        this.autoCompleteInput = checkNotNull(autoCompleteInput, "AutoCompleteInput cannot be null.");
    }

    @Override public AutoCompleteResult call() throws Exception {
        final List<Category> categories = new CategoriesFinder(context, autoCompleteInput).find();
        final Category categoryBestMatch = categories.isEmpty() ? null : categories.get(0);

        final List<Account> accountsFrom = new AccountsFromFinder(context, autoCompleteInput, categoryBestMatch).find();
        final List<Account> accountsTo = new AccountsToFinder(context, autoCompleteInput, categoryBestMatch).find();
        final List<List<Tag>> tags = new TagsFinder(context, autoCompleteInput, categoryBestMatch).find();

        return new AutoCompleteResult(accountsFrom, accountsTo, categories, tags);
    }
}
