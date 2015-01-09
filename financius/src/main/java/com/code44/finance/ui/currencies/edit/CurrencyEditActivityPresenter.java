package com.code44.finance.ui.currencies.edit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.code44.finance.R;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.ExchangeRateRequest;
import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.ThemeUtils;
import com.squareup.otto.Subscribe;

class CurrencyEditActivityPresenter extends ModelEditActivityPresenter<Currency> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
//    private static final int LOADER_CURRENCIES = 1;

    //    private final Set<String> existingCurrencyCodes = new HashSet<>();
//    private final CurrenciesApi currenciesApi;
//    private final Currency mainCurrency;
    private final Currency formatCurrency = new Currency();
    //
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private TextView codeTextView;
    private AutoCompleteTextView codeEditTextView;
    private EditText symbolEditTextView;
    private Button symbolPositionButton;
//    private Button thousandsSeparatorButton;
//    private Button decimalSeparatorButton;
//    private Button decimalsCountButton;
//    private EditText exchangeRateEditTextView;
//    private ListPopupWindow listPopupWindow;
//    private View mainCurrencyContainerView;
//    private View exchangeRateContainerView;
//    private CheckBox isDefaultCheckBox;

    private ForegroundColorSpan foregroundColorSpan;
    private String code;
    private String symbol;
    private SymbolPosition symbolPosition;

    public CurrencyEditActivityPresenter(EventBus eventBus, CurrenciesApi currenciesApi, Currency mainCurrency) {
        super(eventBus);
//        this.currenciesApi = currenciesApi;
//        this.mainCurrency = mainCurrency;
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        foregroundColorSpan = new ForegroundColorSpan(ThemeUtils.getColor(activity, R.attr.colorPrimary));

        // Get view
//        swipeRefreshLayout = findView(activity, R.id.loadingView);
//        codeTextView = findView(activity, R.id.codeTextView);
        codeEditTextView = findView(activity, R.id.codeEditTextView);
        symbolEditTextView = findView(activity, R.id.symbolEditTextView);
        symbolPositionButton = findView(activity, R.id.symbolPositionButton);
//        thousandsSeparatorButton = findView(activity, R.id.thousandsSeparatorButton);
//        decimalSeparatorButton = findView(activity, R.id.decimalSeparatorButton);
//        decimalsCountButton = findView(activity, R.id.decimalsCountButton);
//
//        exchangeRateEditTextView = findView(activity, R.id.exchangeRateEdit);
//        mainCurrencyContainerView = findView(activity, R.id.mainCurrencyContainerView);
//        exchangeRateContainerView = findView(activity, R.id.exchangeRateContainerView);
//        isDefaultCheckBox = findView(activity, R.id.isDefaultCheckBox);
//        final TextView currentMainCurrency_TV = findView(activity, R.id.currentMainCurrencyTextView);
//        final ImageButton refreshRateButton = findView(activity, R.id.refreshRateButton);

        // Setup
        symbolPositionButton.setOnClickListener(this);
//        swipeRefreshLayout.setEnabled(false);
//        prepareCurrenciesAutoComplete();
//        currentMainCurrency_TV.setText(getString(R.string.f_current_main_currency_is_x, mainCurrency.getCode()));
//        decimalsCountButton.setOnClickListener(this);
//        thousandsSeparatorButton.setOnClickListener(this);
//        decimalSeparatorButton.setOnClickListener(this);
//        isDefaultCheckBox.setOnCheckedChangeListener(this);
//        refreshRateButton.setOnClickListener(this);
//        refreshRateButton.setColorFilter(ThemeUtils.getColor(this, android.R.attr.textColorPrimary));
        codeEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                updateCodeTitlePosition();
            }

            @Override
            public void afterTextChanged(Editable s) {
                code = codeEditTextView.getText().toString();
                codeEditTextView.removeTextChangedListener(this);
                onDataChanged(getStoredModel());
                codeEditTextView.addTextChangedListener(this);
            }
        });
        symbolEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //noinspection ConstantConditions
//                model.setSymbol(symbolEditTextView.getText().toString());
//                              updateFormatView();
//
//                updateSymbolTitlePosition();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                symbol = symbolEditTextView.getText().toString();
                symbolEditTextView.removeTextChangedListener(this);
                onDataChanged(getStoredModel());
                symbolEditTextView.addTextChangedListener(this);
            }
        });

