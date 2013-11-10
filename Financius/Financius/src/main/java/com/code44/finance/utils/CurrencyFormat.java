package com.code44.finance.utils;

import com.code44.finance.db.Tables;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyFormat
{
    private final DecimalFormat decimalFormat;

    public CurrencyFormat()
    {
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
    }

    public CurrencyFormat(char groupSeparator, char decimalSeparator, int decimals, String symbol, String symbolFormat)
    {
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance();

        // Setup symbols
        final DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(groupSeparator);
        symbols.setDecimalSeparator(decimalSeparator);

        // Setup format
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setMinimumFractionDigits(decimals);
        decimalFormat.setMaximumFractionDigits(decimals);
        if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.LEFT_FAR))
        {
            decimalFormat.setPositivePrefix(symbol + " ");
            decimalFormat.setNegativePrefix(symbol + " -");
        }
        else if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.LEFT_CLOSE))
        {
            decimalFormat.setPositivePrefix(symbol);
            decimalFormat.setNegativePrefix(symbol + "-");
        }
        else if (symbolFormat.equalsIgnoreCase(Tables.Currencies.SymbolFormat.RIGHT_FAR))
        {
            decimalFormat.setPositiveSuffix(" " + symbol);
            decimalFormat.setNegativeSuffix(" " + symbol);
        }
        else
        {
            decimalFormat.setPositiveSuffix(symbol);
            decimalFormat.setNegativeSuffix(symbol);
        }
    }

    public String format(double value)
    {
        return decimalFormat.format(value);
    }
}