package com.code44.finance.common.model;

public enum TransactionType {
    EXPENSE(TransactionType.VALUE_EXPENSE),
    INCOME(TransactionType.VALUE_INCOME),
    TRANSFER(TransactionType.VALUE_TRANSFER);

    private static final int VALUE_EXPENSE = 1;
    private static final int VALUE_INCOME = 2;
    private static final int VALUE_TRANSFER = 3;

    private final int value;

    private TransactionType(int value) {
        this.value = value;
    }

    public static TransactionType fromInt(int value) {
        switch (value) {
            case VALUE_EXPENSE:
                return EXPENSE;

            case VALUE_INCOME:
                return INCOME;

            case VALUE_TRANSFER:
                return TRANSFER;

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
