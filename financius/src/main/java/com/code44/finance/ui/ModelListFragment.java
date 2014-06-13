package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.model.BaseModel;

public abstract class ModelListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    protected static final int LOADER_MODELS = 1000;

    public static final String ARG_MODE = "ARG_MODE";

    protected BaseModelsAdapter adapter;

    private ModelListFragmentCallbacks callbacks;
    private boolean isSelectMode;

    public static Bundle makeArgs(int mode) {
        final Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode);
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
        setHasOptionsMenu(true);

        // Get arguments
        isSelectMode = getArguments().getInt(ARG_MODE, ModelListActivity.MODE_VIEW) == ModelListActivity.MODE_SELECT;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.models, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                startModelEditActivity(getActivity(), null, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_MODELS) {
            Query query = getQuery();
            if (query == null) {
                query = Query.get();
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
        if (isSelectMode()) {
            callbacks.onModelSelected(id, modelFrom(adapter.getCursor()));
        } else {
            startModelActivity(getActivity(), view, id);
        }
    }

    protected abstract void startModelActivity(Context context, View expandFrom, long modelId);

    protected abstract void startModelEditActivity(Context context, View expandFrom, long modelId);

    protected abstract BaseModelsAdapter createAdapter(Context context);

    protected abstract Uri getUri();

    protected abstract BaseModel modelFrom(Cursor cursor);

    protected Query getQuery() {
        return null;
    }

    protected boolean isSelectMode() {
        return isSelectMode;
    }

    static interface ModelListFragmentCallbacks {
        public void onModelSelected(long id, BaseModel model);
    }
}