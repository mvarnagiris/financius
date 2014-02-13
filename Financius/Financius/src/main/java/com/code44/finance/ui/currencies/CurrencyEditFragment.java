package com.code44.finance.ui.currencies;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.ItemEditStepsFragment;
import com.code44.finance.utils.CurrenciesUtils;

public class CurrencyEditFragment extends ItemEditStepsFragment
{
    private static final String FRAGMENT_CURRENT = "FRAGMENT_CURRENT";

    public static CurrencyEditFragment newInstance(long itemId)
    {
        final Bundle args = makeArgs(itemId);

        final CurrencyEditFragment f = new CurrencyEditFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public int getStepsCount()
    {
        return itemId == 0 ? 2 : 1;
    }

    @Override
    public boolean onSave(Context context, long itemId)
    {
        final CurrencyFormatFragment f = (CurrencyFormatFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT);
        final String code = f.getCurrencyCode();
        final String symbol = f.getSymbol();
        final int decimals = f.getDecimals();
        final String groupSeparator = f.getGroupSeparator();
        final String decimalSeparator = f.getDecimalSeparator();
        final boolean isDefault = f.isMainCurrency();
        final String symbolFormat = f.getSymbolFormat();
        final double exchangeRate = f.getExchangeRate();

        ContentValues values = CurrenciesUtils.getValues(code, symbol, decimals, groupSeparator, decimalSeparator, isDefault, symbolFormat, exchangeRate);
        if (itemId == 0)
            API.createItem(CurrenciesProvider.uriCurrencies(), values);
        else
            API.updateItem(CurrenciesProvider.uriCurrencies(), itemId, values);

        return true;
    }

    @Override
    public boolean onDiscard()
    {
        return true;
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState)
    {
        // Fragments
        if (getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT) == null)
            getChildFragmentManager().beginTransaction().replace(R.id.container_V, getStepsCount() == 2 ? CurrencyCodeFragment.newInstance() : CurrencyFormatFragment.newInstance(itemId, null), FRAGMENT_CURRENT).commit();
    }

    @Override
    protected int onNextStep()
    {
        final String currencyCode = ((CurrencyCodeFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_CURRENT)).getCurrencyCode();

        if (TextUtils.isEmpty(currencyCode) || currencyCode.length() != 3)
            return -1;

        getChildFragmentManager().beginTransaction().replace(R.id.container_V, CurrencyFormatFragment.newInstance(itemId, currencyCode), FRAGMENT_CURRENT).addToBackStack(null).commit();
        return 1;
    }

    @Override
    protected int onPrevStep()
    {
        getChildFragmentManager().popBackStack();
        return 0;
    }
}
