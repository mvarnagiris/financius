package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.ui.accounts.AccountFragment;

public class TransactionActivity extends ModelActivity {
    public static void start(Context context, String transactionServerId) {
        final Intent intent = makeIntent(context, TransactionActivity.class, transactionServerId);
        start(context, intent);
    }

    @Override protected int getActionBarTitleResId() {
        return R.string.transaction;
    }

    @Override protected ModelFragment createModelFragment(String modelServerId) {
        // TODO
        return AccountFragment.newInstance(modelServerId);
    }
}
