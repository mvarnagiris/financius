package com.code44.finance.ui.transactions.edit;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.google.common.base.Strings;

import java.util.List;

class TagsFinder extends Finder<List<Tag>> {
    private final Category category;

    public TagsFinder(@NonNull Context context, @NonNull AutoCompleteInput autoCompleteInput, @Nullable Category category) {
        super(context, autoCompleteInput);
        this.category = category;
    }

    @Override protected Cursor queryTransactions(AutoCompleteInput input) {
        final Query query = getBaseQuery();

        if (!Strings.isNullOrEmpty(input.getNote())) {
            query.selection(" and " + Tables.Transactions.NOTE + "=?", input.getNote());
        }

        if (input.getCategory() != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", input.getCategory().getId());
        } else if (category != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", category.getId());
        }

        if (input.getAccountFrom() != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_FROM_ID + "=?", input.getAccountFrom().getId());
        }

        if (input.getAccountTo() != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_TO_ID + "=?", input.getAccountTo().getId());
        }

        return executeQuery(query);
    }

    @Override protected FinderScore createScore(AutoCompleteInput autoCompleteInput) {
        return new Score(autoCompleteInput.getDate(), autoCompleteInput.getAmount());
    }

    @Override protected boolean isValidTransaction(Transaction transaction) {
        return true;
    }

    @Override protected List<Tag> getModelForTransaction(Transaction transaction) {
        return transaction.getTags();
    }
}
