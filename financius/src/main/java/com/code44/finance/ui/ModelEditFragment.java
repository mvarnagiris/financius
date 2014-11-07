package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.model.Model;

public abstract class ModelEditFragment<T extends Model> extends ModelFragment<T> {
    private static final String STATE_MODEL = "STATE_MODEL";

    private ModelEditListener listener;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ModelEditListener) {
            listener = (ModelEditListener) activity;
        } else {
            throw new IllegalStateException(activity.getClass().getName() + " must implement " + ModelEditListener.class.getName());
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        // Restore state
        if (savedInstanceState != null) {
            model = savedInstanceState.getParcelable(STATE_MODEL);
        }
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final Button cancel_B = (Button) view.findViewById(R.id.cancelButton);
        final Button save_B = (Button) view.findViewById(R.id.saveButton);

        // Setup
        cancel_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        save_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (model != null) {
            onModelLoaded(model);
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ensureModelUpdated(model);
        outState.putParcelable(STATE_MODEL, model);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODEL && model == null) {
            super.onLoadFinished(loader, data);
        }
    }

    @Override protected Uri getDeleteUri() {
        // Ignore
        return null;
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        // Ignore
        return null;
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        // Ignore
    }

    protected abstract boolean onSave(Context context, T model);

    protected abstract void ensureModelUpdated(T model);

    protected boolean isNewModel() {
        return modelId.equals("0");
    }

    private void save() {
        ensureModelUpdated(model);
        if (onSave(getActivity(), model)) {
            listener.onModelSaved();
        }
    }

    private void cancel() {
        listener.onModelCanceled();
    }

    public static interface ModelEditListener {
        public void onModelSaved();

        public void onModelCanceled();
    }
}
