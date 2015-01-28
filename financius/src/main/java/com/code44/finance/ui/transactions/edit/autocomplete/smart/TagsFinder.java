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

import java.util.List;

public class TagsFinder extends Finder<List<Tag>> {
    private final Category category;

    protected TagsFinder(Context context, AutoCompleteInput autoCompleteInput, boolean log, Category category) {
        super(context, autoCompleteInput, log);
        this.category = category;
    }

    @Override protected Cursor queryTransactions(AutoCompleteInput input) {
        final Query query = getBaseQuery();

        if (!Strings.isEmpty(input.getNote())) {
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
        return transaction.getTags() != null && !transaction.getTags().isEmpty();
    }

    @Override protected List<Tag> getModelForTransaction(Transaction transaction) {
        return transaction.getTags();
    }

    @Override protected String getLogName(List<Tag> model) {
        StringBuilder sb = new StringBuilder();
        for (Tag tag : model) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(tag.getTitle());
        }
        return sb.toString();
    }
}
