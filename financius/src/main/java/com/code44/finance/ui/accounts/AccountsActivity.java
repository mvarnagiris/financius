package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.analytics.Analytics;

public class AccountsActivity extends ModelListActivity {
    public static Intent makeIntentView(Context context) {
        return makeIntentView(context, AccountsActivity.class);
    }

    public static void startSelect(Fragment fragment, int requestCode) {
        startActivityForResult(fragment, makeIntentSelect(fragment.getActivity(), AccountsActivity.class), requestCode);
    }

    @Override
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode, Parcelable[] selectedModels) {
        return AccountsFragment.newInstance(mode);
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return NavigationAdapter.NavigationScreen.Accounts;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.AccountList;
    }
}
