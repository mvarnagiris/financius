package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class AccountsActivity extends ModelListActivity {
    public static void start(Context context, View expandFrom) {
        final Intent intent = makeIntent(context, AccountsActivity.class);
        start(context, intent, expandFrom);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.accounts;
    }

    @Override
    protected ModelListFragment createModelsFragment(int mode) {
        return AccountsFragment.newInstance(mode);
    }
}
