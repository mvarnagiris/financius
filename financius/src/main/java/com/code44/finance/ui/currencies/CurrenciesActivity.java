package com.code44.finance.ui.currencies;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class CurrenciesActivity extends ModelListActivity {
    public static void start(Context context, View expandFrom) {
        startScaleUp(context, makeIntentView(context, CurrenciesActivity.class), expandFrom);
    }

    public static void startSelect(Fragment fragment, int requestCode) {
        startForResult(fragment, makeIntentSelect(fragment.getActivity(), CurrenciesActivity.class), requestCode);
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
    protected ModelListFragment createModelsFragment(int mode) {
        return CurrenciesFragment.newInstance(mode);
    }
}
