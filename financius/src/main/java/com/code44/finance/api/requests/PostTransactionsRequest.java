package com.code44.finance.api.requests;

import com.code44.finance.backend.endpoint.transactions.Transactions;
import com.code44.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.code44.finance.backend.endpoint.transactions.model.TransactionsBody;
import com.code44.finance.data.db.model.Transaction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PostTransactionsRequest extends PostRequest<TransactionsBody> {
    private final List<Transaction> transactions;
    @Inject Transactions transactionsService;

    public PostTransactionsRequest(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override protected TransactionsBody createBody() {
        return new TransactionsBody();
    }

    @Override protected void onAddPostData(TransactionsBody body) {
        final List<TransactionEntity> transactionEntities = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionEntities.add(transaction.toEntity());
        }
        body.setTransactions(transactionEntities);
    }

    @Override protected void performRequest(TransactionsBody body) throws Exception {
        transactionsService.save(body);
    }
}
