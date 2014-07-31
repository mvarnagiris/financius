package com.code44.finance.common.model;

public enum CategoryOwner {
    SYSTEM(CategoryOwner.VALUE_SYSTEM),
    USER(CategoryOwner.VALUE_USER);

    private static final int VALUE_SYSTEM = 1;
    private static final int VALUE_USER = 2;

    private final int value;

    private CategoryOwner(int value) {
        this.value = value;
    }

    public static CategoryOwner fromInt(int value) {
        switch (value) {
            case VALUE_SYSTEM:
                return SYSTEM;

            case VALUE_USER:
                return USER;

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
