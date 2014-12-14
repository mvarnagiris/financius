package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.Pair;
import android.text.format.DateUtils;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;

import java.util.List;

public abstract class Finder<T> {
    private final Context context;
    private final AutoCompleteInput autoCompleteInput;

    protected Finder(Context context, AutoCompleteInput autoCompleteInput) {
        this.context = context;
        this.autoCompleteInput = autoCompleteInput;
    }

    public Pair<T, List<T>> find() {
        return find(autoCompleteInput);
    }

    protected abstract Pair<T, List<T>> find(AutoCompleteInput input);

    protected Query getBaseQuery() {
        final long fromDate = System.currentTimeMillis() - (DateUtils.DAY_IN_MILLIS * 90);
        return Tables.Transactions.getQuery()
                .selection(" and " + Tables.Transactions.TYPE + "=?", autoCompleteInput.getTransactionType().asString())
                .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                .selection(" and " + Tables.Transactions.DATE + ">?", String.valueOf(fromDate));
    }

    protected Cursor query(Query query) {
        return query.from(context, TransactionsProvider.uriTransactions()).execute();
    }
}
