package com.code44.finance.ui.accounts;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class AccountActivity extends ModelActivity {
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
