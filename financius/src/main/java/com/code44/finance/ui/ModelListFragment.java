package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.model.BaseModel;

public abstract class ModelListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    public static final String ARG_MODE = ModelListFragment.class.getName() + ".ARG_MODE";

    protected static final int LOADER_MODELS = 1000;

    protected BaseModelsAdapter adapter;
    protected ModelListActivity.Mode mode;

    private ModelListFragmentCallbacks callbacks;

    public static Bundle makeArgs(ModelListActivity.Mode mode) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        return args;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ModelListFragmentCallbacks) {
            callbacks = (ModelListFragmentCallbacks) activity;
        } else {
            throw new IllegalStateException(activity + " must implement " + ModelListFragmentCallbacks.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        mode = (ModelListActivity.Mode) getArguments().getSerializable(ARG_MODE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        adapter = createAdapter(getActivity());
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_MODELS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            Query query = getQuery();
            if (query == null) {
                query = Query.create();
            }
            return query.asCursorLoader(getActivity(), getUri());
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
        callbacks.onModelClickListener(view, position, id, modelFrom(adapter.getCursor()));
    }

    protected abstract BaseModelsAdapter createAdapter(Context context);

    protected abstract Uri getUri();

    protected abstract Query getQuery();

    protected abstract BaseModel modelFrom(Cursor cursor);

    protected ModelListActivity.Mode getMode() {
        return mode;
    }

    public static interface ModelListFragmentCallbacks {
        public void onModelClickListener(View view, int position, long modelId, BaseModel model);
    }
}