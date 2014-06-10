package com.code44.finance.ui.currencies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    protected void startModelActivity(Context context, View expandFrom, long modelId) {
        CurrencyActivity.start(context, expandFrom, modelId);
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
}