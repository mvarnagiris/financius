package com.code44.finance.ui.common.presenters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.model.Model;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.utils.EventBus;

public abstract class ModelEditActivityPresenter<M extends Model> extends ModelActivityPresenter<M> {
    public ModelEditActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        // Get views
        final Button cancelView = findView(activity, R.id.cancelButton);
        final Button saveView = findView(activity, R.id.saveButton);

        // Setup
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    @Override public void onResume(BaseActivity activity) {
        super.onResume(activity);
        if (isNewModel()) {
            onDataChanged(getStoredModel());
        }
    }

    @Override public boolean onCreateOptionsMenu(BaseActivity activity, Menu menu) {
        return false;
    }

    @Override protected void onModelLoaded(M model) {
        onDataChanged(model);
    }

    @Override protected void startModelEdit(Context context, String modelId) {
    }

    @Override protected Uri getDeleteUri() {
        return null;
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return null;
    }

    protected abstract void onDataChanged(M storedModel);

    protected abstract boolean onSave();

    protected boolean isNewModel() {
        return getModelId().equals("0");
    }

    private void save() {
        if (onSave()) {
            getActivity().finish();
        }
    }

    private void cancel() {
        getActivity().finish();
    }
}
