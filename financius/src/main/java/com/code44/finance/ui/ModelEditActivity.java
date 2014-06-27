package com.code44.finance.ui;

import android.view.Menu;

public abstract class ModelEditActivity extends ModelActivity implements ModelEditFragment.ModelEditFragmentListener {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    @Override
    protected void startEditActivity(long modelId) {
        // Do nothing
    }

    @Override
    public void onModelSaved() {
        finish();
    }

    @Override
    public void onModelCanceled() {
        finish();
    }
}