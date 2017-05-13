package com.code44.finance.money.grouping;

import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.money.AmountRetriever;
import com.code44.finance.money.CurrenciesManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TagAmountGroups extends AmountGroups<TagGroup> {
    private final CurrenciesManager currenciesManager;
    private final String currencyCode;
    private final TransactionValidator transactionValidator;

    public TagAmountGroups(CurrenciesManager currenciesManager, String currencyCode, TransactionValidator transactionValidator) {
        this.currenciesManager = currenciesManager;
        this.currencyCode = currencyCode;
        this.transactionValidator = transactionValidator;
    }

    @Override protected int getGroupCount(Transaction transaction) {
        final List<Tag> tags = transaction.getTags();
        return tags == null || tags.size() == 0 ? 1 : tags.size();
    }

    @Override protected Long getGroupId(Transaction transaction, int groupPosition) {
        if (transactionValidator != null && !transactionValidator.isTransactionValid(transaction)) {
            return null;
        }

        final List<Tag> tags = transaction.getTags();
        return tags == null || tags.size() == 0 ? 0L : tags.get(groupPosition).getId().hashCode();
    }

    @Override protected TagGroup createAmountGroup(Transaction transaction, int groupPosition) {
        final List<Tag> tags = transaction.getTags();
        final Tag tag = tags == null || tags.size() == 0 ? null : tags.get(groupPosition);
        return new TagGroup(tag);
    }

    @Override protected long getAmount(TagGroup amountGroup, Transaction transaction) {
        return AmountRetriever.getAmount(transaction, currenciesManager, currencyCode);
    }

    @Override protected List<TagGroup> getGroups(Collection<TagGroup> groups) {
        final List<TagGroup> sortedGroups = new ArrayList<>(groups);
        Collections.sort(sortedGroups, new Comparator<TagGroup>() {
            @Override public int compare(TagGroup lhs, TagGroup rhs) {
                final long delta = lhs.getValue() - rhs.getValue();
                if (delta > 0) {
                    return -1;
                } else if (delta < 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return sortedGroups;
    }
}
