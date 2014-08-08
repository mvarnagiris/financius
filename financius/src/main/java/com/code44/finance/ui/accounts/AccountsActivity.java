package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class AccountsActivity extends ModelListActivity {
    public static void start(Context context, View expandFrom) {
        final Intent intent = makeIntentView(context, AccountsActivity.class);
        startScaleUp(context, intent, expandFrom);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.accounts;
    }

    @Override
    protected ModelListFragment createModelsFragment(Mode mode) {
        return AccountsFragment.newInstance(mode);
    }

    @Override
    protected void startModelActivity(View expandFrom, long modelId) {
        AccountActivity.start(this, expandFrom, modelId);
    }

    @Override
    protected void startModelEditActivity(View expandFrom, long modelId) {
        AccountEditActivity.start(this, expandFrom, modelId);
    }

    @Override
    protected int inflateActivity() {
        setContentView(R.layout.activity_drawer);
        return R.id.content_V;
    }
}
