package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.db.Column;
import com.google.api.client.json.GenericJson;

import java.util.UUID;

public abstract class BaseModel<E extends GenericJson> implements Parcelable {
    private long localId;
    private String id;
    private ModelState modelState;
    private SyncState syncState;

    protected BaseModel() {
        setLocalId(0);
        setId(null);
        setModelState(ModelState.NORMAL);
        setSyncState(SyncState.NONE);
    }

    protected BaseModel(Parcel parcel) {
        setLocalId(parcel.readLong());
        setId(parcel.readString());
        setModelState(ModelState.fromInt(parcel.readInt()));
        setSyncState(SyncState.fromInt(parcel.readInt()));
        fromParcel(parcel);
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(getLocalId());
        parcel.writeString(getId());
        parcel.writeInt(getModelState().asInt());
        parcel.writeInt(getSyncState().asInt());
        toParcel(parcel);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseModel)) return false;

        final BaseModel baseModel = (BaseModel) o;

        // We are only checking id, because otherwise some parts of the app might misbehave
        // For example BaseModelAdapter contains Set<BaseModel> selectedItems
        // noinspection RedundantIfStatement
        return !(StringUtils.isEmpty(id) || StringUtils.isEmpty(baseModel.id)) && id.equals(baseModel.id);

    }

    @Override public int hashCode() {
        // TODO Should create a better hash code for models that doesn't have id.
        return id != null ? id.hashCode() : 0;
    }

    protected abstract Column getLocalIdColumn();

    protected abstract Column getIdColumn();

    protected abstract Column getModelStateColumn();

    protected abstract Column getSyncStateColumn();

    protected abstract void toValues(ContentValues values);

    protected abstract void toParcel(Parcel parcel);

    protected abstract void toEntity(E entity);

    protected abstract E createEntity();

    protected abstract void fromParcel(Parcel parcel);

    protected abstract void fromCursor(Cursor cursor, String columnPrefixTable);

    protected abstract void fromEntity(E entity);

    public ContentValues asValues() {
        if (StringUtils.isEmpty(id)) {
            setId(UUID.randomUUID().toString());
        }

        checkValues();

        final ContentValues values = new ContentValues();

        // Local id
        if (localId != 0) {
            values.put(getLocalIdColumn().getName(), localId);
        }

        // Server id
        values.put(getIdColumn().getName(), id);

        // Model state
        values.put(getModelStateColumn().getName(), modelState.asInt());

        // Sync state
        values.put(getSyncStateColumn().getName(), syncState.asInt());

        // Other values
        toValues(values);

        return values;
    }

    public E asEntity() {
        checkValues();

        final E entity = createEntity();

        // Id
        entity.put("id", id);

        // Model state
        entity.put("model_state", modelState.toString());

        // Other values
        toEntity(entity);

        return entity;
    }

    public void checkValues() throws IllegalStateException {
        Preconditions.checkNotEmpty(id, "Id cannot be empty.");
        Preconditions.checkNotNull(modelState, "ModelState cannot be null.");
        Preconditions.checkNotNull(syncState, "SyncState cannot be null.");
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void updateFrom(Cursor cursor, String columnPrefixTable) {
        int index;

        // Local id
        index = cursor.getColumnIndex(getLocalIdColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setLocalId(cursor.getLong(index));
        }

        // Id
        index = cursor.getColumnIndex(getIdColumn().getName(columnPrefixTable));
        if (index >= 0) {
            setId(cursor.getString(index));
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

    public boolean hasId() {
        return !StringUtils.isEmpty(id);
    }

    protected void updateFrom(E entity) {
        setId((String) entity.get("id"));
        setModelState(ModelState.valueOf((String) entity.get("model_state")));
        setSyncState(SyncState.SYNCED);
        fromEntity(entity);
    }
}
