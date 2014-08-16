package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class TransactionActivity extends ModelActivity {
    public static void start(Context context, String transactionServerId) {
        final Intent intent = makeIntent(context, TransactionActivity.class, transactionServerId);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        toolbarHelper.setElevation(0);
    }

    @Override protected int getActionBarTitleResId() {
        return R.string.transaction;
    }

    @Override protected ModelFragment createModelFragment(String modelServerId) {
        return TransactionFragment.newInstance(modelServerId);
    }
}
