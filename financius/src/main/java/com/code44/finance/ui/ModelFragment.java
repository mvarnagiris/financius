package com.code44.finance.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.code44.finance.db.model.BaseModel;
import com.code44.finance.utils.Query;

public abstract class ModelFragment<T extends BaseModel> extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final int LOADER_MODEL = 1000;
    private static final String ARG_MODEL_ID = "ARG_MODEL_ID";
    protected long modelId;
    protected T model;

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
        if (id == LOADER_MODEL) {
            Query query = getQuery();
            if (query == null) {
                query = Query.get().build();
            }
            return query.asCursorLoader(getActivity(), getUri(modelId));
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODEL) {
            model = getModelFrom(data);
            onModelLoaded(model);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    protected abstract Uri getUri(long modelId);

    protected abstract T getModelFrom(Cursor cursor);

    protected abstract void onModelLoaded(T model);

    protected Query getQuery() {
        return null;
    }
}
