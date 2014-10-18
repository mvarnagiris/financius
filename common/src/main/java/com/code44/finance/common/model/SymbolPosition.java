package com.code44.finance.common.model;

public enum SymbolPosition {
    CloseRight(SymbolPosition.VALUE_CLOSE_RIGHT),
    FarRight(SymbolPosition.VALUE_FAR_RIGHT),
    CloseLeft(SymbolPosition.VALUE_CLOSE_LEFT),
    FarLeft(SymbolPosition.VALUE_FAR_LEFT);

    private static final int VALUE_CLOSE_RIGHT = 1;
    private static final int VALUE_FAR_RIGHT = 2;
    private static final int VALUE_CLOSE_LEFT = 3;
    private static final int VALUE_FAR_LEFT = 4;

    private final int value;

    private SymbolPosition(int value) {
        this.value = value;
    }

    public static SymbolPosition fromInt(int value) {
        switch (value) {
            case VALUE_CLOSE_RIGHT:
                return CloseRight;

            case VALUE_FAR_RIGHT:
                return FarRight;

            case VALUE_CLOSE_LEFT:
                return CloseLeft;

            case VALUE_FAR_LEFT:
                return FarLeft;

            default:
                throw new IllegalArgumentException("Value " + value + " is not supported.");
        }
    }

    public int asInt() {
        return value;
    }

    public String asString() {
        return String.valueOf(value);
    }
}
