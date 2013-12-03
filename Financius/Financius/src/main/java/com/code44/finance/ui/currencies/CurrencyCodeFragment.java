package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.AbstractFragment;
import com.code44.finance.utils.AnimUtils;

import java.util.*;

public class CurrencyCodeFragment extends AbstractFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int LOADER_CURRENCIES = 1;
    private AutoCompleteTextView code_ET;
    private TextView currentCurrency_TV;
    private Map<String, Long> currenciesMap = new HashMap<String, Long>();
    private String defaultCurrencyCode = "";

    public static CurrencyCodeFragment newInstance()
    {
        return new CurrencyCodeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_currency_code, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        code_ET = (AutoCompleteTextView) view.findViewById(R.id.code_ET);
        currentCurrency_TV = (TextView) view.findViewById(R.id.currentCurrency_TV);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        Set<Currency> currencySet = new HashSet<Currency>();
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale loc : locales)
        {
            try
            {
                currencySet.add(Currency.getInstance(loc));
            }
            catch (Exception exc)
            {
                // Locale not found
            }
        }
        String[] currencies = new String[currencySet.size()];
        int i = 0;
        for (Currency currency : currencySet)
        {
            currencies[i++] = currency.getCurrencyCode();
        }
        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, currencies);
        code_ET.setAdapter(autocompleteAdapter);
        code_ET.setThreshold(0);
        code_ET.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkForDuplicate();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        // Loader
        getLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        switch (id)
        {
            case LOADER_CURRENCIES:
            {
                uri = CurrenciesProvider.uriCurrencies();
                projection = new String[]{Tables.Currencies.T_ID, Tables.Currencies.CODE, Tables.Currencies.IS_DEFAULT};
                selection = Tables.Currencies.DELETE_STATE + "=?";
                selectionArgs = new String[]{String.valueOf(Tables.DeleteState.NONE)};
            }
        }

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_CURRENCIES:
                bindCurrencies(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
    }

    @SuppressWarnings("ConstantConditions")
    public String getCurrencyCode()
    {
        if (checkForDuplicate() && code_ET.getText().length() == 3)
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(code_ET.getWindowToken(), 0);
            return code_ET.getText().toString();
        }

        AnimUtils.shake(code_ET);
        return null;
    }

    private void bindCurrencies(Cursor c)
    {
        currenciesMap.clear();
        if (c != null && c.moveToFirst())
        {
            final int iId = c.getColumnIndex(Tables.Currencies.ID);
            final int iCode = c.getColumnIndex(Tables.Currencies.CODE);
            final int iIsDefault = c.getColumnIndex(Tables.Currencies.IS_DEFAULT);

            do
            {
                currenciesMap.put(c.getString(iCode), c.getLong(iId));

                if (c.getInt(iIsDefault) != 0)
                    defaultCurrencyCode = c.getString(iCode);
            }
            while (c.moveToNext());


        }
        checkForDuplicate();
    }

    private boolean checkForDuplicate()
    {
        boolean isOK = true;

        //noinspection ConstantConditions
        if (currenciesMap.keySet().contains(code_ET.getText().toString()))
        {
            currentCurrency_TV.setText(R.string.l_currency_exists);
            currentCurrency_TV.setTextColor(getResources().getColor(R.color.text_red));
            isOK = false;
        }
        else
        {
            currentCurrency_TV.setText(getString(R.string.f_current_main_currency_x, defaultCurrencyCode));
            currentCurrency_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        }

        return isOK;
    }
}