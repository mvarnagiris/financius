package com.code44.finance.ui.common.presenters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.data.model.Model;
import com.code44.finance.ui.common.adapters.ModelsAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ModelsPresenter<M extends Model> extends RecyclerViewPresenter<ModelsAdapter<M>> implements LoaderManager.LoaderCallbacks<Cursor>, ModelsAdapter.OnModelClickListener<M> {
    private static final String EXTRA_MODE = ModelsPresenter.class.getName() + ".EXTRA_MODE";
    private static final String EXTRA_SELECTED_MODELS = ModelsPresenter.class.getName() + ".EXTRA_SELECTED_MODELS";

    private static final int LOADER_MODELS = 4124;

    private final Mode mode;
    private final Parcelable[] selectedModels;

    public ModelsPresenter(ActionBarActivity activity) {
        super(activity);
        mode = (Mode) activity.getIntent().getSerializableExtra(EXTRA_MODE);
        selectedModels = activity.getIntent().getParcelableArrayExtra(EXTRA_SELECTED_MODELS);

        activity.getSupportLoaderManager().initLoader(LOADER_MODELS, null, this);
    }

    public static void addViewExtras(Intent intent) {
        intent.putExtra(EXTRA_MODE, ModelsPresenter.Mode.View);
    }

    public static void addSelectExtras(Intent intent) {
        intent.putExtra(EXTRA_MODE, ModelsPresenter.Mode.Select);
    }

    public static void addMultiSelectExtras(Intent intent, List<? extends Model> selectedModels) {
        intent.putExtra(EXTRA_MODE, ModelsPresenter.Mode.MultiSelect);
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (Model model : selectedModels) {
            parcelables[index++] = model;
        }
        intent.putExtra(EXTRA_SELECTED_MODELS, parcelables);
    }

    @Override protected void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    }

    @Override protected ModelsAdapter<M> createAdapter() {
        return createAdapter(this);
    }

    @Override protected void setupAdapter(ModelsAdapter<M> adapter) {
        adapter.setMode(mode);
        if (mode == ModelsPresenter.Mode.MultiSelect) {
            final Set<M> selectedModelsSet = new HashSet<>();
            for (Parcelable parcelable : selectedModels) {
                //noinspection unchecked
                selectedModelsSet.add((M) parcelable);
            }
            adapter.setSelectedModels(selectedModelsSet);
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            return getModelsCursorLoader(getRecyclerView().getContext());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            getAdapter().setCursor(data);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_MODELS) {
            getAdapter().setCursor(null);
        }
    }

    @Override public void onModelClick(View view, M model, Cursor cursor, int position, Mode mode, boolean isSelected) {
        if (mode == ModelsPresenter.Mode.View) {
            onModelClick(view.getContext(), view, model, cursor, position);
        } else if (mode == ModelsPresenter.Mode.Select) {
            // TODO onModelSelected(model);
        } else {
            // TODO adapter.toggleModelSelected(modelFrom(adapter.getCursor()));
        }
    }

    protected abstract ModelsAdapter<M> createAdapter(ModelsAdapter.OnModelClickListener<M> defaultOnModelClickListener);

    protected abstract CursorLoader getModelsCursorLoader(Context context);

    protected abstract void onModelClick(Context context, View view, M model, Cursor cursor, int position);

    public static enum Mode {
        View, Select, MultiSelect
    }
}
