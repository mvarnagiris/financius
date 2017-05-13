package com.code44.finance.common.model;

public enum DecimalSeparator {
    Dot("."),
    Comma(","),
    Space(" ");

    private final String symbol;

    DecimalSeparator(String symbol) {
        this.symbol = symbol;
    }

    public static DecimalSeparator fromSymbol(String symbol) {
        switch (symbol) {
            case ".":
                return Dot;

            case ",":
                return Comma;

            case " ":
                return Space;

            default:
                throw new IllegalArgumentException("Symbol '" + symbol + "' is not supported.");
        }
    }

    public String symbol() {
        return symbol;
    }
}
