package com.code44.finance.providers;

import com.code44.finance.db.model.Transaction;

public class TransactionsProvider extends BaseModelProvider<Transaction> {
    @Override
    protected Class<Transaction> getModelClass() {
        return Transaction.class;
    }
}