//        getSupportLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
    }

    @Override public void onActivityResumed(BaseActivity activity) {
        super.onActivityResumed(activity);
        getEventBus().register(this);
    }

    @Override public void onActivityPaused(BaseActivity activity) {
        super.onActivityPaused(activity);
        getEventBus().unregister(this);
    }

    @Override protected void onDataChanged(Currency model) {
        formatCurrency.setCode(getCode());
        formatCurrency.setSymbol(getSymbol());
        formatCurrency.setSymbolPosition(getSymbolPosition());

        codeEditTextView.setText(formatCurrency.getCode());
        symbolEditTextView.setText(formatCurrency.getSymbol());
        updateSymbolPosition();


//        thousandsSeparatorButton.setText(getGroupSeparatorExplanation(model.getGroupSeparator()));
//        decimalSeparatorButton.setText(getDecimalSeparatorExplanation(model.getDecimalSeparator()));
//        decimalsCountButton.setText(String.valueOf(model.getDecimalCount()));
//        exchangeRateEditTextView.setText(String.valueOf(model.getExchangeRate()));
//        isDefaultCheckBox.setChecked(model.isDefault());
//        updateFormatView();
//        updateCodeTitlePosition();
//        updateSymbolTitlePosition();

        codeEditTextView.setEnabled(isNewModel());
//        mainCurrencyContainerView.setVisibility(mainCurrency.getId().equals(model.getId()) ? View.GONE : View.VISIBLE);
//        exchangeRateContainerView.setVisibility(model.isDefault() ? View.GONE : View.VISIBLE);
    }

    @Override protected boolean onSave() {
        boolean canSave = true;

//        if (TextUtils.isEmpty(model.getCode()) || model.getCode().length() != 3) {
//            canSave = false;
//            // TODO Show error
//        }
//
//        if (canSave) {
//            DataStore.insert().values(model.asValues()).into(this, CurrenciesProvider.uriCurrencies());
//        }

        return canSave;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Currencies.getQuery().asCursorLoader(context, CurrenciesProvider.uriCurrency(modelId));
    }

    @Override protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        if (id == LOADER_CURRENCIES) {
//            return Tables.Currencies.getQuery().asCursorLoader(this, CurrenciesProvider.uriCurrencies());
//        }
        return super.onCreateLoader(id, args);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (loader.getId() == LOADER_CURRENCIES) {
//            existingCurrencyCodes.clear();
//            if (data.moveToFirst()) {
//                do {
//                    existingCurrencyCodes.add(data.getString(0));
//                } while (data.moveToNext());
//            }
//            return;
//        }
        super.onLoadFinished(loader, data);
    }

    @Override public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
//        model.setDefault(isChecked);
//        onModelLoaded(model);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.symbolPositionButton:
                toggleSymbolPosition();
                break;

            case R.id.refreshRateButton: {
//                ensureModelUpdated(model);
//                final String code = model.getCode();
//                if (!TextUtils.isEmpty(code) && code.length() == 3) {
//                    currenciesApi.updateExchangeRate(code, mainCurrency.getCode());
//                    setRefreshing(true);
//                }
                break;
            }

            case R.id.thousandsSeparatorButton: {
//                final String[] values = new String[GroupSeparator.values().length];
//                int index = 0;
//                for (GroupSeparator groupSeparator : GroupSeparator.values()) {
//                    values[index++] = getGroupSeparatorExplanation(groupSeparator);
//                }
//                final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, values);
//                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        listPopupWindow.dismiss();
//                        listPopupWindow = null;
//                        model.setGroupSeparator(GroupSeparator.values()[position]);
//                        ensureModelUpdated(model);
//                        onModelLoaded(model);
//                    }
//                };
//                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.decimalSeparatorButton: {
//                final String[] values = new String[DecimalSeparator.values().length];
//                int index = 0;
//                for (DecimalSeparator decimalSeparator : DecimalSeparator.values()) {
//                    values[index++] = getDecimalSeparatorExplanation(decimalSeparator);
//                }
//                final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, values);
//                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        listPopupWindow.dismiss();
//                        listPopupWindow = null;
//                        model.setDecimalSeparator(DecimalSeparator.values()[position]);
//                        ensureModelUpdated(model);
//                        onModelLoaded(model);
//                    }
//                };
//                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.decimalsCountButton: {
//                final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"0", "1", "2"});
//                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                        if (listPopupWindow == null) {
//                            return;
//                        }
//
//                        listPopupWindow.dismiss();
//                        listPopupWindow = null;
//                        model.setDecimalCount(position);
//                        ensureModelUpdated(model);
//                        onModelLoaded(model);
//                    }
//                };
//                showPopupList(view, adapter, itemClickListener);
                break;
            }
        }
    }

    @Subscribe public void onRefreshFinished(final ExchangeRateRequest request) {
//        if (model != null && !Strings.isEmpty(model.getCode()) && model.getCode().equals(request.getFromCode())) {
//            loadingView.post(new Runnable() {
//                @Override public void run() {
//                    setRefreshing(false);
//                    final Currency currency = request.getCurrency();
//                    if (currency != null) {
//                        model.setExchangeRate(currency.getExchangeRate());
//                        onModelLoaded(model);
//                    }
//                }
//            });
//        }
    }

    private void updateSymbolPosition() {
        final SpannableStringBuilder ssb = new SpannableStringBuilder(getFormattedText());
        final int symbolLength = formatCurrency.getSymbol().length();

        switch (formatCurrency.getSymbolPosition()) {
            case CloseRight:
            case FarRight:
                ssb.setSpan(foregroundColorSpan, ssb.length() - symbolLength, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case CloseLeft:
            case FarLeft:
                ssb.setSpan(foregroundColorSpan, 0, symbolLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                throw new IllegalArgumentException("Symbol position " + formatCurrency.getSymbolPosition() + " is not supported.");
        }
        symbolPositionButton.setText(ssb);
    }

    private String getFormattedText() {
        return MoneyFormatter.format(formatCurrency, 100000);
    }

//    @Override protected void ensureModelUpdated(Currency model) {
//        model.setCode(codeEditTextView.getText().toString());
//        model.setSymbol(symbolEditTextView.getText().toString());
//        double exchangeRate;
//        try {
//            exchangeRate = Double.parseDouble(exchangeRateEditTextView.getText().toString());
//        } catch (Exception e) {
//            exchangeRate = 1.0;
//        }
//        model.setExchangeRate(exchangeRate);
//    }

    private void prepareCurrenciesAutoComplete() {
//        // Build currencies set
//        final Set<java.util.Currency> currencySet = new HashSet<>();
//        final Locale[] locales = Locale.getAvailableLocales();
//        for (Locale loc : locales) {
//            try {
//                currencySet.add(java.util.Currency.getInstance(loc));
//            } catch (Exception exc) {
//                // Locale not found
//            }
//        }
//
//        // Build currencies codes array
//        final String[] currencies = new String[currencySet.size()];
//        int i = 0;
//        for (java.util.Currency currency : currencySet) {
//            currencies[i++] = currency.getCurrencyCode();
//        }
//
//        // Prepare auto complete view
//        final ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currencies);
//        codeEditTextView.setAdapter(autoCompleteAdapter);
//        codeEditTextView.setThreshold(0);
//        codeEditTextView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //noinspection ConstantConditions
//                checkForCurrencyDuplicate(codeEditTextView.getText().toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
    }

    private void checkForCurrencyDuplicate(String code) {
//        if (isCurrencyExists(code) && isNewModel()) {
//            codeEditTextView.setError(getString(R.string.l_currency_exists));
//        } else {
//            codeEditTextView.setError(null);
//        }
    }

    private boolean isCurrencyExists(String code) {
//        return existingCurrencyCodes.contains(code.toUpperCase());
        return false;
    }

    private void updateFormatView() {
//        symbolPositionButton.setText(MoneyFormatter.format(model, 100000, false));
    }

    private void showPopupList(View anchorView, ListAdapter adapter, AdapterView.OnItemClickListener itemClickListener) {
//        listPopupWindow = new ListPopupWindow(this);
//        listPopupWindow.setModal(true);
//        // listPopupWindow.setListSelector(getResources().getDrawable(R.drawable.btn_borderless));
//        listPopupWindow.setAdapter(adapter);
//        listPopupWindow.setOnItemClickListener(itemClickListener);
//        listPopupWindow.setAnchorView(anchorView);
//        listPopupWindow.show();
    }

    private void updateCodeTitlePosition() {
//        if (TextUtils.isEmpty(codeEditTextView.getText())) {
//            codeTextView.animate().translationY(codeEditTextView.getBaseline() + (codeTextView.getHeight() - codeTextView.getBaseline())).setDuration(100).start();
//        } else {
//            codeTextView.animate().translationY(0).setDuration(100).start();
//        }
    }

    private void updateSymbolTitlePosition() {
//        if (TextUtils.isEmpty(symbolEditTextView.getText())) {
//            symbolTextView.animate().translationY(symbolEditTextView.getBaseline() + (symbolTextView.getHeight() - symbolTextView.getBaseline())).setDuration(100).start();
//        } else {
//            symbolTextView.animate().translationY(0).setDuration(100).start();
//        }
    }

    private String getDecimalSeparatorExplanation(DecimalSeparator decimalSeparator) {
//        switch (decimalSeparator) {
//            case Dot:
//                return getString(R.string.dot);
//            case Comma:
//                return getString(R.string.comma);
//            case Space:
//                return getString(R.string.space);
//        }
        return null;
    }

    private String getGroupSeparatorExplanation(GroupSeparator groupSeparator) {
//        switch (groupSeparator) {
//            case None:
//                return getString(R.string.none);
//            case Dot:
//                return getString(R.string.dot);
//            case Comma:
//                return getString(R.string.comma);
//            case Space:
//                return getString(R.string.space);
//        }
        return null;
    }

    private String getSymbolPositionExplanation(SymbolPosition symbolPosition) {
//        switch (symbolPosition) {
//            case CloseRight:
//                return getString(R.string.close_right);
//            case FarRight:
//                return getString(R.string.far_right);
//            case CloseLeft:
//                return getString(R.string.close_left);
//            case FarLeft:
//                return getString(R.string.far_left);
//        }
        return null;
    }

    private void setRefreshing(boolean refreshing) {
//        swipeRefreshLayout.setRefreshing(refreshing);
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
        onDataChanged(getStoredModel());
    }
}
