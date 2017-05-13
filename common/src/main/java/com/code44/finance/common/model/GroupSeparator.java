package com.code44.finance.common.model;

public enum GroupSeparator {
    None(""),
    Dot("."),
    Comma(","),
    Space(" ");

    private final String symbol;

    GroupSeparator(String symbol) {
        this.symbol = symbol;
    }

    public static GroupSeparator fromSymbol(String symbol) {
        switch (symbol) {
            case "":
                return None;

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
