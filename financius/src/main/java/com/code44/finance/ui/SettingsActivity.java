package com.code44.finance.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.adapters.SettingsAdapter;
import com.code44.finance.ui.categories.CategoriesActivity;
import com.code44.finance.ui.currencies.CurrenciesActivity;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static void start(Context context, View expandFrom) {
        startScaleUp(context, makeIntent(context, SettingsActivity.class), expandFrom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbarHelper.setTitle(R.string.settings);

        // Get views
        final ListView list_V = (ListView) findViewById(R.id.list_V);

        // Setup
        final SettingsAdapter adapter = new SettingsAdapter(this);
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == SettingsAdapter.ID_CURRENCIES) {
            CurrenciesActivity.start(this, view);
        } else if (id == SettingsAdapter.ID_CATEGORIES) {
            CategoriesActivity.start(this, view);
        }
    }
}
