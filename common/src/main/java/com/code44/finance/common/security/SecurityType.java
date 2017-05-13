package com.code44.finance.common.security;

public enum SecurityType {
    None(0), Pin(1);

    private final int value;

    SecurityType(int value) {
        this.value = value;
    }

    public static SecurityType fromInt(int value) {
        switch (value) {
            case 1:
                return Pin;
            default:
                return None;
        }
    }

    public int asInt() {
        return value;
    }
}
