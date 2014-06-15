package com.code44.finance.data.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.code44.finance.data.db.Column;

import java.util.UUID;

public abstract class BaseModel implements Parcelable {
    private long id;
    private String serverId;
    private ItemState itemState;
    private SyncState syncState;

    protected BaseModel() {
        setId(0);
        setServerId(null);
        setItemState(ItemState.NORMAL);
        setSyncState(SyncState.NONE);
    }

    protected BaseModel(Parcel in) {
        setId(in.readLong());
        setServerId(in.readString());
        setItemState(ItemState.fromInt(in.readInt()));
        setSyncState(SyncState.fromInt(in.readInt()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getServerId());
        dest.writeInt(getItemState().asInt());
        dest.writeInt(getSyncState().asInt());
    }

    public ContentValues asContentValues() {
        final ContentValues values = new ContentValues();

        if (id != 0) {
            values.put(getIdColumn().getName(), id);
        }

        if (TextUtils.isEmpty(serverId)) {
            setServerId(UUID.randomUUID().toString());
        }

        values.put(getServerIdColumn().getName(), serverId);
        values.put(getItemStateColumn().getName(), itemState.asInt());
        values.put(getSyncStateColumn().getName(), syncState.asInt());

        return values;
    }

    public void checkValues() throws IllegalStateException {
        if (serverId == null) {
            throw new IllegalStateException("ServerId cannot be empty.");
        }

        if (itemState == null) {
            throw new IllegalStateException("ItemState cannot be null.");
        }

        if (syncState == null) {
            throw new IllegalStateException("SyncState cannot be null.");
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public ItemState getItemState() {
        return itemState;
    }

    public void setItemState(ItemState itemState) {
        this.itemState = itemState;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    protected abstract Column getIdColumn();

    protected abstract Column getServerIdColumn();

    protected abstract Column getItemStateColumn();

    protected abstract Column getSyncStateColumn();

    protected void updateFrom(Cursor cursor, String columnPrefixTable) {
        int index;

        index = cursor.getColumnIndex(getIdColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setId(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(getServerIdColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setServerId(cursor.getString(index));
        }

        index = cursor.getColumnIndex(getItemStateColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setItemState(ItemState.fromInt(cursor.getInt(index)));
        }

        index = cursor.getColumnIndex(getSyncStateColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setSyncState(SyncState.fromInt(cursor.getInt(index)));
        }
    }

    public static enum ItemState {
        NORMAL(ItemState.VALUE_NORMAL), DELETED(ItemState.VALUE_DELETED), DELETED_UNDO(ItemState.VALUE_DELETED_UNDO);

        private static final int VALUE_NORMAL = 1;
        private static final int VALUE_DELETED = 2;
        private static final int VALUE_DELETED_UNDO = 3;

        private final int value;

        private ItemState(int value) {
            this.value = value;
        }

        public static ItemState fromInt(int value) {
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
    }

    public static enum SyncState {
        NONE(SyncState.VALUE_NONE), IN_PROGRESS(SyncState.VALUE_IN_PROGRESS), SYNCED(SyncState.VALUE_SYNCED), LOCAL_CHANGES(SyncState.VALUE_LOCAL_CHANGES);

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
    }
}
