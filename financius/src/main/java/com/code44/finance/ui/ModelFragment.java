package com.code44.finance.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.code44.finance.R;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.ui.dialogs.DeleteDialogFragment;

public abstract class ModelFragment<T extends BaseModel> extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int REQUEST_DELETE = 6666;

    protected static final String ARG_MODEL_SERVER_ID = "ARG_MODEL_SERVER_ID";

    protected static final String FRAGMENT_DELETE = "FRAGMENT_DELETE";

    protected static final int LOADER_MODEL = 1000;

    protected String modelId;
    protected T model;

    public static Bundle makeArgs(String modelServerId) {
        final Bundle args = new Bundle();
        args.putString(ARG_MODEL_SERVER_ID, modelServerId);
        return args;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get arguments
        modelId = getArguments().getString(ARG_MODEL_SERVER_ID, "0");
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_MODEL, null, this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.model, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                startModelEdit(getActivity(), modelId);
                return true;

            case R.id.action_delete:
                final Uri deleteUri = getDeleteUri();
                final Pair<String, String[]> deleteSelection = getDeleteSelection();
                DeleteDialogFragment.newInstance(getActivity(), REQUEST_DELETE, deleteUri, deleteSelection.first, deleteSelection.second).show(getFragmentManager(), FRAGMENT_DELETE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODEL) {
            return getModelCursorLoader(getActivity(), modelId);
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

    protected abstract CursorLoader getModelCursorLoader(Context context, String modelServerId);

    protected abstract T getModelFrom(Cursor cursor);

    protected abstract void onModelLoaded(T model);

    protected abstract Uri getDeleteUri();

    protected abstract Pair<String, String[]> getDeleteSelection();

    protected abstract void startModelEdit(Context context, String modelServerId);
}
