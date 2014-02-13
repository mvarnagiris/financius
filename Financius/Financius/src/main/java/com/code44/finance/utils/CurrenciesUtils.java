package com.code44.finance.utils;

import android.content.ContentValues;
import com.code44.finance.db.Tables;

public class CurrenciesUtils
{
    public static ContentValues getValues(String code, String symbol, int decimals, String groupSeparator, String decimalSeparator, boolean isDefault, String symbolFormat, double exchangeRate)
    {
        ContentValues values = new ContentValues();

        values.put(Tables.Currencies.SERVER_ID, code);
        values.put(Tables.Currencies.CODE, code);
        values.put(Tables.Currencies.SYMBOL, symbol);
        values.put(Tables.Currencies.DECIMALS, decimals);
        values.put(Tables.Currencies.GROUP_SEPARATOR, groupSeparator);
        values.put(Tables.Currencies.DECIMAL_SEPARATOR, decimalSeparator);
        values.put(Tables.Currencies.IS_DEFAULT, isDefault);
        values.put(Tables.Currencies.SYMBOL_FORMAT, symbolFormat);
        if (isDefault)
            exchangeRate = 1.0;
        values.put(Tables.Currencies.EXCHANGE_RATE, exchangeRate);

        return values;
    }
}