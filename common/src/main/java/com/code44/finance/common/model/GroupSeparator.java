package com.code44.finance.common.model;

public enum GroupSeparator {
    NONE(""),
    DOT("."),
    COMMA(","),
    SPACE(" ");

    private final String symbol;

    private GroupSeparator(String symbol) {
        this.symbol = symbol;
    }

    public static GroupSeparator fromSymbol(String symbol) {
        switch (symbol) {
            case "":
                return NONE;

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
