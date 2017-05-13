package com.code44.finance.ui.transactions.edit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteEditText extends AutoCompleteTextView implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int LOADER_NOTES = 8125;

    public NoteEditText(Context context) {
        super(context);
        init();
    }

    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_NOTES) {
            final long fromDate = System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS * 90;
            final String filter = getText().toString();
            final Query query = Query.create()
                    .projectionLocalId(Tables.Transactions.LOCAL_ID)
                    .projection(Tables.Transactions.NOTE.getName())
                    .selection(Tables.Transactions.DATE + ">?", String.valueOf(fromDate))
                    .selection(" and " + Tables.Transactions.MODEL_STATE + "=?", ModelState.Normal.asString())
                    .groupBy(Tables.Transactions.NOTE.getName());

            if (!Strings.isNullOrEmpty(filter)) {
                query.selection(" and lower(" + Tables.Transactions.NOTE + ") glob ?", "*" + filter.toLowerCase() + "*");
            }

            return query.asCursorLoader(getContext(), TransactionsProvider.uriTransactions());
        }

        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_NOTES) {
            final List<String> items = new ArrayList<>();
            if (data != null && data.moveToFirst()) {
                final int noteIndex = data.getColumnIndex(Tables.Transactions.NOTE.getName());
                do {
                    items.add(data.getString(noteIndex));
                } while (data.moveToNext());
            }
            setAutoCompleteAdapter(items);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String value = (String) parent.getItemAtPosition(position);
        setText(value);
        setSelection(getText().length());
    }

    private void init() {
        // Setup
        addTextChangedListener(new NoteTextWatcher());
        setOnItemClickListener(this);
        setAutoCompleteAdapter(Collections.<String>emptyList());

        // Loader
        if (!isInEditMode()) {
            ((BaseActivity) getContext()).getSupportLoaderManager().initLoader(LOADER_NOTES, null, this);
        }
    }

    private void setAutoCompleteAdapter(List<String> values) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, values);
        setAdapter(adapter);
    }

    private class NoteTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override public void afterTextChanged(Editable s) {
            ((BaseActivity) getContext()).getSupportLoaderManager().restartLoader(LOADER_NOTES, null, NoteEditText.this);
        }
    }
}
