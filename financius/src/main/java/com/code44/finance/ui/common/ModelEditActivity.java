package com.code44.finance.ui.common;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.model.Model;

public abstract class ModelEditActivity<M extends Model> extends ModelActivity<M> {
    private static final String STATE_MODEL = "STATE_MODEL";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore state
        if (savedInstanceState != null) {
            model = savedInstanceState.getParcelable(STATE_MODEL);
        }
    }

    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Get views
        final Button cancelView = (Button) findViewById(R.id.cancelButton);
        final Button saveView = (Button) findViewById(R.id.saveButton);

        // Setup
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override protected void onStart() {
        super.onStart();
        if (model != null) {
            onModelLoaded(model);
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ensureModelUpdated(model);
        outState.putParcelable(STATE_MODEL, model);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODEL && model == null) {
            super.onLoadFinished(loader, data);
        }
    }

    @Override protected Uri getDeleteUri() {
        // Ignore
        return null;
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        // Ignore
        return null;
    }

    @Override protected void startModelEdit(String modelId) {
        // Ignore
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    protected abstract boolean onSave(M model);

    protected abstract void ensureModelUpdated(M model);

    protected boolean isNewModel() {
        return modelId.equals("0");
    }

    private void save() {
        ensureModelUpdated(model);
        if (onSave(model)) {
            finish();
        }
    }

    private void cancel() {
        finish();
    }
}