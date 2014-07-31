package com.code44.finance.common.model;

public enum SymbolPosition {
    CLOSE_RIGHT(SymbolPosition.VALUE_CLOSE_RIGHT),
    FAR_RIGHT(SymbolPosition.VALUE_FAR_RIGHT),
    CLOSE_LEFT(SymbolPosition.VALUE_CLOSE_LEFT),
    FAR_LEFT(SymbolPosition.VALUE_FAR_LEFT);

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
                return CLOSE_RIGHT;

            case VALUE_FAR_RIGHT:
                return FAR_RIGHT;

            case VALUE_CLOSE_LEFT:
                return CLOSE_LEFT;

            case VALUE_FAR_LEFT:
                return FAR_LEFT;

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
