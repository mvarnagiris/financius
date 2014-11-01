package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.ui.common.ModelEditActivity;
import com.code44.finance.utils.MoneyFormatter;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class CurrencyEditActivity extends ModelEditActivity<Currency> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int LOADER_CURRENCIES = 1;

    private final Set<String> existingCurrencyCodes = new HashSet<>();

    @Inject CurrenciesApi currenciesApi;
    @Inject @Main Currency mainCurrency;

    private SmoothProgressBar loadingView;
    private AutoCompleteTextView codeEditView;
    private Button thousandsSeparatorButton;
    private Button decimalSeparatorButton;
    private Button decimalsCountButton;
    private TextView codeLabelView;
    private TextView symbolLabelView;
    private EditText symbolView;
    private Button symbolPositionButton;
    private EditText exchangeRateEditView;
    private ListPopupWindow listPopupWindow;
    private View mainCurrencyContainerView;
    private View exchangeRateContainerView;
    private CheckBox isDefaultCheckBox;

    public static void start(Context context, String currencyId) {
        startActivity(context, makeIntent(context, CurrencyEditActivity.class, currencyId));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_edit);

        // Get view
        loadingView = (SmoothProgressBar) findViewById(R.id.loading);
        codeEditView = (AutoCompleteTextView) findViewById(R.id.codeEdit);
        thousandsSeparatorButton = (Button) findViewById(R.id.thousandsSeparatorButton);
        decimalSeparatorButton = (Button) findViewById(R.id.decimalSeparatorButton);
        decimalsCountButton = (Button) findViewById(R.id.decimalsCountButton);
        codeLabelView = (TextView) findViewById(R.id.code);
        symbolLabelView = (TextView) findViewById(R.id.symbol);
        symbolView = (EditText) findViewById(R.id.symbolEdit);
        symbolPositionButton = (Button) findViewById(R.id.symbolPositionButton);
        exchangeRateEditView = (EditText) findViewById(R.id.exchangeRateEdit);
        mainCurrencyContainerView = findViewById(R.id.mainCurrencyContainer);
        exchangeRateContainerView = findViewById(R.id.exchangeRateContainer);
        isDefaultCheckBox = (CheckBox) findViewById(R.id.isDefaultCheckBox);
        final TextView currentMainCurrency_TV = (TextView) findViewById(R.id.currentMainCurrency_TV);
        final ImageButton refreshRate_B = (ImageButton) findViewById(R.id.refreshRate_B);

        // Setup
        prepareCurrenciesAutoComplete();
        currentMainCurrency_TV.setText(getString(R.string.f_current_main_currency_is_x, mainCurrency.getCode()));
        decimalsCountButton.setOnClickListener(this);
        thousandsSeparatorButton.setOnClickListener(this);
        decimalSeparatorButton.setOnClickListener(this);
        symbolPositionButton.setOnClickListener(this);
        isDefaultCheckBox.setOnCheckedChangeListener(this);
        refreshRate_B.setOnClickListener(this);
        symbolView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //noinspection ConstantConditions
                model.setSymbol(symbolView.getText().toString());
                updateFormatView();

                updateSymbolTitlePosition();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        codeEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCodeTitlePosition();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Loader
        getSupportLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
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

    @Override protected boolean onSave(Currency model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getCode()) || model.getCode().length() != 3) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            DataStore.insert().values(model.asValues()).into(this, CurrenciesProvider.uriCurrencies());
        }

        return canSave;
    }

    @Override protected void ensureModelUpdated(Currency model) {
        model.setCode(codeEditView.getText().toString());
        model.setSymbol(symbolView.getText().toString());
        double exchangeRate;
        try {
            exchangeRate = Double.parseDouble(exchangeRateEditView.getText().toString());
        } catch (Exception e) {
            exchangeRate = 1.0;
        }
        model.setExchangeRate(exchangeRate);
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Currencies.getQuery().asCursorLoader(this, CurrenciesProvider.uriCurrency(modelId));
    }

    @Override protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override protected void onModelLoaded(Currency model) {
        symbolView.setText(model.getSymbol());
        codeEditView.setText(model.getCode());
        thousandsSeparatorButton.setText(getGroupSeparatorExplanation(model.getGroupSeparator()));
        decimalSeparatorButton.setText(getDecimalSeparatorExplanation(model.getDecimalSeparator()));
        decimalsCountButton.setText(String.valueOf(model.getDecimalCount()));
        exchangeRateEditView.setText(String.valueOf(model.getExchangeRate()));
        isDefaultCheckBox.setChecked(model.isDefault());
        updateFormatView();
        updateCodeTitlePosition();
        updateSymbolTitlePosition();

        codeEditView.setEnabled(isNewModel());
        mainCurrencyContainerView.setVisibility(mainCurrency.getId().equals(model.getId()) ? View.GONE : View.VISIBLE);
        exchangeRateContainerView.setVisibility(model.isDefault() ? View.GONE : View.VISIBLE);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CURRENCIES) {
            return Tables.Currencies.getQuery().asCursorLoader(this, CurrenciesProvider.uriCurrencies());
        }
        return super.onCreateLoader(id, args);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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

    @Override public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
        model.setDefault(isChecked);
        onModelLoaded(model);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refreshRate_B: {
                ensureModelUpdated(model);
                final String code = model.getCode();
                if (!TextUtils.isEmpty(code) && code.length() == 3) {
                    currenciesApi.updateExchangeRate(code, mainCurrency.getCode());
                    setRefreshing(true);
                }
                break;
            }
            case R.id.symbolPositionButton: {
                final String[] values = new String[SymbolPosition.values().length];
                int index = 0;
                for (SymbolPosition symbolPosition : SymbolPosition.values()) {
                    values[index++] = getSymbolPositionExplanation(symbolPosition);
                }
                final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, values);
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow.dismiss();
                        listPopupWindow = null;
                        model.setSymbolPosition(SymbolPosition.values()[position]);
                        ensureModelUpdated(model);
                        onModelLoaded(model);
                    }
                };
                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.thousandsSeparatorButton: {
                final String[] values = new String[GroupSeparator.values().length];
                int index = 0;
                for (GroupSeparator groupSeparator : GroupSeparator.values()) {
                    values[index++] = getGroupSeparatorExplanation(groupSeparator);
                }
                final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, values);
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow.dismiss();
                        listPopupWindow = null;
                        model.setGroupSeparator(GroupSeparator.values()[position]);
                        ensureModelUpdated(model);
                        onModelLoaded(model);
                    }
                };
                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.decimalSeparatorButton: {
                final String[] values = new String[DecimalSeparator.values().length];
                int index = 0;
                for (DecimalSeparator decimalSeparator : DecimalSeparator.values()) {
                    values[index++] = getDecimalSeparatorExplanation(decimalSeparator);
                }
                final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, values);
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow.dismiss();
                        listPopupWindow = null;
                        model.setDecimalSeparator(DecimalSeparator.values()[position]);
                        ensureModelUpdated(model);
                        onModelLoaded(model);
                    }
                };
                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.decimalsCountButton: {
                final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"0", "1", "2"});
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow.dismiss();
                        listPopupWindow = null;
                        model.setDecimalCount(position);
                        ensureModelUpdated(model);
                        onModelLoaded(model);
                    }
                };
                showPopupList(view, adapter, itemClickListener);
                break;
            }
        }
    }

    @Subscribe public void onRefreshFinished(final ExchangeRateRequest request) {
        if (model.getCode().equals(request.getFromCode())) {
            loadingView.post(new Runnable() {
                @Override public void run() {
                    setRefreshing(false);
                    final Currency currency = request.getCurrency();
                    if (currency != null) {
                        model.setExchangeRate(currency.getExchangeRate());
                        onModelLoaded(model);
                    }
                }
            });
        }
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
        final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currencies);
        codeEditView.setAdapter(autoCompleteAdapter);
        codeEditView.setThreshold(0);
        codeEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //noinspection ConstantConditions
                checkForCurrencyDuplicate(codeEditView.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkForCurrencyDuplicate(String code) {
        if (isCurrencyExists(code) && isNewModel()) {
            codeEditView.setError(getString(R.string.l_currency_exists));
        } else {
            codeEditView.setError(null);
        }
    }

    private boolean isCurrencyExists(String code) {
        return existingCurrencyCodes.contains(code.toUpperCase());
    }

    private void updateFormatView() {
        symbolPositionButton.setText(MoneyFormatter.format(model, 100000, false));
    }

    private void showPopupList(View anchorView, ListAdapter adapter, AdapterView.OnItemClickListener itemClickListener) {
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setModal(true);
        // listPopupWindow.setListSelector(getResources().getDrawable(R.drawable.btn_borderless));
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener(itemClickListener);
        listPopupWindow.setAnchorView(anchorView);
        listPopupWindow.show();
    }

    private void updateCodeTitlePosition() {
        if (TextUtils.isEmpty(codeEditView.getText())) {
            codeLabelView.animate().translationY(codeEditView.getBaseline() + (codeLabelView.getHeight() - codeLabelView.getBaseline())).setDuration(100).start();
        } else {
            codeLabelView.animate().translationY(0).setDuration(100).start();
        }
    }

    private void updateSymbolTitlePosition() {
        if (TextUtils.isEmpty(symbolView.getText())) {
            symbolLabelView.animate().translationY(symbolView.getBaseline() + (symbolLabelView.getHeight() - symbolLabelView.getBaseline())).setDuration(100).start();
        } else {
            symbolLabelView.animate().translationY(0).setDuration(100).start();
        }
    }

    private String getDecimalSeparatorExplanation(DecimalSeparator decimalSeparator) {
        switch (decimalSeparator) {
            case Dot:
                return getString(R.string.dot);
            case Comma:
                return getString(R.string.comma);
            case Space:
                return getString(R.string.space);
        }
        return null;
    }

    private String getGroupSeparatorExplanation(GroupSeparator groupSeparator) {
        switch (groupSeparator) {
            case None:
                return getString(R.string.none);
            case Dot:
                return getString(R.string.dot);
            case Comma:
                return getString(R.string.comma);
            case Space:
                return getString(R.string.space);
        }
        return null;
    }

    private String getSymbolPositionExplanation(SymbolPosition symbolPosition) {
        switch (symbolPosition) {
            case CloseRight:
                return getString(R.string.close_right);
            case FarRight:
                return getString(R.string.far_right);
            case CloseLeft:
                return getString(R.string.close_left);
            case FarLeft:
                return getString(R.string.far_left);
        }
        return null;
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
