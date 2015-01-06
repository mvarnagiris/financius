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
import com.code44.finance.data.model.Model;
import com.code44.finance.ui.DrawerActivity;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ModelListActivity extends DrawerActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, View.OnClickListener {
    protected static final int LOADER_MODELS = 1245;

    private static final String RESULT_EXTRA_MODEL = "RESULT_EXTRA_MODEL";
    private static final String RESULT_EXTRA_MODELS = "RESULT_EXTRA_MODELS";

    private static final String EXTRA_MODE = ModelListActivity.class.getName() + ".EXTRA_MODE";
    private static final String EXTRA_SELECTED_MODELS = ModelListActivity.class.getName() + ".EXTRA_SELECTED_MODELS";

    protected ModelsActivityPresenter.Mode mode;
    protected Parcelable[] selectedModels;
    protected BaseModelsAdapter adapter;

    public static Intent makeViewIntent(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelsActivityPresenter.Mode.View);
        return intent;
    }

    public static Intent makeSelectIntent(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelsActivityPresenter.Mode.Select);
        return intent;
    }

    public static Intent makeMultiSelectIntent(Context context, Class<? extends ModelListActivity> activityClass, List<? extends Model> selectedModels) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelsActivityPresenter.Mode.MultiSelect);
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (Model model : selectedModels) {
            parcelables[index++] = model;
        }
        intent.putExtra(EXTRA_SELECTED_MODELS, parcelables);
        return intent;
    }

    public static <T extends Parcelable> T getModelExtra(Intent data) {
        return data.getParcelableExtra(ModelListActivity.RESULT_EXTRA_MODEL);
    }

    public static <T extends Parcelable> List<T> getModelsExtra(Intent data) {
        final Parcelable[] parcelables = data.getParcelableArrayExtra(ModelListActivity.RESULT_EXTRA_MODELS);
        final List<T> models = new ArrayList<>();
        for (Parcelable parcelable : parcelables) {
            //noinspection unchecked
            models.add((T) parcelable);
        }
        return models;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        onExtras(getIntent());
        onViewCreated();
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
        final Model model = modelFrom(adapter.getCursor());
        if (mode == ModelsActivityPresenter.Mode.View) {
            onModelClick(view, position, model.getId(), model);
        } else if (mode == ModelsActivityPresenter.Mode.Select) {
            onModelSelected(model);
        } else {
            adapter.toggleModelSelected(modelFrom(adapter.getCursor()));
        }
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                onMultipleModelsSelected(adapter.getSelectedModels());
                break;
            case R.id.cancelButton:
                finish();
                break;
        }
    }

    protected abstract int getLayoutId();

    protected abstract BaseModelsAdapter createAdapter();

    protected abstract CursorLoader getModelsCursorLoader();

    protected abstract Model modelFrom(Cursor cursor);

    protected abstract void onModelClick(View view, int position, String modelId, Model model);

    protected abstract void startModelEdit(String modelId);

    protected void onExtras(Intent extras) {
        mode = (ModelsActivityPresenter.Mode) extras.getSerializableExtra(EXTRA_MODE);
        selectedModels = extras.getParcelableArrayExtra(EXTRA_SELECTED_MODELS);
        if (mode == null) {
            throw new IllegalStateException("Activity " + ((Object) this).getClass().getName() + " must be created with Intent containing " + EXTRA_MODE + " with values from " + ModelsActivityPresenter.Mode.class.getName());
        }
    }

    protected void onViewCreated() {
        // Setup Toolbar
        if (mode != ModelsActivityPresenter.Mode.View) {
            getSupportActionBar().setTitle(R.string.select);
        }

        // Get views
        final View editButtonsContainerView = findViewById(R.id.editButtonsContainerView);

        // Setup
        adapter = createAdapter();
        if (mode == ModelsActivityPresenter.Mode.MultiSelect) {
            final Set<Model> selectedModelsSet = new HashSet<>();
            for (Parcelable parcelable : selectedModels) {
                selectedModelsSet.add((Model) parcelable);
            }
            adapter.setSelectedModels(selectedModelsSet);
        }

        if (editButtonsContainerView != null) {
            if (mode == ModelsActivityPresenter.Mode.MultiSelect) {
                editButtonsContainerView.setVisibility(View.VISIBLE);
                final Button save_B = (Button) findViewById(R.id.saveButton);
                final Button cancel_B = (Button) findViewById(R.id.cancelButton);
                save_B.setOnClickListener(this);
                cancel_B.setOnClickListener(this);
            } else {
                editButtonsContainerView.setVisibility(View.GONE);
            }
        }
        onSetupList(adapter);
    }

    protected void onModelSelected(Model model) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MODEL, model);
        setResult(RESULT_OK, data);
        finish();
    }

    protected void onMultipleModelsSelected(Set<Model> selectedModels) {
        final Intent data = new Intent();
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (Model model : selectedModels) {
            parcelables[index++] = model;
        }
        data.putExtra(RESULT_EXTRA_MODELS, parcelables);
        setResult(RESULT_OK, data);
        finish();
    }

    protected ModelsActivityPresenter.Mode getMode() {
        return mode;
    }

    protected void onSetupList(BaseModelsAdapter adapter) {
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

}
