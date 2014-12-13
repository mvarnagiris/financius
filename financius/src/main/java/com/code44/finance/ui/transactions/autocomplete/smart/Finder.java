package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;

import java.util.List;

public abstract class Finder<T> {
    protected final Context context;
    protected final TransactionType transactionType;

    protected Finder(Context context, TransactionType transactionType) {
        this.context = context;
        this.transactionType = transactionType;
    }

    public abstract List<T> find();

    protected Query getBaseQuery() {
        final long fromDate = System.currentTimeMillis() - (DateUtils.DAY_IN_MILLIS * 90);
        return Tables.Transactions.getQuery()
                .selection(" and " + Tables.Transactions.TYPE + "=?", transactionType.asString())
                .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                .selection(" and " + Tables.Transactions.DATE + ">?", String.valueOf(fromDate));
    }

    protected Cursor query(Query query) {
        return query.from(context, TransactionsProvider.uriTransactions()).execute();
    }
}
