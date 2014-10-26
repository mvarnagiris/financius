package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class AccountActivity extends ModelActivity {
    public static void start(Context context, String accountServerId) {
        final Intent intent = makeIntent(context, AccountActivity.class, accountServerId);
        startActivity(context, intent);
    }

    @Override protected int getActionBarTitleResId() {
        return R.string.account;
    }

    @Override protected ModelFragment createModelFragment(String modelServerId) {
        return AccountFragment.newInstance(modelServerId);
    }
}
