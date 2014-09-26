package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.adapters.CurrencyAccountsAdapter;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.ModelFragment;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.views.FabImageButton;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class CurrencyFragment extends ModelFragment<Currency> implements View.OnClickListener {
    private static final int LOADER_ACCOUNTS = 1;

    @Inject CurrenciesApi currenciesApi;
    @Inject @Main Currency mainCurrency;

    private TextView code_TV;
    private TextView format_TV;
    private TextView exchangeRate_TV;
    private SmoothProgressBar loading_SPB;
    private FabImageButton refreshRate_IB;
    private CurrencyAccountsAdapter adapter;

    public static CurrencyFragment newInstance(String currencyServerId) {
        final Bundle args = makeArgs(currencyServerId);

        final CurrencyFragment fragment = new CurrencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        code_TV = (TextView) view.findViewById(R.id.code_TV);
        format_TV = (TextView) view.findViewById(R.id.format_TV);
        exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
        loading_SPB = (SmoothProgressBar) view.findViewById(R.id.loading_SPB);
        refreshRate_IB = (FabImageButton) view.findViewById(R.id.refreshRate_IB);
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);

        // Setup
        adapter = new CurrencyAccountsAdapter(getActivity());
        list_V.setAdapter(adapter);
        refreshRate_IB.setOnClickListener(this);
    }

    @Override public void onResume() {
        super.onResume();
        setRefreshing(false);
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_delete).setVisible(model != null && !model.isDefault());
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ACCOUNTS) {
            return Tables.Accounts.getQuery().asCursorLoader(getActivity(), AccountsProvider.uriAccounts());
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

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Currencies.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrency(modelServerId));
    }

    @Override protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override protected void onModelLoaded(Currency currency) {
        adapter.setCurrency(currency);
        code_TV.setText(currency.getCode());
        if (currency.isDefault()) {
            exchangeRate_TV.setText(R.string.main_currency);
        } else {
            exchangeRate_TV.setText(String.valueOf(currency.getExchangeRate()));
        }
        format_TV.setText(MoneyFormatter.format(currency, 100000));
        // TODO This doesn't seem to be working on first load. Check after Android L is released.
        refreshRate_IB.setVisibility(!currency.isDefault() ? View.VISIBLE : View.GONE);

        getActivity().invalidateOptionsMenu();

        // Loader
        getLoaderManager().restartLoader(LOADER_ACCOUNTS, null, this);
    }

    @Override protected Uri getDeleteUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Currencies.ID + "=?", new String[]{String.valueOf(modelId)});
    }

    @Override protected void startModelEdit(Context context, String modelServerId) {
        CurrencyEditActivity.start(context, modelServerId);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshRate_IB:
                refreshRate();
                break;
        }
    }

    @Subscribe public void onRefreshFinished(ExchangeRateRequest request) {
        if (model.getCode().equals(request.getFromCode())) {
            setRefreshing(false);
        }
    }

    private void refreshRate() {
        currenciesApi.updateExchangeRate(model.getCode(), mainCurrency.getCode());
        setRefreshing(true);
    }

    private void setRefreshing(boolean refreshing) {
        if (refreshing) {
            loading_SPB.setVisibility(View.VISIBLE);
            loading_SPB.progressiveStart();
        } else {
            loading_SPB.progressiveStop();
            loading_SPB.setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
                @Override public void onStop() {
                    loading_SPB.setSmoothProgressDrawableCallbacks(null);
                    loading_SPB.setVisibility(View.GONE);
                }

                @Override public void onStart() {

                }
            });
        }
    }
}
