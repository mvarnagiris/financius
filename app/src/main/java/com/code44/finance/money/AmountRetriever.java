package com.code44.finance.money;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Transaction;

public final class AmountRetriever {
    private AmountRetriever() {
    }

    public static long getAmount(Transaction transaction, CurrenciesManager currenciesManager, String currencyCode) {
        if (transaction.getTransactionType() == TransactionType.Income) {
            return getIncomeAmount(transaction, currenciesManager, currencyCode);
        } else {
            return getExpenseAmount(transaction, currenciesManager, currencyCode);
        }
    }

    public static long getExpenseAmount(Transaction transaction, CurrenciesManager currenciesManager, String currencyCode) {
        if (transaction.getTransactionType() == TransactionType.Income) {
            return 0;
        }

        final Account account = transaction.getAccountFrom();
        final String fromCurrencyCode = account != null ? account.getCurrencyCode() : currenciesManager.getMainCurrencyCode();
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
            final Account account = transaction.getAccountTo();
            final String fromCurrencyCode = account != null ? account.getCurrencyCode() : currenciesManager.getMainCurrencyCode();
            if (fromCurrencyCode.equals(currencyCode)) {
                transaction.getAmount();
            }

            return (long) (transaction.getAmount() * currenciesManager.getExchangeRate(fromCurrencyCode, currencyCode));
        }

        final Account accountFrom = transaction.getAccountFrom();
        final Account accountTo = transaction.getAccountTo();
        final String fromCurrencyCode = accountFrom != null ? accountFrom.getCurrencyCode() : currenciesManager.getMainCurrencyCode();
        final String toCurrencyCode = accountTo != null ? accountTo.getCurrencyCode() : currenciesManager.getMainCurrencyCode();
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

    public static String getAmountCurrency(TransactionType transactionType, Account accountFrom, Account accountTo, CurrenciesManager currenciesManager) {
        String currencyCode;
        switch (transactionType) {
            case Expense:
                currencyCode = accountFrom == null ? null : accountFrom.getCurrencyCode();
                break;
            case Income:
                currencyCode = accountTo == null ? null : accountTo.getCurrencyCode();
                break;
            case Transfer:
                currencyCode = accountFrom == null ? null : accountFrom.getCurrencyCode();
                break;
            default:
                throw new IllegalStateException("Category type " + transactionType + " is not supported.");
        }

        if (currencyCode == null) {
            // When account is not selected yet, we use main currency.
            currencyCode = currenciesManager.getMainCurrencyCode();
        }

        return currencyCode;
    }
}
