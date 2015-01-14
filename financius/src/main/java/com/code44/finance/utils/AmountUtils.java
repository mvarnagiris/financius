package com.code44.finance.utils;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;

public final class AmountUtils {
    private AmountUtils() {
    }

    public static long getExpenseAmount(Transaction transaction, Currency baseCurrency) {
        if (transaction.getTransactionType() == TransactionType.Income) {
            return 0;
        }

        if (baseCurrency.equals(transaction.getAccountFrom().getCurrency())) {
            transaction.getAmount();
        }

        return transaction.getAmount();
    }

    public static long getIncomeAmount(Transaction transaction) {
        if (transaction.getTransactionType() == TransactionType.Expense) {
            return 0;
        }

        if (transaction.getTransactionType() == TransactionType.Income) {
            return transaction.getAmount();
        }

        final Currency fromCurrency = transaction.getAccountFrom().getCurrency();
        final Currency toCurrency = transaction.getAccountTo().getCurrency();

        if (fromCurrency.equals(toCurrency)) {
            return transaction.getAmount();
        }

        return (long) (transaction.getAmount() * transaction.getExchangeRate());
    }
}
