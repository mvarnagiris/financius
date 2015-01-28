package com.code44.finance.ui.transactions.edit.autocomplete.smart;

import android.content.Context;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.edit.autocomplete.TransactionAutoComplete;

import java.util.List;
import java.util.concurrent.Executor;

public class SmartTransactionAutoComplete extends TransactionAutoComplete {
    private final Context context;
    private final boolean log;

    public SmartTransactionAutoComplete(Context context, Executor executor, TransactionAutoCompleteListener listener, AutoCompleteInput autoCompleteInput, boolean log) {
        super(executor, listener, autoCompleteInput);
        this.context = context.getApplicationContext();
        this.log = log;
    }

    @Override protected AutoCompleteResult autoComplete(AutoCompleteInput input) {
        final AutoCompleteResult result = new AutoCompleteResult();

        final List<Category> categoriesResult = new CategoriesFinder(context, input, log).find();
        result.setCategories(categoriesResult);
        final Category categoryBestMatch = categoriesResult.isEmpty() ? null : categoriesResult.get(0);

        final List<List<Tag>> tagsResult = new TagsFinder(context, input, log, categoryBestMatch).find();
        result.setTags(tagsResult);

        if (input.getTransactionType() != TransactionType.Income) {
            final List<Account> accountsResult = new AccountsFromFinder(context, input, log, categoryBestMatch).find();
            result.setAccountsFrom(accountsResult);
        }

        if (input.getTransactionType() != TransactionType.Expense) {
            final List<Account> accountsResult = new AccountsToFinder(context, input, log, categoryBestMatch).find();
            result.setAccountsTo(accountsResult);
        }

        if (input.getAmount() == 0) {
            final List<Long> amountsResult = new AmountsFinder(context, input, log, categoryBestMatch).find();
            result.setAmounts(amountsResult);
        }

        if (Strings.isEmpty(input.getNote())) {
            final List<String> notesResult = new NotesFinder(context, input, log, categoryBestMatch, tagsResult.isEmpty() ? null : tagsResult.get(0)).find();
            result.setNotes(notesResult);
        }

        return result;
    }
}
