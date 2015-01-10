package com.code44.finance.ui.currencies.edit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.MoneyFormatter;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

class CurrencyEditActivityPresenter extends ModelEditActivityPresenter<Currency> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String STATE_CODE = "STATE_CODE";
    public static final String STATE_SYMBOL = "STATE_SYMBOL";
    public static final String STATE_SYMBOL_POSITION = "STATE_SYMBOL_POSITION";
    public static final String STATE_GROUP_SEPARATOR = "STATE_GROUP_SEPARATOR";
    public static final String STATE_DECIMAL_SEPARATOR = "STATE_DECIMAL_SEPARATOR";
    public static final String STATE_DECIMAL_COUNT = "STATE_DECIMAL_COUNT";
    public static final String STATE_IS_DEFAULT = "STATE_IS_DEFAULT";
    public static final String STATE_EXCHANGE_RATE = "STATE_EXCHANGE_RATE";
    private static final int LOADER_CURRENCIES = 1;
    private final Set<String> existingCurrencyCodes = new HashSet<>();
    private final CurrenciesApi currenciesApi;
    private final Currency mainCurrency;
    private final Currency formatCurrency = new Currency();

    private TextView codeTextView;
    private AutoCompleteTextView codeEditTextView;
    private EditText symbolEditTextView;
    private TextView errorTextView;
    private TextView currencyFormatTextView;
    private View exchangeRateContainerView;
    private EditText exchangeRateEditText;
    private View exchangeRateDividerView;

    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;
    private GroupSeparator groupSeparator;
    private DecimalSeparator decimalSeparator;
    private Integer decimalCount;
    private Boolean isDefault;
    private Double exchangeRate;

    public CurrencyEditActivityPresenter(EventBus eventBus, CurrenciesApi currenciesApi, Currency mainCurrency) {
        super(eventBus);
        this.currenciesApi = currenciesApi;
        this.mainCurrency = mainCurrency;
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        // Get view
        codeTextView = findView(activity, R.id.codeTextView);
        codeEditTextView = findView(activity, R.id.codeEditTextView);
        symbolEditTextView = findView(activity, R.id.symbolEditTextView);
        errorTextView = findView(activity, R.id.errorTextView);
        currencyFormatTextView = findView(activity, R.id.currencyFormatTextView);
        exchangeRateContainerView = findView(activity, R.id.exchangeRateContainerView);
        exchangeRateEditText = findView(activity, R.id.exchangeRateEditText);
        exchangeRateDividerView = findView(activity, R.id.exchangeRateDividerView);
        final Button symbolPositionButton = findView(activity, R.id.symbolPositionButton);
        final Button groupSeparatorButton = findView(activity, R.id.groupSeparatorButton);
        final Button decimalSeparatorButton = findView(activity, R.id.decimalSeparatorButton);
        final Button decimalCountButton = findView(activity, R.id.decimalCountButton);
        final View mainCurrencyContainerView = findView(activity, R.id.mainCurrencyContainerView);
        final CheckBox isDefaultCheckBox = findView(activity, R.id.isDefaultCheckBox);
        final TextView currentMainCurrencyTextView = findView(activity, R.id.currentMainCurrencyTextView);
        final ImageView refreshRateImageView = findView(activity, R.id.refreshRateImageView);

        // Setup
        prepareCurrenciesAutoComplete();
        symbolPositionButton.setOnClickListener(this);
        groupSeparatorButton.setOnClickListener(this);
        decimalSeparatorButton.setOnClickListener(this);
        decimalCountButton.setOnClickListener(this);
        isDefaultCheckBox.setOnCheckedChangeListener(this);
        codeEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code = codeEditTextView.getText().toString();
                updateFormat();
            }
        });
        symbolEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                symbol = symbolEditTextView.getText().toString();
                updateFormat();
            }
        });
        exchangeRateEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                try {
                    exchangeRate = Double.parseDouble(exchangeRateEditText.getText().toString());
                } catch (NumberFormatException e) {
                    exchangeRate = 1.0;
                }

                if (Double.compare(exchangeRate, 0) <= 0) {
                    exchangeRate = 1.0;
                }
            }
        });
        currentMainCurrencyTextView.setText(activity.getString(R.string.f_current_main_currency_is_x, mainCurrency.getCode()));
        refreshRateImageView.setOnClickListener(this);
        if (!isNewModel()) {
            codeTextView.setVisibility(View.VISIBLE);
            codeEditTextView.setVisibility(View.GONE);
        }

        if (isDefaultCurrency()) {
            mainCurrencyContainerView.setVisibility(View.GONE);
        }

        // Restore state
        if (savedInstanceState != null) {
            code = savedInstanceState.getString(STATE_CODE);
            symbol = savedInstanceState.getString(STATE_SYMBOL);
            symbolPosition = (SymbolPosition) savedInstanceState.getSerializable(STATE_SYMBOL_POSITION);
            groupSeparator = (GroupSeparator) savedInstanceState.getSerializable(STATE_GROUP_SEPARATOR);
            decimalSeparator = (DecimalSeparator) savedInstanceState.getSerializable(STATE_DECIMAL_SEPARATOR);
            decimalCount = savedInstanceState.getInt(STATE_DECIMAL_COUNT, -1);
            if (decimalCount == -1) {
                decimalCount = null;
            }
            isDefault = savedInstanceState.getInt(STATE_IS_DEFAULT, -1) == -1 ? null : savedInstanceState.getInt(STATE_IS_DEFAULT, 0) == 1;
            exchangeRate = savedInstanceState.getDouble(STATE_EXCHANGE_RATE, -1) < 0 ? null : savedInstanceState.getDouble(STATE_EXCHANGE_RATE, 1);
            onDataChanged(getStoredModel());
        }

        activity.getSupportLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
    }

    @Override public void onActivityResumed(BaseActivity activity) {
        super.onActivityResumed(activity);
        getEventBus().register(this);
    }

    @Override public void onActivityPaused(BaseActivity activity) {
        super.onActivityPaused(activity);
        getEventBus().unregister(this);
    }

    @Override public void onActivitySaveInstanceState(BaseActivity activity, Bundle outState) {
        super.onActivitySaveInstanceState(activity, outState);
        outState.putString(STATE_CODE, code);
        outState.putString(STATE_SYMBOL, symbol);
        outState.putSerializable(STATE_SYMBOL_POSITION, symbolPosition);
        outState.putSerializable(STATE_GROUP_SEPARATOR, groupSeparator);
        outState.putSerializable(STATE_DECIMAL_SEPARATOR, decimalSeparator);
        outState.putInt(STATE_DECIMAL_COUNT, decimalCount == null ? -1 : decimalCount);
        outState.putInt(STATE_IS_DEFAULT, isDefault == null ? -1 : isDefault ? 1 : 0);
        outState.putDouble(STATE_EXCHANGE_RATE, exchangeRate == null ? -1 : exchangeRate);
    }

    @Override protected void onDataChanged(Currency model) {
        codeTextView.setText(getCode());
        codeEditTextView.setText(getCode());
        symbolEditTextView.setText(getSymbol());
        updateFormat();

        if (!isDefaultCurrency()) {
            if (isDefault()) {
                exchangeRateContainerView.setVisibility(View.GONE);
                exchangeRateDividerView.setVisibility(View.GONE);
            } else {
                exchangeRateContainerView.setVisibility(View.VISIBLE);
                exchangeRateDividerView.setVisibility(View.VISIBLE);
                exchangeRateEditText.setText(String.valueOf(getExchangeRate()));
            }
        }
    }

    @Override protected boolean onSave() {
        boolean canSave = true;

        final String code = getCode();
        if (TextUtils.isEmpty(code) || code.length() != 3) {
            canSave = false;
            errorTextView.setText(R.string.l_please_enter_currency_code);
            errorTextView.setVisibility(View.VISIBLE);
        }

        if (canSave) {
            final Currency currency = new Currency();
            currency.setId(getId());
            currency.setCode(code);
            currency.setSymbol(getSymbol());
            currency.setSymbolPosition(getSymbolPosition());
            currency.setGroupSeparator(getGroupSeparator());
            currency.setDecimalSeparator(getDecimalSeparator());
            currency.setDecimalCount(getDecimalCount());
            currency.setDefault(isDefault());
            currency.setExchangeRate(getExchangeRate());
            DataStore.insert().values(currency.asValues()).into(getActivity(), CurrenciesProvider.uriCurrencies());
        }

        return canSave;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Currencies.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrency(modelId));
    }

    @Override protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CURRENCIES) {
            return Tables.Currencies.getQuery().asCursorLoader(getActivity(), CurrenciesProvider.uriCurrencies());
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
        isDefault = isChecked;
        onDataChanged(getStoredModel());
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.symbolPositionButton:
                toggleSymbolPosition();
                break;
            case R.id.groupSeparatorButton:
                toggleGroupSeparator();
                break;
            case R.id.decimalSeparatorButton:
                toggleDecimalSeparator();
                break;
            case R.id.decimalCountButton:
                toggleDecimalCount();
                break;
            case R.id.refreshRateImageView:
                final String code = getCode();
                if (!TextUtils.isEmpty(code) && code.length() == 3) {
                    currenciesApi.updateExchangeRate(code, mainCurrency.getCode());
                }
                break;
        }
    }

    @Subscribe public void onRefreshFinished(ExchangeRateRequest request) {
        if (!Strings.isEmpty(getCode()) && getCode().equals(request.getFromCode())) {
            exchangeRate = request.getCurrency().getExchangeRate();
            onDataChanged(getStoredModel());
        }
    }

    private void updateFormat() {
        formatCurrency.setCode(getCode());
        formatCurrency.setSymbol(getSymbol());
        formatCurrency.setSymbolPosition(getSymbolPosition());
        formatCurrency.setGroupSeparator(getGroupSeparator());
        formatCurrency.setDecimalSeparator(getDecimalSeparator());
        formatCurrency.setDecimalCount(getDecimalCount());
        currencyFormatTextView.setText(MoneyFormatter.format(formatCurrency, 100000, false));
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
        codeEditTextView.setAdapter(autoCompleteAdapter);
        codeEditTextView.setThreshold(0);
        codeEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkForCurrencyDuplicate(codeEditTextView.getText().toString());
            }
        });
    }

    private void checkForCurrencyDuplicate(String code) {
        if (isCurrencyExists(code) && isNewModel()) {
            errorTextView.setError(getActivity().getString(R.string.l_currency_exists));
            errorTextView.setVisibility(View.VISIBLE);
        } else {
            errorTextView.setVisibility(View.GONE);
        }
    }

    private boolean isCurrencyExists(String code) {
        return existingCurrencyCodes.contains(code.toUpperCase());
    }

    private String getId() {
        return getStoredModel() != null ? getStoredModel().getId() : null;
    }

    private String getCode() {
        if (code != null) {
            return code;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getCode();
        }

        return null;
    }

    private String getSymbol() {
        if (symbol != null) {
            return symbol;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getSymbol();
        }

        return "";
    }

    private SymbolPosition getSymbolPosition() {
        if (symbolPosition != null) {
            return symbolPosition;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getSymbolPosition();
        }

        return SymbolPosition.FarRight;
    }


    private GroupSeparator getGroupSeparator() {
        if (groupSeparator != null) {
            return groupSeparator;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getGroupSeparator();
        }

        return GroupSeparator.Comma;
    }

    private DecimalSeparator getDecimalSeparator() {
        if (decimalSeparator != null) {
            return decimalSeparator;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getDecimalSeparator();
        }

        return DecimalSeparator.Dot;
    }

    private int getDecimalCount() {
        if (decimalCount != null) {
            return decimalCount;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getDecimalCount();
        }

        return 2;
    }

    private boolean isDefault() {
        if (isDefault != null) {
            return isDefault;
        }

        return getStoredModel() != null && getStoredModel().isDefault();

    }

    private double getExchangeRate() {
        if (exchangeRate != null) {
            return exchangeRate;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getExchangeRate();
        }

        return 1;
    }

    private void toggleSymbolPosition() {
        switch (getSymbolPosition()) {
            case CloseRight:
                symbolPosition = SymbolPosition.FarLeft;
                break;
            case FarRight:
                symbolPosition = SymbolPosition.CloseRight;
                break;
            case CloseLeft:
                symbolPosition = SymbolPosition.FarRight;
                break;
            case FarLeft:
                symbolPosition = SymbolPosition.CloseLeft;
                break;
            default:
                throw new IllegalArgumentException("Symbol position " + getSymbolPosition() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleGroupSeparator() {
        switch (getGroupSeparator()) {
            case None:
                groupSeparator = GroupSeparator.Comma;
                break;
            case Dot:
                groupSeparator = GroupSeparator.Space;
                break;
            case Comma:
                groupSeparator = GroupSeparator.Dot;
                break;
            case Space:
                groupSeparator = GroupSeparator.None;
                break;
            default:
                throw new IllegalArgumentException("Group separator " + getGroupSeparator() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleDecimalSeparator() {
        switch (getDecimalSeparator()) {
            case Dot:
                decimalSeparator = DecimalSeparator.Space;
                break;
            case Comma:
                decimalSeparator = DecimalSeparator.Dot;
                break;
            case Space:
                decimalSeparator = DecimalSeparator.Comma;
                break;
            default:
                throw new IllegalArgumentException("Decimal separator " + getDecimalSeparator() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleDecimalCount() {
        switch (getDecimalCount()) {
            case 0:
                decimalCount = 2;
                break;
            case 1:
                decimalCount = 0;
                break;
            case 2:
                decimalCount = 1;
                break;
            default:
                throw new IllegalArgumentException("Decimal count " + getDecimalCount() + " is not supported.");
        }
        updateFormat();
    }

    private boolean isDefaultCurrency() {
        return mainCurrency.getId().equals(getModelId());
    }
}
