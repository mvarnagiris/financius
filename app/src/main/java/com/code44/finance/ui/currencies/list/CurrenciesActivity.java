package com.code44.finance.ui.currencies.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.UpdateExchangeRatesRequest;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.currencies.detail.CurrencyActivity;
import com.code44.finance.ui.currencies.edit.CurrencyEditActivity;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class CurrenciesActivity extends ModelsActivity<CurrencyFormat, CurrenciesAdapter> implements SwipeRefreshLayout.OnRefreshListener, CompoundButton.OnCheckedChangeListener {
    @Inject GeneralPrefs generalPrefs;
    @Inject CurrenciesApi currenciesApi;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public static void startView(Context context) {
        ActivityStarter.begin(context, CurrenciesActivity.class).modelsView().start();
    }

    public static void startSelect(Context context, int requestCode) {
        ActivityStarter.begin(context, CurrenciesActivity.class).modelsSelect().startForResult(requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get views
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        final View settingsContainerView = findViewById(R.id.settingsContainerView);
        final SwitchCompat autoUpdateCurrenciesSwitch = (SwitchCompat) findViewById(R.id.autoUpdateCurrenciesSwitch);

        // Setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.progress_bar_colors));
        autoUpdateCurrenciesSwitch.setChecked(generalPrefs.isAutoUpdateCurrencies());
        autoUpdateCurrenciesSwitch.setOnCheckedChangeListener(this);
        if (getMode() != Mode.View) {
            settingsContainerView.setVisibility(View.GONE);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.currencies, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_rates:
                onRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_currencies;
    }

    @Override protected CurrenciesAdapter createAdapter(ModelsAdapter.OnModelClickListener<CurrencyFormat> defaultOnModelClickListener, Mode mode) {
        return new CurrenciesAdapter(defaultOnModelClickListener, mode, this, currenciesManager, amountFormatter);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.CurrencyFormats.getQuery()
                .clearSort()
                .sortOrder("case when " + Tables.CurrencyFormats.CODE + "=\"" + currenciesManager.getMainCurrencyCode() + "\" then 0 else 1 end")
                .sortOrder(Tables.CurrencyFormats.CODE.getName())
                .asCursorLoader(this, CurrenciesProvider.uriCurrencies());
    }

    @Override protected void onModelClick(CurrencyFormat model) {
        CurrencyActivity.start(this, model.getId());
    }

    @Override protected void startModelEdit(String modelId) {
        CurrencyEditActivity.start(this, modelId);
    }

    @Override public void onRefresh() {
        currenciesApi.updateExchangeRates();
        setRefreshing(true);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        generalPrefs.setAutoUpdateCurrencies(isChecked);
        generalPrefs.notifyChanged();
        if (isChecked) {
            onRefresh();
        }
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.CurrencyList;
    }

    @Subscribe public void onRefreshFinished(UpdateExchangeRatesRequest request) {
        setRefreshing(false);
    }

    private void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }
}
