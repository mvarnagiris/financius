package com.code44.finance.data.db.model;

public enum SyncState {
    NONE(SyncState.VALUE_NONE),
    IN_PROGRESS(SyncState.VALUE_IN_PROGRESS),
    SYNCED(SyncState.VALUE_SYNCED),
    LOCAL_CHANGES(SyncState.VALUE_LOCAL_CHANGES);

    private static final int VALUE_NONE = 1;
    private static final int VALUE_IN_PROGRESS = 2;
    private static final int VALUE_SYNCED = 3;
    private static final int VALUE_LOCAL_CHANGES = 4;

    private final int value;

    private SyncState(int value) {
        this.value = value;
    }

    public static SyncState fromInt(int value) {
        switch (value) {
            case VALUE_NONE:
                return NONE;

            case VALUE_IN_PROGRESS:
                return IN_PROGRESS;

            case VALUE_SYNCED:
                return SYNCED;

            case VALUE_LOCAL_CHANGES:
                return LOCAL_CHANGES;

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
