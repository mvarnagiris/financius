package com.code44.finance.ui.currencies.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.data.model.Currency;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

import javax.inject.Inject;

public class CurrencyEditActivity extends BaseActivity {
    @Inject CurrenciesApi currenciesApi;
    @Inject @Main Currency mainCurrency;

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
        return new CurrencyEditActivityPresenter(getEventBus(), currenciesApi, mainCurrency);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CurrencyEdit;
    }
}
