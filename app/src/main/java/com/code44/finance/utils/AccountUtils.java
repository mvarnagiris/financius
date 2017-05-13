package com.code44.finance.utils;

import com.code44.finance.data.model.Transaction;

public final class AccountUtils {
    private static final String UNKNOWN_VALUE = "?";
    private static final String TRANSFER_SYMBOL = " → ";

    private AccountUtils() {
    }

    public static String getTitle(Transaction transaction) {
        switch (transaction.getTransactionType()) {
            case Expense:
                return transaction.getAccountFrom() != null ? transaction.getAccountFrom().getTitle() : UNKNOWN_VALUE;
            case Income:
                return transaction.getAccountTo() != null ? transaction.getAccountTo().getTitle() : UNKNOWN_VALUE;
            case Transfer:
                return (transaction.getAccountFrom() != null ? transaction.getAccountFrom()
                        .getTitle() : UNKNOWN_VALUE) + TRANSFER_SYMBOL + (transaction.getAccountTo() != null ? transaction.getAccountTo()
                        .getTitle() : UNKNOWN_VALUE);
            default:
                throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
        }
    }
}
