package com.code44.finance.ui.accounts;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class AccountsActivity extends ModelListActivity {
    public static void startSelect(Fragment fragment, int requestCode) {
        startForResult(fragment, makeIntentSelect(fragment.getActivity(), AccountsActivity.class), requestCode);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.accounts;
    }

    @Override
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode, Parcelable[] selectedModels) {
        return AccountsFragment.newInstance(mode);
    }
}
