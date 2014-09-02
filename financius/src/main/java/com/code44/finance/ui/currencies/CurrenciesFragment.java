package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.adapters.CurrenciesAdapter;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRatesRequest;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.GeneralPrefs;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CurrenciesFragment extends ModelListFragment implements CompoundButton.OnCheckedChangeListener {
    private final List<Currency> currencies = new ArrayList<>();
    private final GeneralPrefs generalPrefs = GeneralPrefs.get();
    private final CurrenciesApi currenciesApi = CurrenciesApi.get();
    private final EventBus eventBus = EventBus.get();

    private SmoothProgressBar loading_SPB;

    public static CurrenciesFragment newInstance(Mode mode) {
        final Bundle args = makeArgs(mode, null);

        final CurrenciesFragment fragment = new CurrenciesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currencies, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        loading_SPB = (SmoothProgressBar) view.findViewById(R.id.loading_SPB);
        final View settingsContainer_V = view.findViewById(R.id.settingsContainer_V);
        final Switch autoUpdateCurrencies_S = (Switch) view.findViewById(R.id.autoUpdateCurrencies_S);

        // Setup
        loading_SPB.setVisibility(View.GONE);
        setRefreshing(false);
        autoUpdateCurrencies_S.setChecked(generalPrefs.isAutoUpdateCurrencies());
        autoUpdateCurrencies_S.setOnCheckedChangeListener(this);
        if (getMode() == Mode.SELECT) {
            settingsContainer_V.setVisibility(View.GONE);
        }
    }

    @Override public void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override public void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.currencies, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_rates:
                refreshRates();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected BaseModelsAdapter createAdapter(Context context) {
        return new CurrenciesAdapter(context);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Currencies.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrencies());
    }

    @Override protected BaseModel modelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override protected void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model) {
        CurrencyActivity.start(context, modelServerId);
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        CurrencyEditActivity.start(context, modelServerId);
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

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        generalPrefs.setAutoUpdateCurrencies(isChecked);
        if (isChecked) {
            refreshRates();
        }
    }

    @Subscribe public void onRefreshFinished(ExchangeRatesRequest request) {
        setRefreshing(false);
    }

    private void refreshRates() {
        final List<String> fromCodes = new ArrayList<>();
        for (Currency currency : currencies) {
            if (!currency.isDefault()) {
                fromCodes.add(currency.getCode());
            }
        }

        if (!fromCodes.isEmpty()) {
            currenciesApi.updateExchangeRates(fromCodes);
            setRefreshing(true);
        }
    }

    private void setRefreshing(boolean refreshing) {
        if (refreshing) {
            loading_SPB.setVisibility(View.VISIBLE);
            loading_SPB.progressiveStart();
        } else {
            loading_SPB.progressiveStop();
        }
    }
}