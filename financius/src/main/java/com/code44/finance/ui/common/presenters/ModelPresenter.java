package com.code44.finance.ui.common.presenters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;

import com.code44.finance.data.model.Model;

public abstract class ModelPresenter<M extends Model> extends Presenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String EXTRA_MODEL_ID = ModelPresenter.class.getName() + ".EXTRA_MODEL_ID";

    private static final int LOADER_MODEL = 1000;

    private final Context context;
    private final String modelId;

    private M model;

    public ModelPresenter(ActionBarActivity activity) {
        this.context = activity.getApplicationContext();
        modelId = activity.getIntent().getStringExtra(EXTRA_MODEL_ID);

        activity.getSupportLoaderManager().initLoader(LOADER_MODEL, null, this);
    }

    public static void addExtras(Intent intent, String modelId) {
        intent.putExtra(EXTRA_MODEL_ID, modelId);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODEL) {
            return getModelCursorLoader(context, modelId);
        }

        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODEL) {
            model = getModelFrom(data);
            onModelLoaded(model);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
    }

    protected abstract CursorLoader getModelCursorLoader(Context context, String modelId);

    protected abstract M getModelFrom(Cursor cursor);

    protected abstract void onModelLoaded(M model);
}
