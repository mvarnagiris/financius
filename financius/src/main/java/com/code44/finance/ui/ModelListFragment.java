package com.code44.finance.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;

public abstract class ModelListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int LOADER_MODELS = 1000;

    private BaseModelsAdapter adapter;

    public static Bundle makeArgs() {
        return new Bundle();
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
            return new CursorLoader(getActivity(), getUri(), getProjection(), getSelection(), getSelectionArgs(), getSortOrder());
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
        startModelActivity(getActivity(), view, id);
    }

    protected abstract void startModelActivity(Context context, View expandFrom, long modelId);

    protected abstract BaseModelsAdapter createAdapter(Context context);

    protected abstract Uri getUri();

    protected String[] getProjection() {
        return null;
    }

    protected String getSelection() {
        return null;
    }

    protected String[] getSelectionArgs() {
        return null;
    }

    protected String getSortOrder() {
        return null;
    }
}