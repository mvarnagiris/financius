package com.code44.finance.ui;

import android.os.Bundle;
import android.view.Menu;

public abstract class ModelEditActivity extends ModelActivity implements ModelEditFragment.ModelEditFragmentListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        getActionBar().hide();
    }

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