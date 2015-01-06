package com.code44.finance.ui.common.presenters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;

import com.code44.finance.data.model.Model;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.utils.EventBus;

public abstract class ModelEditActivityPresenter<M extends Model> extends ModelActivityPresenter<M> {
    private M storedModel;

    public ModelEditActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
    }

    @Override public boolean onActivityCreateOptionsMenu(BaseActivity activity, Menu menu) {
        return false;
    }

    @Override protected void onModelLoaded(M model) {
        storedModel = model;
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

    protected M getStoredModel() {
        return storedModel;
    }

    protected abstract void onDataChanged(M storedModel);

    protected abstract boolean onSave();

    private void save() {
        if (onSave()) {
            getActivity().finish();
        }
    }

    private void cancel() {
        getActivity().finish();
    }
}
