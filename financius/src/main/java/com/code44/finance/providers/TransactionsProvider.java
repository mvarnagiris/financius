package com.code44.finance.providers;

import android.content.ContentUris;
import android.net.Uri;

import com.code44.finance.db.model.Transaction;

public class TransactionsProvider extends BaseModelProvider<Transaction> {
    public static Uri uriTransactions() {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(TransactionsProvider.class) + "/" + Transaction.class.getSimpleName());
    }

    public static Uri uriTransaction(long transactionId) {
        return ContentUris.withAppendedId(uriTransactions(), transactionId);
    }

    @Override
    protected Class<Transaction> getModelClass() {
        return Transaction.class;
    }
}
