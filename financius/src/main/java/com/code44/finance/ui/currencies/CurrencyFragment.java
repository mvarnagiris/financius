package com.code44.finance.ui.currencies;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.db.model.Currency;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.BaseModelFragment;

public class CurrencyFragment extends BaseModelFragment<Currency> {
    private EditText code_ET;

    public static CurrencyFragment newInstance(long currencyId) {
        final Bundle args = makeArgs(currencyId);

        final CurrencyFragment fragment = new CurrencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get view
        code_ET = (EditText) view.findViewById(R.id.code_ET);
    }

    @Override
    public boolean onSave() {
        return false;
    }

    @Override
    protected Uri getModelUri(long modelId) {
        return CurrenciesProvider.uriCurrency(modelId);
    }

    @Override
    protected Class<? extends Currency> getModelClass() {
        return Currency.class;
    }

    @Override
    protected Currency getNewModel() {
        final Currency currency = new Currency();
        currency.useDefaultsIfNotSet();
        return currency;
    }

    @Override
    protected void onModelLoaded(Currency model) {
        code_ET.setText(model.getCode());
    }
}
