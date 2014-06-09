package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public abstract class ModelListActivity extends BaseActivity {
    public static Intent makeIntentModels(Context context, Class<? extends ModelListActivity> activityClass) {
        return makeIntent(context, activityClass);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarTitle(getActionBarTitleResId());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, createModelsFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    protected abstract int getActionBarTitleResId();

    protected abstract ModelListFragment<?> createModelsFragment();
}
