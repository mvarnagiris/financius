package com.code44.finance.ui.currencies;

import android.app.Fragment;
import android.content.Context;
import android.view.Menu;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class CurrenciesActivity extends ModelListActivity {
    public static void start(Context context) {
        start(context, makeIntentView(context, CurrenciesActivity.class));
    }

    public static void startSelect(Fragment fragment, int requestCode) {
        startForResult(fragment, makeIntentSelect(fragment.getActivity(), CurrenciesActivity.class), requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.currencies;
    }

    @Override
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode) {
        return CurrenciesFragment.newInstance(mode);
    }
}
