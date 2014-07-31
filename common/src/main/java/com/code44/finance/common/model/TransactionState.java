package com.code44.finance.common.model;

public enum TransactionState {
    CONFIRMED(TransactionState.VALUE_CONFIRMED),
    PENDING(TransactionState.VALUE_PENDING);

    private static final int VALUE_CONFIRMED = 1;
    private static final int VALUE_PENDING = 2;

    private final int value;

    private TransactionState(int value) {
        this.value = value;
    }

    public static TransactionState fromInt(int value) {
        switch (value) {
            case VALUE_CONFIRMED:
                return CONFIRMED;

            case VALUE_PENDING:
                return PENDING;

            default:
                throw new IllegalArgumentException("State " + value + " is not supported.");
        }
    }

    public int asInt() {
        return value;
    }

    public String asString() {
        return String.valueOf(value);
    }
}
