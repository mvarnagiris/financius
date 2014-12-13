package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.db.Column;

import java.util.UUID;

public abstract class Model implements Parcelable {
    private long localId;
    private String id;
    private ModelState modelState;
    private SyncState syncState;

    protected Model() {
        localId = 0;
        setId(null);
        setModelState(ModelState.Normal);
        setSyncState(SyncState.None);
    }

    protected Model(Parcel parcel) {
        localId = parcel.readLong();
        setId(parcel.readString());
        setModelState(ModelState.fromInt(parcel.readInt()));
        setSyncState(SyncState.fromInt(parcel.readInt()));
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(localId);
        parcel.writeString(id);
        parcel.writeInt(modelState.asInt());
        parcel.writeInt(syncState.asInt());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Model)) return false;

        final Model model = (Model) o;

        // We are only checking id, because otherwise some parts of the app might misbehave
        // For example BaseModelAdapter contains Set<BaseModel> selectedItems
        // noinspection RedundantIfStatement
        return !(Strings.isEmpty(id) || Strings.isEmpty(model.id)) && id.equals(model.id);

    }

    @Override public int hashCode() {
        // TODO Should create a better hash code for models that doesn't have id.
        return id != null ? id.hashCode() : 0;
    }

    protected abstract Column getLocalIdColumn();

    protected abstract Column getIdColumn();

    protected abstract Column getModelStateColumn();

    protected abstract Column getSyncStateColumn();

    public void prepareForDb() {
        if (Strings.isEmpty(id)) {
            id = UUID.randomUUID().toString();
        }

        if (modelState == null) {
            modelState = ModelState.Normal;
        }

        if (syncState == null) {
            syncState = SyncState.None;
        }
    }

    public void validate() {
        Preconditions.notEmpty(id, "Id cannot be empty.");
        Preconditions.notNull(modelState, "ModelState cannot be null.");
        Preconditions.notNull(syncState, "SyncState cannot be null.");
    }

    public ContentValues asValues() {
        prepareForDb();
        validate();

        final ContentValues values = new ContentValues();

        if (localId != 0) {
            values.put(getLocalIdColumn().getName(), localId);
        }

        values.put(getIdColumn().getName(), id);
        values.put(getModelStateColumn().getName(), modelState.asInt());
        values.put(getSyncStateColumn().getName(), syncState.asInt());

        return values;
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
            localId = cursor.getLong(index);
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
    }

    public boolean hasId() {
        return !Strings.isEmpty(id);
    }
}
