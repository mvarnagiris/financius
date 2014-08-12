package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class AccountActivity extends ModelActivity {
    public static void start(Context context, String accountServerId) {
        final Intent intent = makeIntent(context, AccountActivity.class, accountServerId);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbarHelper.setElevation(0);
    }

    @Override protected int getActionBarTitleResId() {
        return R.string.account;
    }

    @Override protected ModelFragment createModelFragment(String modelServerId) {
        return AccountFragment.newInstance(modelServerId);
    }
}
