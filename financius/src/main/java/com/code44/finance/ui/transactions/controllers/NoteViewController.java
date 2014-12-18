package com.code44.finance.ui.transactions.controllers;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.code44.finance.R;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteViewController extends ViewController implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, AdapterView.OnItemClickListener {
    private static final int LOADER_NOTES = 8125;

    private final AutoCompleteTextView noteAutoCompleteTextView;
    private final Callbacks callbacks;

    public NoteViewController(BaseActivity activity, Callbacks callbacks) {
        this.callbacks = callbacks;

        noteAutoCompleteTextView = findView(activity, R.id.noteAutoCompleteTextView);

        noteAutoCompleteTextView.addTextChangedListener(this);
        noteAutoCompleteTextView.setOnItemClickListener(this);
        setAutoCompleteAdapter(Collections.<String>emptyList());

        activity.getSupportLoaderManager().initLoader(LOADER_NOTES, null, this);
    }

    @Override public void showError(Throwable error) {
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final long fromDate = System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS * 90;
        final String filter = noteAutoCompleteTextView.getText().toString();
        final Query query = Query.create()
                .projectionLocalId(Tables.Transactions.LOCAL_ID)
                .projection(Tables.Transactions.NOTE.getName())
                .selection(Tables.Transactions.DATE + ">?", String.valueOf(fromDate))
                .groupBy(Tables.Transactions.NOTE.getName());

        if (!Strings.isEmpty(filter)) {
            query.selection(" and lower(" + Tables.Transactions.NOTE + ") glob ?", "*" + filter.toLowerCase() + "*");
        }

        return query.asCursorLoader(noteAutoCompleteTextView.getContext(), TransactionsProvider.uriTransactions());
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final List<String> items = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            final int noteIndex = data.getColumnIndex(Tables.Transactions.NOTE.getName());
            do {
                items.add(data.getString(noteIndex));
            } while (data.moveToNext());
        }
        setAutoCompleteAdapter(items);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        ((BaseActivity) this.noteAutoCompleteTextView.getContext()).getSupportLoaderManager().restartLoader(LOADER_NOTES, null, this);
        callbacks.onNoteUpdated(getNote());
    }

    @Override public void afterTextChanged(Editable s) {
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String value = (String) parent.getItemAtPosition(position);
        setNote(value);
        noteAutoCompleteTextView.setSelection(noteAutoCompleteTextView.getText().length());
        callbacks.onNoteUpdated(getNote());
    }

    public String getNote() {
        return noteAutoCompleteTextView.getText().toString();
    }

    public void setNote(String note) {
        noteAutoCompleteTextView.removeTextChangedListener(this);
        noteAutoCompleteTextView.setText(note);
        if (noteAutoCompleteTextView.hasFocus()) {
            noteAutoCompleteTextView.setSelection(noteAutoCompleteTextView.getText().length());
        }
        noteAutoCompleteTextView.addTextChangedListener(this);
    }

    private void setAutoCompleteAdapter(List<String> values) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(noteAutoCompleteTextView.getContext(), android.R.layout.simple_dropdown_item_1line, values);
        this.noteAutoCompleteTextView.setAdapter(adapter);
    }

    public static interface Callbacks {
        public void onNoteUpdated(String note);
    }
}
