package com.code44.finance.ui.transactions;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.widget.AutoCompleteTextView;

import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.common.BaseActivity;

public class NoteController implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher {
    private static final int LOADER_NOTES = 8125;

    private final AutoCompleteTextView noteAutoCompleteTextView;
    private final SimpleCursorAdapter adapter;

    public NoteController(AutoCompleteTextView noteAutoCompleteTextView) {
        this.noteAutoCompleteTextView = noteAutoCompleteTextView;

        this.adapter = new SimpleCursorAdapter(noteAutoCompleteTextView.getContext(), android.R.layout.simple_dropdown_item_1line, null, new String[]{Tables.Transactions.NOTE.getName()}, new int[]{android.R.id.text1}, 0);
        this.noteAutoCompleteTextView.setAdapter(adapter);
        this.noteAutoCompleteTextView.addTextChangedListener(this);

        ((BaseActivity) this.noteAutoCompleteTextView.getContext()).getSupportLoaderManager().initLoader(LOADER_NOTES, null, this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final long fromDate = System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS * 90;
        final String filter = noteAutoCompleteTextView.getText().toString();
        final Query query = Query.create()
                .projectionLocalId(Tables.Transactions.LOCAL_ID)
                .projection(Tables.Transactions.NOTE.getName())
                .selection(Tables.Transactions.DATE + ">?", String.valueOf(fromDate))
                .groupBy(Tables.Transactions.NOTE.getName());

        if (!StringUtils.isEmpty(filter)) {
            query.selection(" and lower(" + Tables.Transactions.NOTE + ") glob ?", "*" + filter.toLowerCase() + "*");
        }

        return query.asCursorLoader(noteAutoCompleteTextView.getContext(), TransactionsProvider.uriTransactions());
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        ((BaseActivity) this.noteAutoCompleteTextView.getContext()).getSupportLoaderManager().restartLoader(LOADER_NOTES, null, this);
    }

    @Override public void afterTextChanged(Editable s) {
    }

    public String getNote() {
        return noteAutoCompleteTextView.getText().toString();
    }

    public void setNote(String note) {
        noteAutoCompleteTextView.setText(note);
    }
}
