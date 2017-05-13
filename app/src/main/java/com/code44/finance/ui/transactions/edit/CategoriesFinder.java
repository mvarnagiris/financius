package com.code44.finance.ui.transactions.edit;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

class CategoriesFinder extends Finder<Category> {
    public CategoriesFinder(@NonNull Context context, @NonNull AutoCompleteInput autoCompleteInput) {
        super(context, autoCompleteInput);
    }

    @Override protected Cursor queryTransactions(AutoCompleteInput input) {
        final Query query = getBaseQuery();

        if (!Strings.isNullOrEmpty(input.getNote())) {
            query.selection(" and " + Tables.Transactions.NOTE + "=?", input.getNote());
        }

        if (input.getTags() != null && input.getTags().size() > 0) {
            final List<String> tagIds = new ArrayList<>();
            for (Tag tag : input.getTags()) {
                tagIds.add(tag.getId());
            }
            query.selection(" and ").selectionInClause(Tables.TransactionTags.TAG_ID.getName(), tagIds);
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
        return transaction.getCategory() != null;
    }

    @Override protected Category getModelForTransaction(Transaction transaction) {
        return transaction.getCategory();
    }
}
