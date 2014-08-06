package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.db.model.BaseModel;

public abstract class ModelEditFragment<T extends BaseModel> extends ModelFragment<T> {
    private static final String STATE_MODEL = "STATE_MODEL";

    private ModelEditFragmentListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof ModelEditFragmentListener) {
            listener = (ModelEditFragmentListener) activity;
        } else {
            throw new IllegalStateException(activity.getClass().getName() + " must implement " + ModelEditFragmentListener.class.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore state
        if (savedInstanceState != null) {
            model = savedInstanceState.getParcelable(STATE_MODEL);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final Button cancel_B = (Button) view.findViewById(R.id.cancel_B);
        final Button save_B = (Button) view.findViewById(R.id.save_B);

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (model != null) {
            onModelLoaded(model);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ensureModelUpdated(model);
        outState.putParcelable(STATE_MODEL, model);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODEL && model == null) {
            super.onLoadFinished(loader, data);
        }
    }

    protected abstract boolean onSave(Context context, T model);

    protected abstract void ensureModelUpdated(T model);

    private void save() {
        ensureModelUpdated(model);
        if (onSave(getActivity(), model)) {
            listener.onModelSaved();
        }
    }

    private void cancel() {
        listener.onModelCanceled();
    }

    public static interface ModelEditFragmentListener {
        public void onModelSaved();

        public void onModelCanceled();
    }
}
