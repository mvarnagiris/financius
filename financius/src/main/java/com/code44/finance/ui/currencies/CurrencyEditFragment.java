package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.api.currencies.CurrenciesAsyncApi;
import com.code44.finance.api.currencies.CurrencyRequest;
import com.code44.finance.data.DataStore;
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

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class CurrencyEditFragment extends ModelEditFragment<Currency> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int LOADER_CURRENCIES = 1;

    private SmoothProgressBar loading_SPB;
    private AutoCompleteTextView code_ET;
    private Button thousandsSeparator_B;
    private Button decimalSeparator_B;
    private Button decimalsCount_B;
    private TextView code_TV;
    private TextView symbol_TV;
    private EditText symbol_ET;
    private Button symbolPosition_B;
    private EditText exchangeRate_ET;
    private ListPopupWindow listPopupWindow_LPW;
    private View mainCurrencyContainer_V;
    private View exchangeRateContainer_V;
    private CheckBox isDefault_CB;

    private Set<String> existingCurrencyCodes = new HashSet<>();

    public CurrencyEditFragment() {
    }

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
        loading_SPB = (SmoothProgressBar) view.findViewById(R.id.loading_SPB);
        code_ET = (AutoCompleteTextView) view.findViewById(R.id.code_ET);
        thousandsSeparator_B = (Button) view.findViewById(R.id.thousandsSeparator_B);
        decimalSeparator_B = (Button) view.findViewById(R.id.decimalSeparator_B);
        decimalsCount_B = (Button) view.findViewById(R.id.decimalsCount_B);
        code_TV = (TextView) view.findViewById(R.id.code_TV);
        symbol_TV = (TextView) view.findViewById(R.id.symbol_TV);
        symbol_ET = (EditText) view.findViewById(R.id.symbol_ET);
        symbolPosition_B = (Button) view.findViewById(R.id.symbolPosition_B);
        exchangeRate_ET = (EditText) view.findViewById(R.id.exchangeRate_ET);
        mainCurrencyContainer_V = view.findViewById(R.id.mainCurrencyContainer_V);
        exchangeRateContainer_V = view.findViewById(R.id.exchangeRateContainer_V);
        isDefault_CB = (CheckBox) view.findViewById(R.id.isDefault_CB);
        final TextView currentMainCurrency_TV = (TextView) view.findViewById(R.id.currentMainCurrency_TV);
        final ImageButton refreshRate_B = (ImageButton) view.findViewById(R.id.refreshRate_B);

        // Setup
        prepareCurrenciesAutoComplete();
        currentMainCurrency_TV.setText(getString(R.string.f_current_main_currency_x, Currency.getDefault().getCode()));
        decimalsCount_B.setOnClickListener(this);
        thousandsSeparator_B.setOnClickListener(this);
        decimalSeparator_B.setOnClickListener(this);
        symbolPosition_B.setOnClickListener(this);
        isDefault_CB.setOnCheckedChangeListener(this);
        refreshRate_B.setOnClickListener(this);
        symbol_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //noinspection ConstantConditions
                model.setSymbol(symbol_ET.getText().toString());
                updateFormatView();

                updateSymbolTitlePosition();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        code_ET.addTextChangedListener(new TextWatcher() {
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgressBar();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onSave(Context context, Currency model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getCode()) || model.getCode().length() != 3 || model.getCode().equals(Currency.getDefault().getCode())) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            DataStore.insert().values(model.asContentValues()).into(CurrenciesProvider.uriCurrencies());
        }

        return canSave;
    }

    @Override
    protected void ensureModelUpdated(Currency model) {
        //noinspection ConstantConditions
        model.setCode(code_ET.getText().toString());
        //noinspection ConstantConditions
        model.setSymbol(symbol_ET.getText().toString());
        double exchangeRate;
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
    protected Query getQuery() {
        return Query.create()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION);
    }

    @Override
    protected Currency getModelFrom(Cursor cursor) {
        return Currency.from(cursor);
    }

    @Override
    protected void onModelLoaded(Currency model) {
        symbol_ET.setText(model.getSymbol());
        code_ET.setText(model.getCode());
        thousandsSeparator_B.setText(model.getGroupSeparator().explanation(getActivity()));
        decimalSeparator_B.setText(model.getDecimalSeparator().explanation(getActivity()));
        decimalsCount_B.setText(String.valueOf(model.getDecimalCount()));
        exchangeRate_ET.setText(String.valueOf(model.getExchangeRate()));
        isDefault_CB.setChecked(model.isDefault());
        updateFormatView();
        updateCodeTitlePosition();
        updateSymbolTitlePosition();

        code_ET.setEnabled(model.getId() == 0);
        mainCurrencyContainer_V.setVisibility(model.getId() == Currency.getDefault().getId() ? View.GONE : View.VISIBLE);
        exchangeRateContainer_V.setVisibility(model.isDefault() ? View.GONE : View.VISIBLE);
        updateProgressBar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CURRENCIES) {
            return Query.create()
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refreshRate_B: {
                ensureModelUpdated(model);
                final String code = model.getCode();
                if (!TextUtils.isEmpty(code) && code.length() == 3) {
                    CurrenciesAsyncApi.get().updateExchangeRate(code);
                }
                break;
            }
            case R.id.symbolPosition_B: {
                final String[] values = new String[Currency.SymbolPosition.values().length];
                int index = 0;
                for (Currency.SymbolPosition symbolPosition : Currency.SymbolPosition.values()) {
                    values[index++] = symbolPosition.explanation(getActivity());
                }
                final ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, values);
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow_LPW.dismiss();
                        listPopupWindow_LPW = null;
                        model.setSymbolPosition(Currency.SymbolPosition.values()[position]);
                        ensureModelUpdated(model);
                        onModelLoaded(model);
                    }
                };
                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.thousandsSeparator_B: {
                final String[] values = new String[Currency.GroupSeparator.values().length];
                int index = 0;
                for (Currency.GroupSeparator groupSeparator : Currency.GroupSeparator.values()) {
                    values[index++] = groupSeparator.explanation(getActivity());
                }
                final ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, values);
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow_LPW.dismiss();
                        listPopupWindow_LPW = null;
                        model.setGroupSeparator(Currency.GroupSeparator.values()[position]);
                        ensureModelUpdated(model);
                        onModelLoaded(model);
                    }
                };
                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.decimalSeparator_B: {
                final String[] values = new String[Currency.DecimalSeparator.values().length];
                int index = 0;
                for (Currency.DecimalSeparator decimalSeparator : Currency.DecimalSeparator.values()) {
                    values[index++] = decimalSeparator.explanation(getActivity());
                }
                final ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, values);
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow_LPW.dismiss();
                        listPopupWindow_LPW = null;
                        model.setDecimalSeparator(Currency.DecimalSeparator.values()[position]);
                        ensureModelUpdated(model);
                        onModelLoaded(model);
                    }
                };
                showPopupList(view, adapter, itemClickListener);
                break;
            }

            case R.id.decimalsCount_B: {
                final ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, new String[]{"0", "1", "2"});
                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        listPopupWindow_LPW.dismiss();
                        listPopupWindow_LPW = null;
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

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(CurrencyRequest.CurrencyRequestEvent event) {
        updateProgressBar();
        if (model != null && CurrencyRequest.getUniqueId(model.getCode(), Currency.getDefault().getCode()).equals(event.getRequest().getUniqueId())) {
            model.setExchangeRate(event.getParsedResponse().getExchangeRate());
            onModelLoaded(model);
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
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void checkForCurrencyDuplicate(String code) {
        if (isCurrencyExists(code) && model.getId() == 0) {
            code_ET.setError(getString(R.string.l_currency_exists));
        } else {
            code_ET.setError(null);
        }
    }

    private boolean isCurrencyExists(String code) {
        return existingCurrencyCodes.contains(code.toUpperCase());
    }

    private void updateFormatView() {
        symbolPosition_B.setText(MoneyFormatter.format(model, 100000, false));
    }

    private void showPopupList(View anchorView, ListAdapter adapter, AdapterView.OnItemClickListener itemClickListener) {
        listPopupWindow_LPW = new ListPopupWindow(getActivity());
        listPopupWindow_LPW.setModal(true);
        // listPopupWindow_LPW.setListSelector(getResources().getDrawable(R.drawable.btn_borderless));
        listPopupWindow_LPW.setAdapter(adapter);
        listPopupWindow_LPW.setOnItemClickListener(itemClickListener);
        listPopupWindow_LPW.setAnchorView(anchorView);
        listPopupWindow_LPW.show();
    }

    private void updateProgressBar() {
        final boolean isFetchingCurrency = model != null && BaseRequestEvent.isWorking(CurrencyRequest.CurrencyRequestEvent.class, CurrencyRequest.getUniqueId(model.getCode(), Currency.getDefault().getCode()));
        if (isFetchingCurrency) {
            if (loading_SPB.getVisibility() != View.VISIBLE) {
                loading_SPB.setVisibility(View.VISIBLE);
            }
        } else {
            if (loading_SPB.getVisibility() != View.INVISIBLE) {
                loading_SPB.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateCodeTitlePosition() {
        if (TextUtils.isEmpty(code_ET.getText())) {
            code_TV.animate().translationY(code_ET.getBaseline() + (code_TV.getHeight() - code_TV.getBaseline())).setDuration(100).start();
        } else {
            code_TV.animate().translationY(0).setDuration(100).start();
        }
    }

    private void updateSymbolTitlePosition() {
        if (TextUtils.isEmpty(symbol_ET.getText())) {
            symbol_TV.animate().translationY(symbol_ET.getBaseline() + (symbol_TV.getHeight() - symbol_TV.getBaseline())).setDuration(100).start();
        } else {
            symbol_TV.animate().translationY(0).setDuration(100).start();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
        model.setDefault(isChecked);
        onModelLoaded(model);
    }
}
