package com.code44.finance.money.grouping;

import com.code44.finance.data.model.Transaction;

public interface TransactionValidator {
    boolean isTransactionValid(Transaction transaction);
}
