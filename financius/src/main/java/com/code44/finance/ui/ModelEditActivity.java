package com.code44.finance.ui;

import android.os.Bundle;
import android.view.Menu;

public abstract class ModelEditActivity extends ModelActivity implements ModelEditFragment.ModelEditListener {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override public void onModelSaved() {
        finish();
    }

    @Override public void onModelCanceled() {
        finish();
    }
}