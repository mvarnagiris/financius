package com.code44.finance.ui.transactions.autocomplete.finders;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.providers.TransactionsProvider;

import java.util.List;

public abstract class Finder<T> {
    protected final Context context;
    protected final TransactionType transactionType;
    protected final Long date;
    protected final Long amount;
    protected final Account accountFrom;
    protected final Account accountTo;
    protected final Category category;
    protected final List<Tag> tags;
    protected final String note;

    protected Finder(Context context, TransactionType transactionType, Long date, Long amount, Account accountFrom, Account accountTo, Category category, List<Tag> tags, String note) {
        this.context = context;
        this.transactionType = transactionType;
        this.date = date;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.category = category;
        this.tags = tags;
        this.note = note;
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
