package com.code44.finance.money.grouping;

import com.code44.finance.data.model.Transaction;
import com.code44.finance.money.AmountRetriever;
import com.code44.finance.money.CurrenciesManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryTagAmountGroups extends AmountGroups<CategoryTagGroup> {
    private final CurrenciesManager currenciesManager;
    private final String currencyCode;
    private final TransactionValidator transactionValidator;

    public CategoryTagAmountGroups(CurrenciesManager currenciesManager, TransactionValidator transactionValidator) {
        this(currenciesManager, currenciesManager.getMainCurrencyCode(), transactionValidator);
    }

    public CategoryTagAmountGroups(CurrenciesManager currenciesManager, String currencyCode, TransactionValidator transactionValidator) {
        this.currenciesManager = currenciesManager;
        this.currencyCode = currencyCode;
        this.transactionValidator = transactionValidator;
    }

    @Override protected int getGroupCount(Transaction transaction) {
        return 1;
    }

    @Override protected Long getGroupId(Transaction transaction, int groupPosition) {
        if (transactionValidator != null && !transactionValidator.isTransactionValid(transaction)) {
            return null;
        }

        return transaction.getCategory() == null ? 0L : transaction.getCategory().getId().hashCode();
    }

    @Override protected CategoryTagGroup createAmountGroup(Transaction transaction, int groupPosition) {
        return new CategoryTagGroup(transaction.getCategory());
    }

    @Override protected long getAmount(CategoryTagGroup amountGroup, Transaction transaction) {
        final long amount = AmountRetriever.getAmount(transaction, currenciesManager, currencyCode);
        amountGroup.addTagsAmount(transaction, amount);
        return amount;
    }

    @Override protected List<CategoryTagGroup> getGroups(Collection<CategoryTagGroup> groups) {
        final List<CategoryTagGroup> sortedGroups = new ArrayList<>(groups);
        Collections.sort(sortedGroups, new Comparator<CategoryTagGroup>() {
            @Override public int compare(CategoryTagGroup lhs, CategoryTagGroup rhs) {
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
