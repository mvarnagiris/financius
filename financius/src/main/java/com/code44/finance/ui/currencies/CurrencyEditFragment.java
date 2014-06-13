package com.code44.finance.ui.currencies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelEditFragment;

public class CurrencyEditFragment extends ModelEditFragment<Currency> {
    private EditText code_ET;

    public static CurrencyEditFragment newInstance(long currencyId) {
        final Bundle args = makeArgs(currencyId);

        final CurrencyEditFragment fragment = new CurrencyEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get view
        code_ET = (EditText) view.findViewById(R.id.code_ET);
    }

    @Override
    public boolean onSave(Currency model) {
        return false;
    }

    @Override
    protected void ensureModelUpdated(Currency model) {

    }

    @Override
    protected Uri getUri(long modelId) {
        return CurrenciesProvider.uriCurrency(modelId);
    }

    @Override
    protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override
    protected void onModelLoaded(Currency model) {
        code_ET.setText(model.getCode());
    }
}
