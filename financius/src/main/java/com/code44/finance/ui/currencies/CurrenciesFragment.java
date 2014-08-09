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
import com.code44.finance.api.currencies.CurrenciesAsyncApi;
import com.code44.finance.api.currencies.CurrencyRequest;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.GeneralPrefs;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CurrenciesFragment extends ModelListFragment implements CompoundButton.OnCheckedChangeListener {
    private final List<Currency> currencies = new ArrayList<>();

    private SmoothProgressBar loading_SPB;

    public static CurrenciesFragment newInstance(Mode mode) {
        final Bundle args = makeArgs(mode);

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
        loading_SPB = (SmoothProgressBar) view.findViewById(R.id.loading_SPB);
        final View settingsContainer_V = view.findViewById(R.id.settingsContainer_V);
        final Switch autoUpdateCurrencies_S = (Switch) view.findViewById(R.id.autoUpdateCurrencies_S);

        // Setup
        autoUpdateCurrencies_S.setChecked(GeneralPrefs.get().isAutoUpdateCurrencies());
        autoUpdateCurrencies_S.setOnCheckedChangeListener(this);
        if (getMode() == Mode.SELECT) {
            settingsContainer_V.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRefreshView();
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
    protected BaseModelsAdapter createAdapter(Context context) {
        return new CurrenciesAdapter(context);
    }

    @Override
    protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Currencies.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrencies());
    }

    @Override
    protected BaseModel modelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override
    protected void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model) {
        CurrencyActivity.start(context, modelServerId);
    }

    @Override
    protected void startModelEdit(Context context, String modelServerId) {
        CurrencyEditActivity.start(context, modelServerId);
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

    private void refreshRates() {
        for (Currency currency : currencies) {
            if (!currency.isDefault()) {
                CurrenciesAsyncApi.get().updateExchangeRate(currency.getCode());
            }
        }
    }

    private void updateRefreshView() {
        final boolean isFetchingCurrencies = EventBus.getDefault().getStickyEvent(CurrencyRequest.CurrencyRequestEvent.class) != null;
        loading_SPB.setVisibility(isFetchingCurrencies ? View.VISIBLE : View.GONE);
    }
}