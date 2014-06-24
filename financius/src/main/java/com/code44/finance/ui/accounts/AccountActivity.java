package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class AccountActivity extends ModelActivity {
    public static void start(Context context, View expandFrom, long accountId) {
        final Intent intent = makeIntent(context, AccountActivity.class, accountId);
        start(context, intent, expandFrom);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.account;
    }

    @Override
    protected ModelFragment createModelFragment(long modelId) {
        return AccountFragment.newInstance(modelId);
    }

    @Override
    protected void startEditActivity(long modelId) {
        AccountEditActivity.start(this, null, modelId);
    }
}
