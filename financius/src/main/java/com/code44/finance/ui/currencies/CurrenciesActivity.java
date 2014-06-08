package com.code44.finance.ui.currencies;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.BaseActivity;

public class CurrenciesActivity extends BaseActivity {
    public static void start(Context context, View expandFrom) {
        start(context, makeIntent(context, CurrenciesActivity.class), expandFrom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarTitle(R.string.currencies);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, CurrenciesFragment.newInstance()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
