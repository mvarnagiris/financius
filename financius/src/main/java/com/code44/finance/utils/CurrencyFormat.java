package com.code44.finance.utils;

import com.code44.finance.data.db.model.Currency;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class CurrencyFormat {
    private final DecimalFormat decimalFormat;

    public CurrencyFormat() {
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
    }

    public CurrencyFormat(Currency currency) {
        decimalFormat = (DecimalFormat) DecimalFormat.getInstance();

        // Setup symbols
        final DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getGroupSeparator().symbol().charAt(0));
        symbols.setDecimalSeparator(currency.getDecimalSeparator().symbol().charAt(0));

        // Setup format
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setMinimumFractionDigits(currency.getDecimalCount());
        decimalFormat.setMaximumFractionDigits(currency.getDecimalCount());
        final String symbol = currency.getSymbol();
        switch (currency.getSymbolPosition()) {
            case FAR_LEFT:
                decimalFormat.setPositivePrefix(symbol + " ");
                decimalFormat.setNegativePrefix(symbol + " -");
                break;

            case CLOSE_LEFT:
                decimalFormat.setPositivePrefix(symbol);
                decimalFormat.setNegativePrefix(symbol + "-");
                break;

            case FAR_RIGHT:
                decimalFormat.setPositiveSuffix(" " + symbol);
                decimalFormat.setNegativeSuffix(" " + symbol);
                break;

            case CLOSE_RIGHT:
                decimalFormat.setPositiveSuffix(symbol);
                decimalFormat.setNegativeSuffix(symbol);
                break;

            default:
                throw new IllegalArgumentException("Symbol position " + currency.getSymbolPosition() + " is not supported.");
        }
    }

    public String format(double value) {
        return decimalFormat.format(value);
    }
}
