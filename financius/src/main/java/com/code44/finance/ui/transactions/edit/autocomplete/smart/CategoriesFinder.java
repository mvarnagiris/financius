package com.code44.finance.ui.transactions.edit.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteInput;
import com.code44.finance.ui.transactions.edit.autocomplete.Finder;
import com.code44.finance.ui.transactions.edit.autocomplete.FinderScore;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFinder extends Finder<Category> {
    protected CategoriesFinder(Context context, AutoCompleteInput autoCompleteInput, boolean log) {
        super(context, autoCompleteInput, log);
    }

    @Override protected Cursor queryTransactions(AutoCompleteInput input) {
        final Query query = getBaseQuery();

        if (!Strings.isEmpty(input.getNote())) {
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

    @Override protected String getLogName(Category model) {
        return model.getTitle();
    }
}
