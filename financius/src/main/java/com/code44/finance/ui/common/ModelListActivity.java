package com.code44.finance.ui.common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.ui.DrawerActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ModelListActivity extends DrawerActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, View.OnClickListener {
    public static final String RESULT_EXTRA_MODEL = "RESULT_EXTRA_MODEL";
    public static final String RESULT_EXTRA_MODELS = "RESULT_EXTRA_MODELS";

    protected static final int LOADER_MODELS = 1245;

    private static final String EXTRA_MODE = ModelListActivity.class.getName() + ".EXTRA_MODE";
    private static final String EXTRA_SELECTED_MODELS = ModelListActivity.class.getName() + ".EXTRA_SELECTED_MODELS";

    protected Mode mode;
    protected Parcelable[] selectedModels;
    protected BaseModelsAdapter adapter;

    public static Intent makeViewIntent(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, Mode.VIEW);
        return intent;
    }

    public static Intent makeSelectIntent(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, Mode.SELECT);
        return intent;
    }

    public static Intent makeMultiSelectIntent(Context context, Class<? extends ModelListActivity> activityClass, List<? extends BaseModel> selectedModels) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, Mode.MULTI_SELECT);
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (BaseModel model : selectedModels) {
            parcelables[index++] = model;
        }
        intent.putExtra(EXTRA_SELECTED_MODELS, parcelables);
        return intent;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mode = (Mode) getIntent().getSerializableExtra(EXTRA_MODE);
        selectedModels = getIntent().getParcelableArrayExtra(EXTRA_SELECTED_MODELS);
        if (mode == null) {
            throw new IllegalStateException("Activity " + ((Object) this).getClass().getName() + " must be created with Intent containing " + EXTRA_MODE + " with values from " + Mode.class.getName());
        }
    }

    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Setup Toolbar
        if (mode != Mode.VIEW) {
            getSupportActionBar().setTitle(R.string.select);
        }

        // Get views
        final ListView listView = (ListView) findViewById(R.id.list);
        final View editButtonsContainerView = findViewById(R.id.editButtonsContainer);

        // Setup
        adapter = createAdapter();
        if (mode == Mode.MULTI_SELECT) {
            final Set<BaseModel> selectedModelsSet = new HashSet<>();
            for (Parcelable parcelable : selectedModels) {
                selectedModelsSet.add((BaseModel) parcelable);
            }
            adapter.setSelectedModels(selectedModelsSet);
        }

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        if (editButtonsContainerView != null) {
            if (mode == Mode.MULTI_SELECT) {
                editButtonsContainerView.setVisibility(View.VISIBLE);
                final Button save_B = (Button) findViewById(R.id.save);
                final Button cancel_B = (Button) findViewById(R.id.cancel);
                save_B.setOnClickListener(this);
                cancel_B.setOnClickListener(this);
            } else {
                editButtonsContainerView.setVisibility(View.GONE);
            }
        }

        // Loader
        getSupportLoaderManager().initLoader(LOADER_MODELS, null, this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.models, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startModelEdit(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            return getModelsCursorLoader();
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.swapCursor(data);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.swapCursor(null);
        }
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final BaseModel model = modelFrom(adapter.getCursor());
        if (mode == Mode.VIEW) {
            onModelClick(view, position, model.getId(), model);
        } else if (mode == Mode.SELECT) {
            onModelSelected(model);
        } else {
            adapter.toggleModelSelected(modelFrom(adapter.getCursor()));
        }
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                onMultipleModelsSelected(adapter.getSelectedModels());
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    protected abstract BaseModelsAdapter createAdapter();

    protected abstract CursorLoader getModelsCursorLoader();

    protected abstract BaseModel modelFrom(Cursor cursor);

    protected abstract void onModelClick(View view, int position, String modelId, BaseModel model);

    protected abstract void startModelEdit(String modelId);

    protected void onModelSelected(BaseModel model) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MODEL, model);
        setResult(RESULT_OK, data);
        finish();
    }

    protected void onMultipleModelsSelected(Set<BaseModel> selectedModels) {
        final Intent data = new Intent();
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (BaseModel model : selectedModels) {
            parcelables[index++] = model;
        }
        data.putExtra(RESULT_EXTRA_MODELS, parcelables);
        setResult(RESULT_OK, data);
        finish();
    }

    protected Mode getMode() {
        return mode;
    }

    public static enum Mode {
        VIEW, SELECT, MULTI_SELECT
    }
}
