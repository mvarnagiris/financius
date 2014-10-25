package com.code44.finance.utils.transaction;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class LastTransactionAutoComplete extends TransactionAutoComplete {
    public LastTransactionAutoComplete(Context context, Executor executor) {
        super(context, executor);
    }

    @Override protected void onTransactionLoaded(Transaction transaction) {
        getExecutor().execute(new QueryRunnable(getContext(), this, transaction));
    }

    @Override protected void onTransactionTypeChanged(Transaction transaction, TransactionType transactionType) {
        onTransactionLoaded(transaction);
    }

    @Override protected void onDateChanged(Transaction transaction, long date) {

    }

    @Override protected void onAmountChanged(Transaction transaction, long amount) {

    }

    @Override protected void onAccountFromChanged(Transaction transaction, Account account) {

    }

    @Override protected void onAccountToChanged(Transaction transaction, Account account) {

    }

    @Override protected void onCategoryChanged(Transaction transaction, Category category) {

    }

    @Override protected void onTagsChanged(Transaction transaction, List<Tag> tags) {

    }

    @Override protected void onNoteChanged(Transaction transaction, String note) {

    }

    private void onTransactionQueryFinished(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        final Transaction transaction = Transaction.from(cursor);
        if (transaction.getAccountFrom() != null) {
            autoCompleteAccountsFrom(Arrays.asList(transaction.getAccountFrom()));
        }

        if (transaction.getAccountTo() != null) {
            autoCompleteAccountsTo(Arrays.asList(transaction.getAccountTo()));
        }

        autoCompleteAmounts(Arrays.asList(transaction.getAmount()));

        if (transaction.getCategory() != null) {
            autoCompleteCategories(Arrays.asList(transaction.getCategory()));
        }

        autoCompleteTags(transaction.getTags());
    }

    private static class QueryRunnable implements Runnable {
        private final Context context;
        private final LastTransactionAutoComplete transactionAutoComplete;
        private final Transaction transaction;

        private QueryRunnable(Context context, LastTransactionAutoComplete transactionAutoComplete, Transaction transaction) {
            this.context = context;
            this.transactionAutoComplete = transactionAutoComplete;
            this.transaction = transaction;
        }

        @Override public void run() {
            final Cursor cursor = Tables.Transactions.getQuery()
                    .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                    .selection(" and " + Tables.Transactions.TYPE + "=?", transaction.getTransactionType().asString())
                    .limit(1)
                    .from(context, TransactionsProvider.uriTransactions())
                    .execute();

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override public void run() {
                    transactionAutoComplete.onTransactionQueryFinished(cursor);
                }
            });
        }
    }
}
