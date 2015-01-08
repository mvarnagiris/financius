package com.code44.finance.ui.currencies.list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRatesRequest;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.currencies.CurrencyActivity;
import com.code44.finance.ui.currencies.CurrencyEditActivity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.GeneralPrefs;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

class CurrenciesActivityPresenter extends ModelsActivityPresenter<Currency> implements CompoundButton.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener {
    private final EventBus eventBus;
    private final GeneralPrefs generalPrefs;
    private final CurrenciesApi currenciesApi;
    private final Currency mainCurrency;
    private final List<Currency> currencies = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    CurrenciesActivityPresenter(EventBus eventBus, GeneralPrefs generalPrefs, CurrenciesApi currenciesApi, Currency mainCurrency) {
        this.eventBus = eventBus;
        this.generalPrefs = generalPrefs;
        this.currenciesApi = currenciesApi;
        this.mainCurrency = mainCurrency;
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        swipeRefreshLayout = findView(activity, R.id.swipeRefreshLayout);
        final View settingsContainerView = findView(activity, R.id.settingsContainerView);
        final Switch autoUpdateCurrenciesSwitch = findView(activity, R.id.autoUpdateCurrenciesSwitch);

        // Setup
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(activity.getResources().getIntArray(R.array.progress_bar_colors));
        autoUpdateCurrenciesSwitch.setChecked(generalPrefs.isAutoUpdateCurrencies());
        autoUpdateCurrenciesSwitch.setOnCheckedChangeListener(this);
        if (getMode() != Mode.View) {
            settingsContainerView.setVisibility(View.GONE);
        }
    }

    @Override public void onActivityResumed(BaseActivity activity) {
        super.onActivityResumed(activity);
        eventBus.register(this);
    }

    @Override public void onActivityPaused(BaseActivity activity) {
        super.onActivityPaused(activity);
        eventBus.unregister(this);
    }

    @Override public boolean onActivityCreateOptionsMenu(BaseActivity activity, Menu menu) {
        super.onActivityCreateOptionsMenu(activity, menu);
        activity.getMenuInflater().inflate(R.menu.currencies, menu);
        return true;
    }

    @Override public boolean onActivityOptionsItemSelected(BaseActivity activity, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_rates:
                onRefresh();
                return true;
        }
        return super.onActivityOptionsItemSelected(activity, item);
    }

    @Override protected ModelsAdapter<Currency> createAdapter(ModelsAdapter.OnModelClickListener<Currency> defaultOnModelClickListener) {
        return new CurrenciesAdapter(defaultOnModelClickListener);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Currencies.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrencies());
    }

    @Override protected void onModelClick(Context context, View view, Currency model, Cursor cursor, int position) {
        CurrencyActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        CurrencyEditActivity.start(context, modelId);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        generalPrefs.setAutoUpdateCurrencies(isChecked);
        if (isChecked) {
            onRefresh();
        }
    }

    @Override public void onRefresh() {
        final List<String> fromCodes = new ArrayList<>();
        for (Currency currency : currencies) {
            if (!currency.isDefault()) {
                fromCodes.add(currency.getCode());
            }
        }

        if (!fromCodes.isEmpty()) {
            currenciesApi.updateExchangeRates(fromCodes, mainCurrency.getCode());
            setRefreshing(true);
        }
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_MODELS) {
            currencies.clear();
            if (data.moveToFirst()) {
                do {
                    currencies.add(Currency.from(data));
                } while (data.moveToNext());
            }
        }
        super.onLoadFinished(loader, data);
    }

    @Subscribe public void onRefreshFinished(ExchangeRatesRequest request) {
        setRefreshing(false);
    }

    private void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }
}
