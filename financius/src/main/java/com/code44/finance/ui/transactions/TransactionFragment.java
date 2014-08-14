package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.ModelFragment;

public class TransactionFragment extends ModelFragment<Transaction> {
    public static TransactionFragment newInstance(String transactionServerId) {
        final Bundle args = makeArgs(transactionServerId);

        final TransactionFragment fragment = new TransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views

    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Accounts.getQuery().asCursorLoader(context, AccountsProvider.uriAccount(modelServerId));
    }

    @Override protected Transaction getModelFrom(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected void onModelLoaded(Transaction model) {
        // TODO
    }

    @Override protected Uri getDeleteUri() {
        return TransactionsProvider.uriTransactions();
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Transactions.SERVER_ID + "=?", new String[]{modelServerId});
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        // TODO AccountEditActivity.start(context, modelServerId);
    }
}
