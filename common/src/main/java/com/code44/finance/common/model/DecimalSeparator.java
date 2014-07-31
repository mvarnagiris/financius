package com.code44.finance.common.model;

public enum DecimalSeparator {
    DOT("."),
    COMMA(","),
    SPACE(" ");

    private final String symbol;

    private DecimalSeparator(String symbol) {
        this.symbol = symbol;
    }

    public static DecimalSeparator fromSymbol(String symbol) {
        switch (symbol) {
            case ".":
                return DOT;

            case ",":
                return COMMA;

            case " ":
                return SPACE;

            default:
                throw new IllegalArgumentException("Symbol '" + symbol + "' is not supported.");
        }
    }

    public String symbol() {
        return symbol;
    }
}
