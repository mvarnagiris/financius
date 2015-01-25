package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.db.Column;

import java.util.UUID;

public abstract class Model extends BaseModel {
    private String id;
    private ModelState modelState;
    private SyncState syncState;

    protected Model() {
        super();
        setId(null);
        setModelState(ModelState.Normal);
        setSyncState(SyncState.None);
    }

    protected Model(Parcel parcel) {
        super(parcel);
        setId(parcel.readString());
        setModelState(ModelState.fromInt(parcel.readInt()));
        setSyncState(SyncState.fromInt(parcel.readInt()));
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(id);
        parcel.writeInt(modelState.asInt());
        parcel.writeInt(syncState.asInt());
    }

    @Override public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();

        values.put(getIdColumn().getName(), id);
        values.put(getModelStateColumn().getName(), modelState.asInt());
        values.put(getSyncStateColumn().getName(), syncState.asInt());

        return values;
    }

    @Override public void prepareForContentValues() {
        super.prepareForContentValues();

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

    @Override public void validateForContentValues() {
        super.validateForContentValues();
        Preconditions.notEmpty(id, "Id cannot be empty.");
        Preconditions.notNull(modelState, "ModelState cannot be null.");
        Preconditions.notNull(syncState, "SyncState cannot be null.");
    }

    @Override public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        super.updateFromCursor(cursor, columnPrefixTable);
        int index;

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
        return id != null ? id.hashCode() : 0;
    }

    protected abstract Column getIdColumn();

    protected abstract Column getModelStateColumn();

    protected abstract Column getSyncStateColumn();

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

    public boolean hasId() {
        return !Strings.isEmpty(id);
    }
}
