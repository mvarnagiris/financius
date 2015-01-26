package com.code44.finance.money;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Transaction;

public final class AmountRetriever {
    private AmountRetriever() {
    }

    public static long getExpenseAmount(Transaction transaction, CurrenciesManager currenciesManager, String currencyCode) {
        if (transaction.getTransactionType() == TransactionType.Income) {
            return 0;
        }

        final String fromCurrencyCode = transaction.getAccountFrom().getCurrencyCode();
        if (fromCurrencyCode.equals(currencyCode)) {
            transaction.getAmount();
        }

        return (long) (transaction.getAmount() * currenciesManager.getExchangeRate(fromCurrencyCode, currencyCode));
    }

    public static long getIncomeAmount(Transaction transaction, CurrenciesManager currenciesManager, String currencyCode) {
        if (transaction.getTransactionType() == TransactionType.Expense) {
            return 0;
        }

        if (transaction.getTransactionType() == TransactionType.Income) {
            final String fromCurrencyCode = transaction.getAccountTo().getCurrencyCode();
            if (fromCurrencyCode.equals(currencyCode)) {
                transaction.getAmount();
            }

            return (long) (transaction.getAmount() * currenciesManager.getExchangeRate(fromCurrencyCode, currencyCode));
        }


        final String fromCurrencyCode = transaction.getAccountFrom().getCurrencyCode();
        final String toCurrencyCode = transaction.getAccountTo().getCurrencyCode();
        long amount;
        if (fromCurrencyCode.equals(toCurrencyCode)) {
            amount = transaction.getAmount();
        } else {
            amount = (long) (transaction.getAmount() * transaction.getExchangeRate());
        }

        if (toCurrencyCode.equals(currencyCode)) {
            return amount;
        }

        return (long) (amount * currenciesManager.getExchangeRate(toCurrencyCode, currencyCode));
    }
}
