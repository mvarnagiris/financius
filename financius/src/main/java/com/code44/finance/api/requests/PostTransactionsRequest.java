package com.code44.finance.api.requests;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.backend.endpoint.transactions.Transactions;
import com.code44.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.code44.finance.backend.endpoint.transactions.model.TransactionsBody;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class PostTransactionsRequest extends PostRequest<TransactionsBody> {
    private final Transactions transactionsService;
    private final List<Transaction> transactions;

    public PostTransactionsRequest(GcmRegistration gcmRegistration, Transactions transactionsService, List<Transaction> transactions) {
        super(null, gcmRegistration);
        Preconditions.notNull(transactionsService, "Transactions service cannot be null.");
        Preconditions.notNull(transactions, "Transactions list cannot be null.");

        this.transactionsService = transactionsService;
        this.transactions = transactions;
    }

    @Override protected TransactionsBody createBody() {
        return new TransactionsBody();
    }

    @Override protected void onAddPostData(TransactionsBody body) {
        final List<TransactionEntity> transactionEntities = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionEntities.add(transaction.asEntity());
        }
        body.setTransactions(transactionEntities);
    }

    @Override protected boolean isPostDataEmpty(TransactionsBody body) {
        return body.getTransactions().isEmpty();
    }

    @Override protected void performRequest(TransactionsBody body) throws Exception {
        transactionsService.save(body);
    }
}
