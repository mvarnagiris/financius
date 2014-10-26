package com.code44.finance.ui.currencies;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class CurrenciesActivity extends ModelListActivity {
    public static void start(Context context) {
        startActivity(context, makeIntentView(context, CurrenciesActivity.class));
    }

    public static void startSelect(Fragment fragment, int requestCode) {
        startActivityForResult(fragment, makeIntentSelect(fragment.getActivity(), CurrenciesActivity.class), requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode, Parcelable[] selectedModels) {
        return CurrenciesFragment.newInstance(mode);
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return null;
    }
}
