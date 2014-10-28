package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapterOld;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.ui.common.BaseModelsAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ModelListActivity extends DrawerActivity implements AdapterView.OnItemClickListener {
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
        intent.putExtra(EXTRA_MODE, ModelListFragment.Mode.VIEW);
        return intent;
    }

    public static Intent makeSelectIntent(Context context, Class<? extends ModelListActivity> activityClass) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelListFragment.Mode.SELECT);
        return intent;
    }

    public static Intent makeMultiSelectIntent(Context context, Class<? extends ModelListActivity> activityClass, List<? extends BaseModel> selectedModels) {
        final Intent intent = makeIntentForActivity(context, activityClass);
        intent.putExtra(EXTRA_MODE, ModelListFragment.Mode.MULTI_SELECT);
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
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        final View editButtonsContainerView = findViewById(R.id.editButtonsContainer);

        // Setup
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        if (editButtonsContainerView != null) {
            if (mode == Mode.MULTI_SELECT) {
                editButtonsContainerView.setVisibility(View.VISIBLE);
                final Button save_B = (Button) view.findViewById(R.id.save_B);
                final Button cancel_B = (Button) view.findViewById(R.id.cancel_B);
                save_B.setOnClickListener(this);
                cancel_B.setOnClickListener(this);
            } else {
                editButtonsContainerView.setVisibility(View.GONE);
            }
        }

        // Setup
        adapter = createAdapter();
        if (mode == Mode.MULTI_SELECT) {
            final Set<BaseModel> selectedModelsSet = new HashSet<>();
            for (Parcelable parcelable : selectedModels) {
                selectedModelsSet.add((BaseModel) parcelable);
            }
            adapter.setSelectedModels(selectedModelsSet);
        }
        prepareView(view, adapter);
    }

    public void onModelSelected(BaseModel model) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MODEL, model);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onModelsSelected(Set<BaseModel> models) {
        final Intent data = new Intent();
        final Parcelable[] parcelables = new Parcelable[models.size()];
        int index = 0;
        for (BaseModel model : models) {
            parcelables[index++] = model;
        }
        data.putExtra(RESULT_EXTRA_MODELS, parcelables);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onModelsSelectCanceled() {
        finish();
    }

    protected abstract BaseModelsAdapterOld createAdapter();

    protected abstract CursorLoader getModelsCursorLoader();

    protected abstract BaseModel modelFrom(Cursor cursor);

    protected abstract void onModelClick(View view, int position, String modelId, BaseModel model);

    protected abstract void startModelEdit(String modelId);

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public static enum Mode {
        VIEW, SELECT, MULTI_SELECT
    }
}
