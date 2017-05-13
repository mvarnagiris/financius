package com.code44.finance.ui.currencies.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.UpdateExchangeRatesRequest;
import com.code44.finance.api.endpoints.EndpointsApi;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.activities.ModelActivity;
import com.code44.finance.ui.currencies.edit.CurrencyEditActivity;
import com.code44.finance.utils.analytics.Screens;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class CurrencyActivity extends ModelActivity<CurrencyFormat> implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int LOADER_ACCOUNTS = 1;

    @Inject CurrenciesApi currenciesApi;
    @Inject EndpointsApi endpointsApi;
    @Inject CurrenciesManager currenciesManager;
    @Inject AmountFormatter amountFormatter;

    private TextView codeTextView;
    private Button mainCurrencyButton;
    private TextView formatTextView;
    private TextView exchangeRateTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CurrencyAccountsAdapter adapter;

    public static void start(Context context, String currencyId) {
        makeActivityStarter(context, CurrencyActivity.class, currencyId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        // Get views
        codeTextView = (TextView) findViewById(R.id.codeTextView);
        mainCurrencyButton = (Button) findViewById(R.id.mainCurrencyButton);
        formatTextView = (TextView) findViewById(R.id.formatTextView);
        exchangeRateTextView = (TextView) findViewById(R.id.exchangeRateTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        final ImageView refreshRateButton = (ImageView) findViewById(R.id.refreshRateButton);
        final ListView listView = (ListView) findViewById(R.id.listView);

        // Setup
        refreshRateButton.setOnClickListener(this);
        mainCurrencyButton.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        adapter = new CurrencyAccountsAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ACCOUNTS) {
            return Tables.Accounts.getQuery().asCursorLoader(this, AccountsProvider.uriAccounts());
        }
        return super.onCreateLoader(id, args);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ACCOUNTS) {
            adapter.swapCursor(data);
            return;
        }
        super.onLoadFinished(loader, data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_ACCOUNTS) {
            adapter.swapCursor(null);
            return;
        }
        super.onLoaderReset(loader);
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.CurrencyFormats.getQuery().asCursorLoader(this, CurrenciesProvider.uriCurrency(modelId));
    }

    @NonNull @Override protected CurrencyFormat getModelFrom(@NonNull Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    @Override protected void onModelLoaded(@NonNull CurrencyFormat model) {
        adapter.setCurrency(model);
        codeTextView.setText(model.getCode());
        if (currenciesManager.isMainCurrency(model.getCode())) {
            exchangeRateTextView.setText(R.string.main_currency);
            mainCurrencyButton.setVisibility(View.GONE);
        } else {
            exchangeRateTextView.setText(String.valueOf(currenciesManager.getExchangeRate(model.getCode(), currenciesManager.getMainCurrencyCode())));
            mainCurrencyButton.setVisibility(View.VISIBLE);
        }
        formatTextView.setText(amountFormatter.format(model.getCode(), 100000));

        supportInvalidateOptionsMenu();

        // Loader
        getSupportLoaderManager().restartLoader(LOADER_ACCOUNTS, null, this);
    }

    @Override protected void startModelEdit(@NonNull String modelId) {
        CurrencyEditActivity.start(this, modelId);
    }

    @Nullable @Override protected Uri getDeleteUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Nullable @Override protected Pair<String, String[]> getDeleteSelection(@NonNull String modelId) {
        return Pair.create(Tables.CurrencyFormats.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshRateButton:
                onRefresh();
                break;
            case R.id.mainCurrencyButton:
                setAsMainCurrency();
                break;
        }
    }

    @Override public void onRefresh() {
        currenciesApi.updateExchangeRates();
        setRefreshing(true);
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Currency;
    }

    @Subscribe public void onRefreshFinished(UpdateExchangeRatesRequest request) {
        setRefreshing(false);
    }

    private void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void setAsMainCurrency() {
        currenciesManager.setMainCurrencyCode(getStoredModel().getCode());
        endpointsApi.syncConfig();
    }
}
