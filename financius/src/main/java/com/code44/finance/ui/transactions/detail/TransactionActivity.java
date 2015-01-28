package com.code44.finance.ui.transactions.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

import javax.inject.Inject;

public class TransactionActivity extends BaseActivity {
    @Inject AmountFormatter amountFormatter;

    public static void start(Context context, String transactionId) {
        final Intent intent = makeIntentForActivity(context, TransactionActivity.class);
        TransactionActivityPresenter.addExtras(intent, transactionId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_transaction);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new TransactionActivityPresenter(getEventBus(), amountFormatter);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Transaction;
    }
}
