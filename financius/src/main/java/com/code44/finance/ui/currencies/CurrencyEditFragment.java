package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.ModelEditFragment;
import com.code44.finance.utils.MoneyFormatter;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CurrencyEditFragment extends ModelEditFragment<Currency> {
    private static final int LOADER_CURRENCIES = 1;

    private AutoCompleteTextView code_ET;
    private Button thousandsSeparator_B;
    private Button decimalSeparator_B;
    private Button decimalsCount_B;
    private EditText symbol_ET;
    private Button symbolPosition_B;
    private EditText exchangeRate_ET;

    private Set<String> existingCurrencyCodes = new HashSet<>();

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
        code_ET = (AutoCompleteTextView) view.findViewById(R.id.code_ET);
        thousandsSeparator_B = (Button) view.findViewById(R.id.thousandsSeparator_B);
        decimalSeparator_B = (Button) view.findViewById(R.id.decimalSeparator_B);
        decimalsCount_B = (Button) view.findViewById(R.id.decimalsCount_B);
        symbol_ET = (EditText) view.findViewById(R.id.symbol_ET);
        symbolPosition_B = (Button) view.findViewById(R.id.symbolPosition_B);
        exchangeRate_ET = (EditText) view.findViewById(R.id.exchangeRate_ET);

        // Setup
        prepareCurrenciesAutoComplete();
        symbol_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ensureModelUpdated(model);
                symbolPosition_B.setText(MoneyFormatter.format(model, 100000));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
    }

    @Override
    public boolean onSave(Context context, Currency model) {
        return false;
    }

    @Override
    protected void ensureModelUpdated(Currency model) {
        //noinspection ConstantConditions
        model.setCode(code_ET.getText().toString());
        //noinspection ConstantConditions
        model.setSymbol(symbol_ET.getText().toString());
        double exchangeRate = 1.0;
        try {
            //noinspection ConstantConditions
            exchangeRate = Double.parseDouble(exchangeRate_ET.getText().toString());
        } catch (Exception e) {
            exchangeRate = 1.0;
        }
        model.setExchangeRate(exchangeRate);
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
        thousandsSeparator_B.setText(model.getGroupSeparator().symbol());
        decimalSeparator_B.setText(model.getDecimalSeparator().symbol());
        decimalsCount_B.setText(String.valueOf(model.getDecimalCount()));
        symbol_ET.setText(model.getSymbol());
        exchangeRate_ET.setText(String.valueOf(model.getExchangeRate()));
        symbolPosition_B.setText(MoneyFormatter.format(model, 100000));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CURRENCIES) {
            return Query.get()
                    .projection(Tables.Currencies.CODE.getName())
                    .selection(Tables.Currencies.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.NORMAL.asInt()))
                    .asCursorLoader(getActivity(), CurrenciesProvider.uriCurrencies());
        }
        return super.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CURRENCIES) {
            existingCurrencyCodes.clear();
            if (data.moveToFirst()) {
                do {
                    existingCurrencyCodes.add(data.getString(0));
                } while (data.moveToNext());
            }
            return;
        }
        super.onLoadFinished(loader, data);
    }

    private void prepareCurrenciesAutoComplete() {
        // Build currencies set
        final Set<java.util.Currency> currencySet = new HashSet<>();
        final Locale[] locales = Locale.getAvailableLocales();
        for (Locale loc : locales) {
            try {
                currencySet.add(java.util.Currency.getInstance(loc));
            } catch (Exception exc) {
                // Locale not found
            }
        }

        // Build currencies codes array
        final String[] currencies = new String[currencySet.size()];
        int i = 0;
        for (java.util.Currency currency : currencySet) {
            currencies[i++] = currency.getCurrencyCode();
        }

        // Prepare auto complete view
        final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, currencies);
        code_ET.setAdapter(autoCompleteAdapter);
        code_ET.setThreshold(0);
        code_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //noinspection ConstantConditions
                checkForCurrencyDuplicate(code_ET.getText().toString());
                ensureModelUpdated(model);
                symbolPosition_B.setText(MoneyFormatter.format(model, 100000));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkForCurrencyDuplicate(String code) {
        if (isCurrencyExists(code)) {
            code_ET.setTextColor(getResources().getColor(R.color.text_negative));
            code_ET.setError(getString(R.string.l_currency_exists));
        } else {
            code_ET.setTextColor(getResources().getColor(R.color.text_primary));
            code_ET.setError(null);
        }
    }

    private boolean isCurrencyExists(String code) {
        return existingCurrencyCodes.contains(code);
    }
}
