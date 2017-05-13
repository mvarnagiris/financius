package com.code44.finance.ui.common.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.model.Model;
import com.code44.finance.ui.common.adapters.ModelViewHolder;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.recycler.DividerDecoration;
import com.code44.finance.ui.common.views.EmptyContainerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ModelsActivity<M extends Model, A extends ModelsAdapter<M, ? extends ModelViewHolder>> extends DrawerActivity implements LoaderManager.LoaderCallbacks<Cursor>, ModelsAdapter.OnModelClickListener<M>, EmptyContainerView.Callbacks {
    static final String EXTRA_MODE = ModelsActivity.class.getName() + ".EXTRA_MODE";
    static final String EXTRA_SELECTED_MODELS = ModelsActivity.class.getName() + ".EXTRA_SELECTED_MODELS";
    private static final int LOADER_MODELS = 4124;
    private static final String RESULT_EXTRA_MODEL = "RESULT_EXTRA_MODEL";
    private static final String RESULT_EXTRA_MODELS = "RESULT_EXTRA_MODELS";

    private static final String STATE_SELECTED_MODELS = ModelsActivity.class.getName() + ".STATE_SELECTED_MODELS";

    private RecyclerView recyclerView;
    private EmptyContainerView emptyContainerView;

    private A adapter;
    private Mode mode;

    public static <T extends Parcelable> T getModelExtra(Intent data) {
        return data.getParcelableExtra(RESULT_EXTRA_MODEL);
    }

    public static <T extends Parcelable> List<T> getModelsExtra(Intent data) {
        final Parcelable[] parcelables = data.getParcelableArrayExtra(RESULT_EXTRA_MODELS);
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

        // Get extras
        mode = (Mode) getIntent().getSerializableExtra(EXTRA_MODE);

        // Get views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        emptyContainerView = (EmptyContainerView) findViewById(R.id.emptyContainerView);

        // Setup
        setupAdapter(savedInstanceState);
        setupRecyclerView(recyclerView);
        setupEmptyContainerView();
        setupEditButtons();

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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_SELECTED_MODELS, new ArrayList<Parcelable>(adapter.getSelectedModels()));
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            return getModelsCursorLoader();
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.setCursor(data);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.setCursor(null);
        }
    }

    @Override public void onModelClick(M model, int position) {
        if (mode == Mode.View) {
            onModelClick(model);
        } else if (mode == Mode.Select) {
            onModelSelected(model);
        } else {
            adapter.toggleModelSelected(model, position);
        }
    }

    @Override public void onEmptyAddClick(View v) {
        startModelEdit(null);
    }

    @LayoutRes protected abstract int getLayoutId();

    protected abstract A createAdapter(ModelsAdapter.OnModelClickListener<M> defaultOnModelClickListener, Mode mode);

    protected abstract CursorLoader getModelsCursorLoader();

    protected abstract void onModelClick(M model);

    protected abstract void startModelEdit(String modelId);

    protected void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupRecyclerViewDecorations(recyclerView);
    }

    protected void setupRecyclerViewDecorations(RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new DividerDecoration(this));
    }

    protected Mode getMode() {
        return mode;
    }

    public A getAdapter() {
        return adapter;
    }

    protected int getModelsLoaderId() {
        return LOADER_MODELS;
    }

    private void setupAdapter(Bundle savedInstanceState) {
        final Parcelable[] selectedModels = getIntent().getParcelableArrayExtra(EXTRA_SELECTED_MODELS);

        adapter = createAdapter(this, mode);
        if (mode == Mode.MultiSelect) {
            final Set<M> selectedModelsSet = new HashSet<>();
            final Parcelable[] parcelables;
            if (savedInstanceState == null) {
                parcelables = selectedModels;
            } else {
                final List<Parcelable> parcelableList = savedInstanceState.getParcelableArrayList(STATE_SELECTED_MODELS);
                parcelables = new Parcelable[parcelableList.size()];
                parcelableList.toArray(parcelables);
            }

            for (Parcelable parcelable : parcelables) {
                //noinspection unchecked
                selectedModelsSet.add((M) parcelable);
            }
            adapter.setSelectedModels(selectedModelsSet);
        }
    }

    private void setupEmptyContainerView() {
        if (emptyContainerView != null) {
            emptyContainerView.setCallbacks(this);
            adapter.registerAdapterDataObserver(new AdapterObserver());
        }
    }

    private void setupEditButtons() {
        final View editButtonsContainerView = findViewById(R.id.editButtonsContainerView);
        if (editButtonsContainerView != null) {
            if (mode == Mode.MultiSelect) {
                editButtonsContainerView.setVisibility(View.VISIBLE);
                final Button saveButton = (Button) editButtonsContainerView.findViewById(R.id.saveButton);
                final Button cancelButton = (Button) editButtonsContainerView.findViewById(R.id.cancelButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        onMultipleModelsSelected(adapter.getSelectedModels());
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        onMultipleModelsSelectCanceled();
                    }
                });
            } else {
                editButtonsContainerView.setVisibility(View.GONE);
            }
        }
    }

    private void onModelSelected(M model) {
        final Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MODEL, model);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void onMultipleModelsSelected(Set<M> selectedModels) {
        final Intent data = new Intent();
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (Model model : selectedModels) {
            parcelables[index++] = model;
        }
        data.putExtra(RESULT_EXTRA_MODELS, parcelables);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void onMultipleModelsSelectCanceled() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public enum Mode {
        View, Select, MultiSelect
    }

    private class AdapterObserver extends RecyclerView.AdapterDataObserver {
        @Override public void onChanged() {
            super.onChanged();
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }
    }
}
