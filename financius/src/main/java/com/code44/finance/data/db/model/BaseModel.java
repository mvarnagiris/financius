package com.code44.finance.data.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.db.Column;

import java.util.UUID;

public abstract class BaseModel implements Parcelable {
    private long id;
    private String serverId;
    private ModelState modelState;
    private SyncState syncState;

    protected BaseModel() {
        setId(0);
        setServerId(null);
        setModelState(ModelState.NORMAL);
        setSyncState(SyncState.NONE);
    }

    protected BaseModel(Parcel parcel) {
        setId(parcel.readLong());
        setServerId(parcel.readString());
        setModelState(ModelState.fromInt(parcel.readInt()));
        setSyncState(SyncState.fromInt(parcel.readInt()));
        fromParcel(parcel);
    }

    protected abstract Column getIdColumn();

    protected abstract Column getServerIdColumn();

    protected abstract Column getModelStateColumn();

    protected abstract Column getSyncStateColumn();

    protected abstract void fromParcel(Parcel parcel);

    protected abstract void toParcel(Parcel parcel);

    protected abstract void toValues(ContentValues values);

    protected abstract void fromCursor(Cursor cursor, String columnPrefixTable);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getId());
        parcel.writeString(getServerId());
        parcel.writeInt(getModelState().asInt());
        parcel.writeInt(getSyncState().asInt());
        toParcel(parcel);
    }

    public ContentValues asContentValues() {
        if (StringUtils.isEmpty(getServerId())) {
            setServerId(UUID.randomUUID().toString());
        }
        checkValues();

        final ContentValues values = new ContentValues();

        // Local id
        if (id != 0) {
            values.put(getIdColumn().getName(), id);
        }

        // Server id
        values.put(getServerIdColumn().getName(), getServerId());

        // Model state
        values.put(getModelStateColumn().getName(), getModelState().asInt());

        // Sync state
        values.put(getSyncStateColumn().getName(), getSyncState().asInt());

        // Other values
        toValues(values);

        return values;
    }

    public void checkValues() throws IllegalStateException {
        if (StringUtils.isEmpty(serverId)) {
            throw new IllegalStateException("Server id cannot be empty.");
        }

        if (modelState == null) {
            throw new NullPointerException("ModelState cannot be null.");
        }

        if (syncState == null) {
            throw new NullPointerException("SyncState cannot be null.");
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

    public ModelState getModelState() {
        return modelState;
    }

    public void setModelState(ModelState modelState) {
        this.modelState = modelState;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    protected void updateFrom(Cursor cursor, String columnPrefixTable) {
        int index;

        // Local id
        index = cursor.getColumnIndex(getIdColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setId(cursor.getLong(index));
        }

        // Server id
        index = cursor.getColumnIndex(getServerIdColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setServerId(cursor.getString(index));
        }

        // Model state
        index = cursor.getColumnIndex(getModelStateColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setModelState(ModelState.fromInt(cursor.getInt(index)));
        }

        // Sync state
        index = cursor.getColumnIndex(getSyncStateColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setSyncState(SyncState.fromInt(cursor.getInt(index)));
        }

        // Other values
        fromCursor(cursor, columnPrefixTable);
    }
}
