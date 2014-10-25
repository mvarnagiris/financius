package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.data.model.BaseModel;

import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public abstract class ModelListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, View.OnClickListener {
    public static final String ARG_MODE = ModelListFragment.class.getName() + ".ARG_MODE";
    public static final String ARG_SELECTED_MODELS = ModelListFragment.class.getName() + ".ARG_SELECTED_MODELS";

    protected static final int LOADER_MODELS = 1000;

    protected BaseModelsAdapter adapter;
    protected Mode mode;

    private OnModelSelectedListener onModelSelectedListener;
    private OnModelsSelectedListener onModelsSelectedListener;

    @Optional @InjectView(R.id.list_V) protected ListView list_V;
    @Optional @InjectView(R.id.editButtonsContainer_V) protected View editButtonsContainer_V;
    @Optional @InjectView(R.id.empty_V) protected View empty_V;

    @Optional @InjectView(R.id.save_B) protected Button save_B;
    @Optional @InjectView(R.id.cancel_B) protected Button cancel_B;

    public static Bundle makeArgs(Mode mode, Parcelable[] selectedModels) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        args.putParcelableArray(ARG_SELECTED_MODELS, selectedModels);
        return args;
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnModelSelectedListener) {
            onModelSelectedListener = (OnModelSelectedListener) activity;
        }

        if (activity instanceof OnModelsSelectedListener) {
            onModelsSelectedListener = (OnModelsSelectedListener) activity;
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get arguments
        mode = (Mode) getArguments().getSerializable(ARG_MODE);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        // Setup
        adapter = createAdapter(getActivity());
        if (mode == Mode.MULTI_SELECT) {
            final Set<BaseModel> selectedModels = new HashSet<>();
            final Parcelable[] selectedModelsArray = getArguments().getParcelableArray(ARG_SELECTED_MODELS);
            for (Parcelable parcelable : selectedModelsArray) {
                selectedModels.add((BaseModel) parcelable);
            }
            adapter.setSelectedModels(selectedModels);
        }
        prepareView(view, adapter);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_MODELS, null, this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.models, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startModelEdit(getActivity(), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            return getModelsCursorLoader(getActivity());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.swapCursor(data);
            setListShown(!adapter.isEmpty());
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.swapCursor(null);
        }
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_B:
                onSaveMultiChoice(adapter.getSelectedModels());
                break;
            case R.id.cancel_B:
                onCancelMultiChoice();
                break;
        }
    }

    @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final BaseModel model = modelFrom(adapter.getCursor());
        if (mode == Mode.VIEW) {
            onModelClick(getActivity(), view, position, model.getId(), model);
        } else if (mode == Mode.SELECT) {
            if (onModelSelectedListener != null) {
                onModelSelectedListener.onModelSelected(model);
            }
        } else {
            adapter.toggleModelSelected(modelFrom(adapter.getCursor()));
        }
    }

    protected abstract BaseModelsAdapter createAdapter(Context context);

    protected abstract CursorLoader getModelsCursorLoader(Context context);

    protected abstract BaseModel modelFrom(Cursor cursor);

    protected abstract void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model);

    protected abstract void startModelEdit(Context context, String modelServerId);

    protected void prepareView(View view, BaseModelsAdapter adapter) {
        // Setup
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
        if (editButtonsContainer_V != null) {
            if (mode == Mode.MULTI_SELECT) {
                editButtonsContainer_V.setVisibility(View.VISIBLE);
                save_B.setOnClickListener(this);
                cancel_B.setOnClickListener(this);
            } else {
                editButtonsContainer_V.setVisibility(View.GONE);
            }
        }

        setListShown(!adapter.isEmpty());
    }

    protected void setListShown(boolean shown) {

        if (empty_V == null || list_V == null) return;

        if (shown) {
            empty_V.setVisibility(View.GONE);
            list_V.setVisibility(View.VISIBLE);
        } else {
            empty_V.setVisibility(View.VISIBLE);
            list_V.setVisibility(View.GONE);
        }
    }

    protected Mode getMode() {
        return mode;
    }

    protected void onSaveMultiChoice(Set<BaseModel> selectedModels) {
        if (onModelsSelectedListener != null) {
            onModelsSelectedListener.onModelsSelected(selectedModels);
        }
    }

    protected void onCancelMultiChoice() {
        if (onModelsSelectedListener != null) {
            onModelsSelectedListener.onModelsSelectCanceled();
        }
    }

    public static enum Mode {
        VIEW, SELECT, MULTI_SELECT
    }

    public static interface OnModelSelectedListener {
        public void onModelSelected(BaseModel model);
    }

    public static interface OnModelsSelectedListener {
        public void onModelsSelected(Set<BaseModel> models);

        public void onModelsSelectCanceled();
    }
}