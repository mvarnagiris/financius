package com.code44.finance.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;

import com.code44.finance.data.db.model.BaseModel;

public abstract class ModelEditFragment<T extends BaseModel> extends ModelFragment<T> {
    private static final String STATE_MODEL = "STATE_MODEL";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore state
        if (savedInstanceState != null) {
            model = savedInstanceState.getParcelable(STATE_MODEL);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (model != null) {
            onModelLoaded(model);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ensureModelUpdated(model);
        outState.putParcelable(STATE_MODEL, model);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODEL && model == null) {
            super.onLoadFinished(loader, data);
        }
    }

    public boolean save() {
        ensureModelUpdated(model);
        return onSave(model);
    }

    public abstract boolean onSave(T model);

    protected abstract void ensureModelUpdated(T model);
}
