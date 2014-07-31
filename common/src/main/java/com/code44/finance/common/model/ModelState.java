package com.code44.finance.common.model;

public enum ModelState {
    NORMAL(ModelState.VALUE_NORMAL),
    DELETED(ModelState.VALUE_DELETED),
    DELETED_UNDO(ModelState.VALUE_DELETED_UNDO);

    private static final int VALUE_NORMAL = 1;
    private static final int VALUE_DELETED = 2;
    private static final int VALUE_DELETED_UNDO = 3;

    private final int value;

    private ModelState(int value) {
        this.value = value;
    }

    public static ModelState fromInt(int value) {
        switch (value) {
            case VALUE_NORMAL:
                return NORMAL;

            case VALUE_DELETED:
                return DELETED;

            case VALUE_DELETED_UNDO:
                return DELETED_UNDO;

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
