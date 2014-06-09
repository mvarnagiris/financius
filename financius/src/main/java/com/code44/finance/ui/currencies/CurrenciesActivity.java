package com.code44.finance.ui.currencies;

import android.content.Context;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class CurrenciesActivity extends ModelListActivity {
    public static void start(Context context, View expandFrom) {
        start(context, makeIntentModels(context, CurrenciesActivity.class), expandFrom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.currencies;
    }

    @Override
    protected ModelListFragment<?> createModelsFragment() {
        return CurrenciesFragment.newInstance();
    }
}
