package com.code44.finance.ui.currencies.edit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.DecimalSeparator;
import com.code44.finance.common.model.GroupSeparator;
import com.code44.finance.common.model.SymbolPosition;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.utils.analytics.Screens;

import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CurrencyEditActivity extends ModelEditActivity<CurrencyFormat, CurrencyFormatEditData> implements View.OnClickListener {
    private final CurrencyFormat formatCurrencyFormat = new CurrencyFormat();

    private TextView codeTextView;
    private AutoCompleteTextView codeEditTextView;
    private EditText symbolEditTextView;
    private TextView errorTextView;
    private TextView currencyFormatTextView;

    public static void start(Context context, String currencyId) {
        makeActivityStarter(context, CurrencyEditActivity.class, currencyId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_edit);

        // Get views
        codeTextView = (TextView) findViewById(R.id.codeTextView);
        codeEditTextView = (AutoCompleteTextView) findViewById(R.id.codeEditTextView);
        symbolEditTextView = (EditText) findViewById(R.id.symbolEditTextView);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        currencyFormatTextView = (TextView) findViewById(R.id.currencyFormatTextView);
        final Button symbolPositionButton = (Button) findViewById(R.id.symbolPositionButton);
        final Button groupSeparatorButton = (Button) findViewById(R.id.groupSeparatorButton);
        final Button decimalSeparatorButton = (Button) findViewById(R.id.decimalSeparatorButton);
        final Button decimalCountButton = (Button) findViewById(R.id.decimalCountButton);

        // Setup
        symbolPositionButton.setOnClickListener(this);
        groupSeparatorButton.setOnClickListener(this);
        decimalSeparatorButton.setOnClickListener(this);
        decimalCountButton.setOnClickListener(this);
        codeEditTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                getModelEditData().setCode(codeEditTextView.getText().toString());
                updateFormat();
            }
        });
        symbolEditTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override public void afterTextChanged(Editable editable) {
                getModelEditData().setSymbol(symbolEditTextView.getText().toString());
                updateFormat();
            }
        });

        if (isNewModel()) {
            prepareCurrenciesAutoComplete();
        } else {
            codeTextView.setVisibility(View.VISIBLE);
            codeEditTextView.setVisibility(View.GONE);
        }
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.CurrencyFormats.getQuery().asCursorLoader(this, CurrenciesProvider.uriCurrency(modelId));
    }

    @NonNull @Override protected CurrencyFormat getModelFrom(@NonNull Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    @NonNull @Override protected CurrencyFormatEditData createModelEditData() {
        return new CurrencyFormatEditData();
    }

    @NonNull @Override protected ModelEditValidator<CurrencyFormatEditData> createModelEditValidator() {
        return new CurrencyFormatEditValidator(this, errorTextView, isNewModel());
    }

    @Override protected void onDataChanged(@NonNull CurrencyFormatEditData modelEditData) {
        final CurrencyFormatEditData currencyFormatEditData = getModelEditData();
        codeTextView.setText(currencyFormatEditData.getCode());
        codeEditTextView.setText(currencyFormatEditData.getCode());
        symbolEditTextView.setText(currencyFormatEditData.getSymbol());
        updateFormat();
    }

    @NonNull @Override protected Uri getSaveUri() {
        return CurrenciesProvider.uriCurrencies();
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
        }
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.CurrencyEdit;
    }

    private void updateFormat() {
        final CurrencyFormatEditData currencyFormatEditData = getModelEditData();
        formatCurrencyFormat.setCode(currencyFormatEditData.getCode());
        formatCurrencyFormat.setSymbol(currencyFormatEditData.getSymbol());
        formatCurrencyFormat.setSymbolPosition(currencyFormatEditData.getSymbolPosition());
        formatCurrencyFormat.setGroupSeparator(currencyFormatEditData.getGroupSeparator());
        formatCurrencyFormat.setDecimalSeparator(currencyFormatEditData.getDecimalSeparator());
        formatCurrencyFormat.setDecimalCount(currencyFormatEditData.getDecimalCount());
        currencyFormatTextView.setText(formatCurrencyFormat.format(100000));
    }

    private void prepareCurrenciesAutoComplete() {
        // Build currencies set
        final Set<Currency> currencySet = new HashSet<>();
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
        codeEditTextView.setAdapter(autoCompleteAdapter);
        codeEditTextView.setThreshold(0);
    }

    private void toggleSymbolPosition() {
        switch (getModelEditData().getSymbolPosition()) {
            case CloseRight:
                getModelEditData().setSymbolPosition(SymbolPosition.FarLeft);
                break;
            case FarRight:
                getModelEditData().setSymbolPosition(SymbolPosition.CloseRight);
                break;
            case CloseLeft:
                getModelEditData().setSymbolPosition(SymbolPosition.FarRight);
                break;
            case FarLeft:
                getModelEditData().setSymbolPosition(SymbolPosition.CloseLeft);
                break;
            default:
                throw new IllegalArgumentException("Symbol position " + getModelEditData().getSymbolPosition() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleGroupSeparator() {
        switch (getModelEditData().getGroupSeparator()) {
            case None:
                getModelEditData().setGroupSeparator(GroupSeparator.Comma);
                break;
            case Dot:
                getModelEditData().setGroupSeparator(GroupSeparator.Space);
                break;
            case Comma:
                getModelEditData().setGroupSeparator(GroupSeparator.Dot);
                break;
            case Space:
                getModelEditData().setGroupSeparator(GroupSeparator.None);
                break;
            default:
                throw new IllegalArgumentException("Group separator " + getModelEditData().getGroupSeparator() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleDecimalSeparator() {
        switch (getModelEditData().getDecimalSeparator()) {
            case Dot:
                getModelEditData().setDecimalSeparator(DecimalSeparator.Space);
                break;
            case Comma:
                getModelEditData().setDecimalSeparator(DecimalSeparator.Dot);
                break;
            case Space:
                getModelEditData().setDecimalSeparator(DecimalSeparator.Comma);
                break;
            default:
                throw new IllegalArgumentException("Decimal separator " + getModelEditData().getDecimalSeparator() + " is not supported.");
        }
        updateFormat();
    }

    private void toggleDecimalCount() {
        switch (getModelEditData().getDecimalCount()) {
            case 0:
                getModelEditData().setDecimalCount(2);
                break;
            case 1:
                getModelEditData().setDecimalCount(0);
                break;
            case 2:
                getModelEditData().setDecimalCount(1);
                break;
            default:
                throw new IllegalArgumentException("Decimal count " + getModelEditData().getDecimalCount() + " is not supported.");
        }
        updateFormat();
    }
}
