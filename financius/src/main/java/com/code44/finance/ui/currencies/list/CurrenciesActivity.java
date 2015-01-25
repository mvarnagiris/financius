package com.code44.finance.ui.currencies.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.utils.preferences.GeneralPrefs;

import javax.inject.Inject;

public class CurrenciesActivity extends BaseActivity {
    @Inject GeneralPrefs generalPrefs;
    @Inject CurrenciesApi currenciesApi;
    @Inject @Main CurrencyFormat mainCurrencyFormat;

    public static void start(Context context) {
        final Intent intent = makeIntentForActivity(context, CurrenciesActivity.class);
        CurrenciesActivityPresenter.addViewExtras(intent);
        startActivity(context, intent);
    }

    public static void startSelect(Activity activity, int requestCode) {
        final Intent intent = makeIntentForActivity(activity, CurrenciesActivity.class);
        CurrenciesActivityPresenter.addSelectExtras(intent);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_currencies);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CurrenciesActivityPresenter(getEventBus(), generalPrefs, currenciesApi, mainCurrencyFormat);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CurrencyList;
    }
}
