package com.code44.finance.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;

import com.code44.finance.db.model.BaseModel;

import nl.qbusict.cupboard.CupboardFactory;

public abstract class ModelEditFragment<T extends BaseModel> extends ModelFragment<T> {
    private static final String STATE_MODEL = "STATE_MODEL";

    private T model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore state
        if (savedInstanceState != null) {
            //noinspection unchecked
            model = savedInstanceState.getParcelable(STATE_MODEL);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_MODEL, model);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (model != null) {
            onModelLoaded(model);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            if (model == null) {
                model = CupboardFactory.cupboard().withCursor(data).get(getModelClass());
                onModelLoaded(model);
            }
        }
    }

    public abstract boolean onSave();

    protected T getModel() {
        return model;
    }
}
