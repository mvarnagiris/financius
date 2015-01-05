package com.code44.finance.ui.common.presenters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;

import com.code44.finance.App;
import com.code44.finance.data.model.Model;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.dialogs.DeleteDialogFragment;
import com.code44.finance.utils.EventBus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public abstract class ModelActivityPresenter<M extends Model> extends ActivityPresenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String EXTRA_MODEL_ID = ModelActivityPresenter.class.getName() + ".EXTRA_MODEL_ID";

    private static final int REQUEST_DELETE = 6666;

    private static final String FRAGMENT_DELETE = "FRAGMENT_DELETE";

    private static final int LOADER_MODEL = 1000;

    private final Object eventHandler = new Object() {
        @Subscribe public void onItemDeleted(DeleteDialogFragment.DeleteDialogEvent event) {
            if (event.getRequestCode() == ModelFragment.REQUEST_DELETE && event.isPositiveClicked()) {
                callbacks.onModelDeleted();
            }
        }
    };

    @Inject EventBus eventBus;

    private String modelId;
    private M model;

    public ModelActivityPresenter() {


        App.with(context).inject(eventHandler);
        eventBus.register(this);

        activity.getSupportLoaderManager().initLoader(LOADER_MODEL, null, this);
    }

    public static void addExtras(Intent intent, String modelId) {
        intent.putExtra(EXTRA_MODEL_ID, modelId);
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        modelId = activity.getIntent().getStringExtra(EXTRA_MODEL_ID);
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

    public void startModelEdit() {
        startModelEdit(context, modelId);
    }

    public void deleteModel() {
        final Uri deleteUri = getDeleteUri();
        final Pair<String, String[]> deleteSelection = getDeleteSelection(modelId);
        DeleteDialogFragment.newInstance(context, REQUEST_DELETE, deleteUri, deleteSelection.first, deleteSelection.second).show(fragmentManager, FRAGMENT_DELETE);
    }

    protected abstract CursorLoader getModelCursorLoader(Context context, String modelId);

    protected abstract M getModelFrom(Cursor cursor);

    protected abstract void onModelLoaded(M model);

    protected abstract void startModelEdit(Context context, String modelId);

    protected abstract Uri getDeleteUri();

    protected abstract Pair<String, String[]> getDeleteSelection(String modelId);
}
