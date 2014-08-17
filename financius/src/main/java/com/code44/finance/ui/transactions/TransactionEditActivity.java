package com.code44.finance.ui.transactions;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.ui.ModelEditActivity;
import com.code44.finance.ui.ModelFragment;

public class TransactionEditActivity extends ModelEditActivity {
    public static void start(Context context, String transactionServerId) {
        start(context, makeIntent(context, TransactionEditActivity.class, transactionServerId));
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.transaction;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return TransactionEditFragment.newInstance(modelServerId);
    }
}
