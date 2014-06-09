package com.code44.finance.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.code44.finance.db.model.BaseModel;
import com.code44.finance.providers.BaseModelProvider;

import nl.qbusict.cupboard.CupboardFactory;

public abstract class ModelFragment<T extends BaseModel> extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_MODEL_ID = "ARG_MODEL_ID";

    private static final int LOADER_MODEL = 1000;

    private long modelId;

    public static Bundle makeArgs(long modelId) {
        final Bundle args = new Bundle();
        args.putLong(ARG_MODEL_ID, modelId);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri uri = BaseModelProvider.uriModel(getModelProviderClass(), getModelClass(), modelId);
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            onModelLoaded(CupboardFactory.cupboard().withCursor(data).get(getModelClass()));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    protected abstract Class<? extends BaseModelProvider<T>> getModelProviderClass();

    protected abstract Class<T> getModelClass();

    protected abstract void onModelLoaded(T model);
}
