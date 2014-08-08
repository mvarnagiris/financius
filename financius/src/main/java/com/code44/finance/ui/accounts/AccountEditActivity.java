package com.code44.finance.ui.accounts;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.ui.ModelEditActivity;
import com.code44.finance.ui.ModelFragment;

public class AccountEditActivity extends ModelEditActivity {
    public static void start(Context context, long accountId) {
        start(context, makeIntent(context, AccountEditActivity.class, accountId));
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.account;
    }

    @Override
    protected ModelFragment createModelFragment(long modelId) {
        return AccountEditFragment.newInstance(modelId);
    }
}
