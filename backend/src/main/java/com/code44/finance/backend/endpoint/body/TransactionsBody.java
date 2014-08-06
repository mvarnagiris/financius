package com.code44.finance.backend.endpoint.body;

import com.code44.finance.backend.entity.CategoryEntity;
import com.code44.finance.backend.entity.TransactionEntity;

import java.util.List;

public class TransactionsBody extends EntitiesBody<CategoryEntity> {
    private final List<TransactionEntity> transactions;

    public TransactionsBody(List<TransactionEntity> transactions, String deviceRegId) {
        super(deviceRegId);
        this.transactions = transactions;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }
}
