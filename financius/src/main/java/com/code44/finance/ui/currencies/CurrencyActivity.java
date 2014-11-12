package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.ModelActivity;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.views.FabImageButton;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class CurrencyActivity extends ModelActivity<Currency> implements View.OnClickListener {
    private static final int LOADER_ACCOUNTS = 1;

    @Inject CurrenciesApi currenciesApi;
    @Inject @Main Currency mainCurrency;

    private TextView codeTextView;
    private TextView formatTextView;
    private TextView exchangeRateTextView;
    private SmoothProgressBar loadingView;
    private FabImageButton refreshRateButton;

    private CurrencyAccountsAdapter adapter;

    public static void start(Context context, String currencyId) {
        final Intent intent = makeIntent(context, CurrencyActivity.class, currencyId);
        startActivity(context, intent);
    }

    @Override public void onResume() {
        super.onResume();
        setRefreshing(false);
        getEventBus().register(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_delete).setVisible(model != null && !model.isDefault());
        return true;
    }

    @Override public void onPause() {
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

    @Override protected int getLayoutId() {
        return R.layout.activity_currency;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        codeTextView = (TextView) findViewById(R.id.codeTextView);
        formatTextView = (TextView) findViewById(R.id.formatTextView);
        exchangeRateTextView = (TextView) findViewById(R.id.exchangeRateTextView);
        loadingView = (SmoothProgressBar) findViewById(R.id.loadingView);
        refreshRateButton = (FabImageButton) findViewById(R.id.refreshRateButton);
        final ListView listView = (ListView) findViewById(R.id.listView);

        // Setup
        adapter = new CurrencyAccountsAdapter(this);
        listView.setAdapter(adapter);
        refreshRateButton.setOnClickListener(this);
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Currencies.getQuery().asCursorLoader(this, CurrenciesProvider.uriCurrency(modelId));
    }

    @Override protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override protected void onModelLoaded(Currency currency) {
        adapter.setCurrency(currency);
        codeTextView.setText(currency.getCode());
        if (currency.isDefault()) {
            exchangeRateTextView.setText(R.string.main_currency);
        } else {
            exchangeRateTextView.setText(String.valueOf(currency.getExchangeRate()));
        }
        formatTextView.setText(MoneyFormatter.format(currency, 100000));
        // TODO This doesn't seem to be working on first load. Check after Android L is released.
        refreshRateButton.setVisibility(!currency.isDefault() ? View.VISIBLE : View.GONE);

        supportInvalidateOptionsMenu();

        // Loader
        getSupportLoaderManager().restartLoader(LOADER_ACCOUNTS, null, this);
    }

    @Override protected Uri getDeleteUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Currencies.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @Override protected void startModelEdit(String modelId) {
        CurrencyEditActivity.start(this, modelId);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshRateButton:
                refreshRate();
                break;
        }
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Currency;
    }

    @Subscribe public void onRefreshFinished(ExchangeRateRequest request) {
        if (model != null && model.getCode().equals(request.getFromCode())) {
            setRefreshing(false);
        }
    }

    private void refreshRate() {
        currenciesApi.updateExchangeRate(model.getCode(), mainCurrency.getCode());
        setRefreshing(true);
    }

    private void setRefreshing(boolean refreshing) {
        if (refreshing) {
            loadingView.setVisibility(View.VISIBLE);
            loadingView.progressiveStart();
        } else {
            loadingView.progressiveStop();
            loadingView.setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
                @Override public void onStop() {
                    loadingView.setSmoothProgressDrawableCallbacks(null);
                    loadingView.setVisibility(View.GONE);
                }

                @Override public void onStart() {
                }
            });
        }
    }
}
