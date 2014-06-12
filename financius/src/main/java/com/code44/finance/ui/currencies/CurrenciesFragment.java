package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.code44.finance.api.currencies.CurrenciesAsyncApi;
import com.code44.finance.api.currencies.CurrencyRequest;
import com.code44.finance.db.Tables;
import com.code44.finance.db.model.Currency;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.Query;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class CurrenciesFragment extends ModelListFragment implements CompoundButton.OnCheckedChangeListener {
    private final List<Currency> currencies = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh_V;

    public static CurrenciesFragment newInstance() {
        final Bundle args = makeArgs();

        final CurrenciesFragment fragment = new CurrenciesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currencies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        swipeRefresh_V = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_V);
        final View separator_V = view.findViewById(R.id.separator_V);
        final Switch autoUpdateCurrencies_S = (Switch) view.findViewById(R.id.autoUpdateCurrencies_S);

        // Setup
        autoUpdateCurrencies_S.setChecked(GeneralPrefs.get().isAutoUpdateCurrencies());
        autoUpdateCurrencies_S.setOnCheckedChangeListener(this);
        swipeRefresh_V.setEnabled(false);
        swipeRefresh_V.setColorScheme(R.color.refresh_color_1, R.color.refresh_color_2, R.color.refresh_color_3, R.color.refresh_color_4);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.currencies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_rates:
                refreshRates();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void startModelActivity(Context context, View expandFrom, long modelId) {
        CurrencyActivity.start(context, expandFrom, modelId);
    }

    @Override
    protected void startModelEditActivity(Context context, View expandFrom, long modelId) {
        CurrencyEditActivity.start(context, expandFrom, modelId);
    }

    @Override
    protected BaseModelsAdapter createAdapter(Context context) {
        return new CurrenciesAdapter(context);
    }

    @Override
    protected Uri getUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override
    protected Query getQuery() {
        return Query.get()
                .appendProjection(Tables.Currencies.ID.getName())
                .appendProjection(Tables.Currencies.PROJECTION)
                .appendSortOrder(Tables.Currencies.IS_DEFAULT + " desc")
                .appendSortOrder(Tables.Currencies.CODE.getName())
                .build();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        GeneralPrefs.get().setAutoUpdateCurrencies(isChecked);
        if (isChecked) {
            refreshRates();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(CurrencyRequest.CurrencyRequestEvent event) {
        updateRefreshView();
    }

    private void updateRefreshView() {
        swipeRefresh_V.setRefreshing(EventBus.getDefault().getStickyEvent(CurrencyRequest.CurrencyRequestEvent.class) != null);
    }

    private void refreshRates() {
        for (Currency currency : currencies) {
            if (!currency.isDefault()) {
                CurrenciesAsyncApi.get().updateExchangeRate(currency.getCode());
            }
        }
        swipeRefresh_V.setRefreshing(true);
    }
}