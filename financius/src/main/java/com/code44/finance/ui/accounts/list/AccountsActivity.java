package com.code44.finance.ui.accounts.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.activities.BaseDrawerActivity;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

import javax.inject.Inject;

public class AccountsActivity extends BaseDrawerActivity {
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    public static Intent makeViewIntent(Context context) {
        final Intent intent = makeIntentForActivity(context, AccountsActivity.class);
        AccountsActivityPresenter.addViewExtras(intent);
        return intent;
    }

    public static void startSelect(Activity activity, int requestCode) {
        final Intent intent = makeIntentForActivity(activity, AccountsActivity.class);
        AccountsActivityPresenter.addSelectExtras(intent);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        final ModelsActivityPresenter.Mode mode = (ModelsActivityPresenter.Mode) getIntent().getSerializableExtra(ModelsActivityPresenter.EXTRA_MODE);
        if (mode == ModelsActivityPresenter.Mode.View) {
            setShowDrawer(true);
            setShowDrawerToggle(true);
        }
        super.onCreate(savedInstanceState);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_accounts);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new AccountsActivityPresenter(currenciesManager, amountFormatter);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.Accounts;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.AccountList;
    }
}
