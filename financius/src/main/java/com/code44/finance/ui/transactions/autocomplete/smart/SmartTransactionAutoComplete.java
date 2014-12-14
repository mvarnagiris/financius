package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;
import android.support.v4.util.Pair;

import com.code44.finance.data.model.Category;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.TransactionAutoComplete;

import java.util.List;
import java.util.concurrent.Executor;

public class SmartTransactionAutoComplete extends TransactionAutoComplete {
    private final Context context;

    public SmartTransactionAutoComplete(Context context, Executor executor, TransactionAutoCompleteListener listener, AutoCompleteInput autoCompleteInput) {
        super(executor, listener, autoCompleteInput);
        this.context = context.getApplicationContext();
    }

    @Override protected AutoCompleteResult autoComplete(AutoCompleteInput input) {
        final AutoCompleteResult result = new AutoCompleteResult();
        final Pair<Category, List<Category>> categoriesResult = new CategoriesFinder(context, input).find();

        result.setCategory(categoriesResult.first);
        result.setOtherCategories(categoriesResult.second);

        return result;
    }
}
