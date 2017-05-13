package com.code44.finance.ui.transactions.edit;

import android.content.Context;
import android.support.annotation.NonNull;

import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.utils.DataLoader;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class AutoCompleteLoader extends DataLoader<AutoCompleteResult> {
    private final AutoCompleteInput autoCompleteInput;

    public AutoCompleteLoader(@NonNull Context context, @NonNull AutoCompleteInput autoCompleteInput) {
        super(context.getApplicationContext());
        this.autoCompleteInput = checkNotNull(autoCompleteInput, "AutoCompleteInput cannot be null.");
    }

    @Override public AutoCompleteResult loadInBackground() {
        try {
            return new TransactionAutoComplete(getContext(), autoCompleteInput).call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AutoCompleteResult(Collections.<Account>emptyList(), Collections.<Account>emptyList(), Collections.<Category>emptyList(), Collections
                .<List<Tag>>emptyList());
    }
}
