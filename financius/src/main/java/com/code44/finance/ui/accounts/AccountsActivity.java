package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class AccountsActivity extends ModelListActivity {
    public static Intent makeIntentView(Context context) {
        return makeIntentView(context, AccountsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        AccountActivity.start(this, modelId);
    }

    @Override
    protected void startModelEditActivity(View expandFrom, long modelId) {
        AccountEditActivity.start(this, modelId);
    }

    @Override
    protected int inflateActivity() {
        setContentView(R.layout.activity_drawer);
        return R.id.content_V;
    }
}
