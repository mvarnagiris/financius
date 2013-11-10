package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.utils.CurrenciesUtils;
import com.code44.finance.utils.NotifyUtils;

public class CurrenciesService extends AbstractItemService
{
    public static final String EXTRA_CODE = CurrenciesService.class.getName() + ".EXTRA_FROM_CODE";
    public static final String EXTRA_SYMBOL = CurrenciesService.class.getName() + ".EXTRA_SYMBOL";
    public static final String EXTRA_DECIMALS = CurrenciesService.class.getName() + ".EXTRA_DECIMALS";
    public static final String EXTRA_GROUP_SEPARATOR = CurrenciesService.class.getName() + ".EXTRA_GROUP_SEPARATOR";
    public static final String EXTRA_DECIMAL_SEPARATOR = CurrenciesService.class.getName() + ".EXTRA_DECIMAL_SEPARATOR";
    public static final String EXTRA_IS_DEFAULT = CurrenciesService.class.getName() + ".EXTRA_IS_DEFAULT";
    public static final String EXTRA_SYMBOL_FORMAT = CurrenciesService.class.getName() + ".EXTRA_SYMBOL_FORMAT";
    public static final String EXTRA_EXCHANGE_RATE = CurrenciesService.class.getName() + ".EXTRA_EXCHANGE_RATE";

    @Override
    protected void notifyOnItemUpdated()
    {
        NotifyUtils.onCurrencyUpdated(this);
    }

    @Override
    protected void prepareValues(ContentValues outValues, Intent intent)
    {
        // Get values
        final String code = intent.getStringExtra(EXTRA_CODE);
        final String symbol = intent.getStringExtra(EXTRA_SYMBOL);
        final int decimals = intent.getIntExtra(EXTRA_DECIMALS, 2);
        final String groupSeparator = intent.getStringExtra(EXTRA_GROUP_SEPARATOR);
        final String decimalSeparator = intent.getStringExtra(EXTRA_DECIMAL_SEPARATOR);
        final boolean isDefault = intent.getBooleanExtra(EXTRA_IS_DEFAULT, false);
        final String symbolFormat = intent.getStringExtra(EXTRA_SYMBOL_FORMAT);
        final double exchangeRate = intent.getDoubleExtra(EXTRA_EXCHANGE_RATE, 1.0);

        // Prepare
        CurrenciesUtils.prepareValues(outValues, code, symbol, decimals, groupSeparator, decimalSeparator, isDefault, symbolFormat, exchangeRate);
    }

    @Override
    protected Uri getUriForItems()
    {
        return CurrenciesProvider.uriCurrencies(this);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Currencies.TABLE_NAME;
    }
}