package com.code44.finance.common.model;

public enum ModelState {
    Normal(ModelState.VALUE_NORMAL),
    Deleted(ModelState.VALUE_DELETED),
    DeletedUndo(ModelState.VALUE_DELETED_UNDO);

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
                return Normal;

            case VALUE_DELETED:
                return Deleted;

            case VALUE_DELETED_UNDO:
                return DeletedUndo;

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
