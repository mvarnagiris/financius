package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.transactions.model.TransactionEntity;
import com.code44.finance.backend.endpoint.transactions.model.TransactionsBody;
import com.code44.finance.data.db.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SaveTransactionsRequest extends FinanciusBaseRequest<Void> {
    private final TransactionsBody body;

    public SaveTransactionsRequest(Context context, User user, List<Transaction> transactions) {
        super(null, context, user);
        body = preparePostBody(transactions);
    }

    @Override
    protected Void performRequest() throws Exception {
        getTransactionsService().save(body).execute();
        return null;
    }

    private TransactionsBody preparePostBody(List<Transaction> transactions) {
        final List<TransactionEntity> transactionEntities = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionEntities.add(transaction.toEntity());
        }

        final TransactionsBody body = new TransactionsBody();
        body.setTransactions(transactionEntities);
        body.setDeviceRegId(GcmRegistration.get().getRegistrationId());

        return body;
    }
}
