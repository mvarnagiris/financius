package com.code44.finance.db.model;

import android.database.Cursor;
import com.code44.finance.db.Tables;

public class Currency extends DBRecord
{
    private String code;
    private String symbol;
    private int decimals;
    private String decimalSeparator;
    private String groupSeparator;
    private String symbolFormat;
    private boolean isDefault;
    private double exchangeRate;

    public static Currency from(Cursor c, String idColumnName)
    {
        final Currency currency = new Currency();
        initBase(currency, c, c.getLong(c.getColumnIndex(idColumnName)), Tables.Currencies.TABLE_NAME);

        final int iCode = c.getColumnIndex(Tables.Currencies.CODE);
        final int iSymbol = c.getColumnIndex(Tables.Currencies.SYMBOL);
        final int iDecimals = c.getColumnIndex(Tables.Currencies.DECIMALS);
        final int iDecimalSeparator = c.getColumnIndex(Tables.Currencies.DECIMAL_SEPARATOR);
        final int iGroupSeparator = c.getColumnIndex(Tables.Currencies.GROUP_SEPARATOR);
        final int iSymbolFormat = c.getColumnIndex(Tables.Currencies.SYMBOL_FORMAT);
        final int iIsDefault = c.getColumnIndex(Tables.Currencies.IS_DEFAULT);
        final int iExchangeRate = c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE);

        if (iCode >= 0)
            currency.setCode(c.getString(iCode));

        if (iSymbol >= 0)
            currency.setSymbol(c.getString(iSymbol));

        if (iDecimals >= 0)
            currency.setDecimals(c.getInt(iDecimals));

        if (iDecimalSeparator >= 0)
            currency.setDecimalSeparator(c.getString(iDecimalSeparator));

        if (iGroupSeparator >= 0)
            currency.setGroupSeparator(c.getString(iGroupSeparator));

        if (iSymbolFormat >= 0)
            currency.setSymbolFormat(c.getString(iSymbolFormat));

        if (iIsDefault >= 0)
            currency.setDefault(c.getInt(iIsDefault) != 0);

        if (iExchangeRate >= 0)
            currency.setExchangeRate(c.getDouble(iExchangeRate));

        return currency;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public int getDecimals()
    {
        return decimals;
    }

    public void setDecimals(int decimals)
    {
        this.decimals = decimals;
    }

    public String getDecimalSeparator()
    {
        return decimalSeparator;
    }

    public void setDecimalSeparator(String decimalSeparator)
    {
        this.decimalSeparator = decimalSeparator;
    }

    public String getGroupSeparator()
    {
        return groupSeparator;
    }

    public void setGroupSeparator(String groupSeparator)
    {
        this.groupSeparator = groupSeparator;
    }

    public String getSymbolFormat()
    {
        return symbolFormat;
    }

    public void setSymbolFormat(String symbolFormat)
    {
        this.symbolFormat = symbolFormat;
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefault(boolean aDefault)
    {
        isDefault = aDefault;
    }

    public double getExchangeRate()
    {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate)
    {
        this.exchangeRate = exchangeRate;
    }
}