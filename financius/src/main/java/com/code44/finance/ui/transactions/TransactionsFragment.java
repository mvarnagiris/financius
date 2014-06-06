package com.code44.finance.ui.transactions;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.TransactionsAdapter;
import com.code44.finance.db.model.Account;
import com.code44.finance.db.model.Category;
import com.code44.finance.db.model.Transaction;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.ui.BaseFragment;

import nl.qbusict.cupboard.CupboardFactory;

public class TransactionsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_TRANSACTIONS = 1;

    private TransactionsAdapter adapter;

    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        adapter = new TransactionsAdapter(getActivity());
        list_V.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Transaction transaction = new Transaction();
        transaction.useDefaultsIfNotSet();
        transaction.setAccountFrom(Account.getSystem());
        transaction.setAccountTo(Account.getSystem());
        transaction.setCategory(Category.getExpense());
        CupboardFactory.cupboard().withContext(getActivity()).put(TransactionsProvider.uriTransactions(), transaction);

        // Loader
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri uri = TransactionsProvider.uriTransactions();
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
