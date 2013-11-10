package com.code44.finance.utils;

import android.content.ContentValues;
import com.code44.finance.db.Tables;
import com.code44.finance.db.Tables.SyncState;

public class CurrenciesUtils
{
    /**
     * Prepare values for update or for create.
     *
     * @param values Values container.
     */
    public static void prepareValues(ContentValues values, String code, String symbol, int decimals, String groupSeparator, String decimalSeparator, boolean isDefault, String symbolFormat, double exchangeRate, int deleteState, int syncState)
    {
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
        values.put(Tables.Currencies.TIMESTAMP, System.currentTimeMillis());
        values.put(Tables.Currencies.DELETE_STATE, deleteState);
        values.put(Tables.Currencies.SYNC_STATE, syncState);
    }

    /**
     * Prepare values for update or for create.
     *
     * @param values Values container.
     */
    public static void prepareValues(ContentValues values, String code, String symbol, int decimals, String groupSeparator, String decimalSeparator, boolean isDefault, String symbolFormat, double exchangeRate)
    {
        prepareValues(values, code, symbol, decimals, groupSeparator, decimalSeparator, isDefault, symbolFormat, exchangeRate, Tables.DeleteState.NONE, SyncState.LOCAL_CHANGES);
    }
}