package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;
import android.support.v4.util.Pair;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.TransactionAutoComplete;

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

        final Pair<Category, List<Category>> categoriesResult = new CategoriesFinder(context, input, log).find();
        result.setCategory(categoriesResult.first);
        result.setOtherCategories(categoriesResult.second);

        final Pair<List<Tag>, List<List<Tag>>> tagsResult = new TagsFinder(context, input, log, categoriesResult.first).find();
        result.setTags(tagsResult.first);
        // TODO Set other tags

        if (input.getTransactionType() != TransactionType.Income) {
            final Pair<Account, List<Account>> accountsResult = new AccountsFromFinder(context, input, log, categoriesResult.first).find();
            result.setAccountFrom(accountsResult.first);
            result.setOtherAccountsFrom(accountsResult.second);
        }

        if (input.getTransactionType() != TransactionType.Expense) {
            final Pair<Account, List<Account>> accountsResult = new AccountsToFinder(context, input, log, categoriesResult.first).find();
            result.setAccountTo(accountsResult.first);
            result.setOtherAccountsTo(accountsResult.second);
        }

        if (input.getAmount() == 0) {
            final Pair<Long, List<Long>> amountsResult = new AmountsFinder(context, input, log, categoriesResult.first).find();
            result.setAmount(amountsResult.first);
            result.setOtherAmounts(amountsResult.second);
        }


        return result;
    }
}
