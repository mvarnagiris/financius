package com.code44.finance.ui.transactions.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class TransactionEditActivity extends BaseActivity {
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;
    @Inject @Local ExecutorService localExecutor;

    public static void start(Context context, String transactionId) {
        final Intent intent = makeIntentForActivity(context, TransactionEditActivity.class);
        TransactionEditActivityPresenter.addExtras(intent, transactionId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new TransactionEditActivityPresenter(getEventBus(), localExecutor, currenciesManager, amountFormatter);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionEdit;
    }
}
