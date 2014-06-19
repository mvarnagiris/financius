package com.code44.finance.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.currencies.CurrenciesActivity;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    public static void start(Context context, View expandFrom) {
        start(context, makeIntent(context, SettingsActivity.class), expandFrom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setActionBarTitle(R.string.settings);

        // Setup
        findViewById(R.id.currencies_B).setOnClickListener(this);
        findViewById(R.id.categories_B).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.currencies_B:
                CurrenciesActivity.start(this, view);
                break;

            case R.id.categories_B:
                CategoriesActivity.start(this, view);
                break;
        }
    }
}
