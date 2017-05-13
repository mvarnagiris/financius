package com.code44.finance.api.endpoints.requests.transactions;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.GetEntitiesRequest;
import com.code44.finance.backend.financius.model.TransactionEntity;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.EventBus;

import java.util.List;

public class GetTransactionsRequest extends GetEntitiesRequest<Transaction, TransactionEntity> {
    public GetTransactionsRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user) {
        super(eventBus, endpointFactory, context, user);
    }

    @Override protected long getLastUpdateTimestamp(User user) {
        return user.getLastTransactionsUpdateTimestamp();
    }

    @Override protected List<TransactionEntity> performRequest(long timestamp) throws Exception {
        return getEndpoint().listTransactions(timestamp).execute().getItems();
    }

    @Override protected Transaction getModelFrom(TransactionEntity entity) {
        return Transaction.from(entity);
    }

    @Override protected Uri getSaveUri() {
        return TransactionsProvider.uriTransactions();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastTransactionsUpdateTimestamp(lastUpdateTimestamp);
    }
}