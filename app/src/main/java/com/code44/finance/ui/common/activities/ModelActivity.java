package com.code44.finance.ui.common.activities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.code44.finance.R;
import com.code44.finance.data.model.Model;
import com.code44.finance.ui.dialogs.DeleteDialogFragment;
import com.google.common.base.Strings;
import com.squareup.otto.Subscribe;

public abstract class ModelActivity<M extends Model> extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String EXTRA_MODEL_ID = ModelActivity.class.getName() + ".EXTRA_MODEL_ID";

    private static final int REQUEST_DELETE = 6666;

    private static final String FRAGMENT_DELETE = "FRAGMENT_DELETE";

    private static final int LOADER_MODEL = 1000;

    private final Object eventHandler = new Object() {
        @Subscribe public void onItemDeleted(DeleteDialogFragment.DeleteDialogEvent event) {
            if (event.getRequestCode() == REQUEST_DELETE && event.isPositiveClicked()) {
                finish();
            }
        }
    };

    private String modelId;
    private M storedModel;

    protected static ActivityStarter makeActivityStarter(Context context, Class<? extends ModelActivity> activityClass, String modelId) {
        return ActivityStarter.begin(context, activityClass).extra(EXTRA_MODEL_ID, modelId);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEventBus().register(eventHandler);

        // Get extras
        modelId = getIntent().getStringExtra(EXTRA_MODEL_ID);
        if (Strings.isNullOrEmpty(modelId)) {
            modelId = "0";
        }

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
                final Pair<String, String[]> deleteSelection = getDeleteSelection(modelId);
                DeleteDialogFragment.newInstance(this, REQUEST_DELETE, deleteUri, deleteSelection.first, deleteSelection.second)
                        .show(getSupportFragmentManager(), FRAGMENT_DELETE);
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
        if (loader.getId() == LOADER_MODEL && data.moveToFirst()) {
            storedModel = getModelFrom(data);
            onModelLoaded(storedModel);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }

    @NonNull protected abstract CursorLoader getModelCursorLoader(@NonNull String modelId);

    @NonNull protected abstract M getModelFrom(@NonNull Cursor cursor);

    protected abstract void onModelLoaded(@NonNull M model);

    protected abstract void startModelEdit(@NonNull String modelId);

    @Nullable protected abstract Uri getDeleteUri();

    @Nullable protected abstract Pair<String, String[]> getDeleteSelection(@NonNull String modelId);

    @NonNull protected String getModelId() {
        return modelId;
    }

    @Nullable protected M getStoredModel() {
        return storedModel;
    }
}
