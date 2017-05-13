package com.code44.finance.api.endpoints.requests.transactions;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.SendEntitiesRequest;
import com.code44.finance.backend.financius.model.TransactionsBody;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.EventBus;

public class SendTransactionsRequest extends SendEntitiesRequest<Transaction, TransactionsBody> {
    public SendTransactionsRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull Device device) {
        super(eventBus, endpointFactory, context, user, dbHelper, device);
    }

    @Override protected Query getQuery() {
        return Tables.Transactions.getQuery();
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Transactions.SYNC_STATE;
    }

    @Override protected Uri getUri() {
        return TransactionsProvider.uriTransactions();
    }

    @Override protected Transaction getModel(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected TransactionsBody createBody() {
        return new TransactionsBody();
    }

    @Override protected long performRequest(TransactionsBody body) throws Exception {
        return getEndpoint().updateTransactions(body).execute().getUpdateTimestamp();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastTransactionsUpdateTimestamp(lastUpdateTimestamp);
    }
}
