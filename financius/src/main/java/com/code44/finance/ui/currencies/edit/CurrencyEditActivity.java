package com.code44.finance.ui.currencies.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

public class CurrencyEditActivity extends BaseActivity {
    public static void start(Context context, String currencyId) {
        final Intent intent = makeIntentForActivity(context, CurrencyEditActivity.class);
        CurrencyEditActivityPresenter.addExtras(intent, currencyId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_currency_edit);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CurrencyEditActivityPresenter(getEventBus());
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CurrencyEdit;
    }
}
