package com.code44.finance.ui.currencies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.adapters.CurrenciesAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelListFragment;

public class CurrenciesFragment extends ModelListFragment {
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
    protected String getSortOrder() {
        return Tables.Currencies.IS_DEFAULT + " desc, " + Tables.Currencies.CODE;
    }

    private void refreshRates() {
        // TODO Refresh rates
    }
}