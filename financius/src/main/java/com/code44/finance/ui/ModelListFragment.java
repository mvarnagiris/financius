package com.code44.finance.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.data.model.BaseModel;

public abstract class ModelListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    public static final String ARG_MODE = ModelListFragment.class.getName() + ".ARG_MODE";

    protected static final int LOADER_MODELS = 1000;

    protected BaseModelsAdapter adapter;
    protected Mode mode;

    private ModelListCallback callback;

    public static Bundle makeArgs(Mode mode) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        return args;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ModelListCallback) {
            callback = (ModelListCallback) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get arguments
        mode = (Mode) getArguments().getSerializable(ARG_MODE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup
        adapter = createAdapter(getActivity());
        prepareView(view, adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_MODELS, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.models, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startModelEdit(getActivity(), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            return getModelsCursorLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_MODELS) {
            adapter.swapCursor(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final BaseModel model = modelFrom(adapter.getCursor());
        if (mode == Mode.VIEW) {
            onModelClick(getActivity(), view, position, model.getServerId(), model);
        } else if (callback != null) {
            callback.onModelSelected(model.getServerId(), model);
        }
    }

    protected abstract BaseModelsAdapter createAdapter(Context context);

    protected abstract CursorLoader getModelsCursorLoader(Context context);

    protected abstract BaseModel modelFrom(Cursor cursor);

    protected abstract void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model);

    protected abstract void startModelEdit(Context context, String modelServerId);

    protected void prepareView(View view, BaseModelsAdapter adapter) {
        // Get views
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
    }

    protected Mode getMode() {
        return mode;
    }

    public static enum Mode {
        VIEW, SELECT
    }

    public static interface ModelListCallback {
        public void onModelSelected(String modelServerId, BaseModel model);
    }
}