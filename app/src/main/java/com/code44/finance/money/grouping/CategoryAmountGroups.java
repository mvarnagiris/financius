package com.code44.finance.money.grouping;

import com.code44.finance.data.model.Transaction;
import com.code44.finance.money.AmountRetriever;
import com.code44.finance.money.CurrenciesManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryAmountGroups extends AmountGroups<CategoryGroup> {
    private final CurrenciesManager currenciesManager;
    private final String currencyCode;
    private final TransactionValidator transactionValidator;

    public CategoryAmountGroups(CurrenciesManager currenciesManager) {
        this(currenciesManager, currenciesManager.getMainCurrencyCode());
    }

    public CategoryAmountGroups(CurrenciesManager currenciesManager, String currencyCode) {
        this(currenciesManager, currencyCode, null);
    }

    public CategoryAmountGroups(CurrenciesManager currenciesManager, TransactionValidator transactionValidator) {
        this(currenciesManager, currenciesManager.getMainCurrencyCode(), transactionValidator);
    }

    public CategoryAmountGroups(CurrenciesManager currenciesManager, String currencyCode, TransactionValidator transactionValidator) {
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

    @Override protected CategoryGroup createAmountGroup(Transaction transaction, int groupPosition) {
        return new CategoryGroup(transaction.getCategory());
    }

    @Override protected long getAmount(CategoryGroup amountGroup, Transaction transaction) {
        return AmountRetriever.getAmount(transaction, currenciesManager, currencyCode);
    }

    @Override protected List<CategoryGroup> getGroups(Collection<CategoryGroup> groups) {
        final List<CategoryGroup> sortedGroups = new ArrayList<>(groups);
        Collections.sort(sortedGroups, new Comparator<CategoryGroup>() {
            @Override public int compare(CategoryGroup lhs, CategoryGroup rhs) {
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