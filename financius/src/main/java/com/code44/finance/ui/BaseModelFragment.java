package com.code44.finance.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.code44.finance.db.model.BaseModel;

import nl.qbusict.cupboard.CupboardFactory;

public abstract class BaseModelFragment<T extends BaseModel> extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_MODEL_ID = "ARG_MODEL_ID";

    private static final int LOADER_MODEL = 1000;

    protected long modelId;
    protected T model;

    public static Bundle makeArgs(long itemId) {
        final Bundle args = new Bundle();
        args.putLong(ARG_MODEL_ID, itemId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        modelId = getArguments().getLong(ARG_MODEL_ID, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_MODEL, null, this);
    }

    public abstract boolean onSave();

    protected abstract Uri getModelUri(long modelId);

    protected abstract Class<? extends T> getModelClass();

    protected abstract T getNewModel();

    protected abstract void onModelLoaded(T model);

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), getModelUri(modelId), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            model = CupboardFactory.cupboard().withCursor(data).get(getModelClass());
        } else {
            model = getNewModel();
        }
        onModelLoaded(model);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
