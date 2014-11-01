package com.code44.finance.ui.accounts;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.ui.ModelEditActivityOld;
import com.code44.finance.ui.ModelFragment;

public class AccountEditActivity extends ModelEditActivityOld {
    public static void start(Context context, String accountServerId) {
        startActivity(context, makeIntent(context, AccountEditActivity.class, accountServerId));
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.account;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return AccountEditFragment.newInstance(modelServerId);
    }
}
