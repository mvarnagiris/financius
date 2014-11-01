package com.code44.finance.ui.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.code44.finance.R;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.ui.dialogs.DeleteDialogFragment;
import com.squareup.otto.Subscribe;

public abstract class ModelActivity<M extends BaseModel> extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String EXTRA_MODEL_ID = "EXTRA_MODEL_ID";

    protected static final int REQUEST_DELETE = 6666;

    protected static final String FRAGMENT_DELETE = "FRAGMENT_DELETE";

    protected static final int LOADER_MODEL = 1000;

    private final Object eventHandler = new Object() {
        @Subscribe public void onItemDeleted(DeleteDialogFragment.DeleteDialogEvent event) {
            if (event.getRequestCode() == ModelFragment.REQUEST_DELETE && event.isPositiveClicked()) {
                finish();
            }
        }
    };

    protected String modelId;
    protected M model;

    protected static Intent makeIntent(Context context, Class<? extends ModelActivity> activityClass, String modelId) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODEL_ID, modelId);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelId = getIntent().getStringExtra(EXTRA_MODEL_ID);
        getEventBus().register(eventHandler);

        // Loader
        getSupportLoaderManager().initLoader(LOADER_MODEL, null, this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.model, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                startModelEdit(modelId);
                return true;

            case R.id.action_delete:
                final Uri deleteUri = getDeleteUri();
                final Pair<String, String[]> deleteSelection = getDeleteSelection();
                DeleteDialogFragment.newInstance(this, REQUEST_DELETE, deleteUri, deleteSelection.first, deleteSelection.second).show(getSupportFragmentManager(), FRAGMENT_DELETE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(eventHandler);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODEL) {
            return getModelCursorLoader(modelId);
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

    protected abstract CursorLoader getModelCursorLoader(String modelId);

    protected abstract M getModelFrom(Cursor cursor);

    protected abstract void onModelLoaded(M model);

    protected abstract Uri getDeleteUri();

    protected abstract Pair<String, String[]> getDeleteSelection();

    protected abstract void startModelEdit(String modelId);
}
