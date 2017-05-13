package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.db.Column;
import com.google.api.client.json.GenericJson;
import com.google.common.base.Strings;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class Model<E extends GenericJson> extends BaseModel {
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

        if (Strings.isNullOrEmpty(id)) {
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
        checkState(!checkNotNull(id).isEmpty(), "Id cannot be empty.");
        checkNotNull(modelState, "ModelState cannot be null.");
        checkNotNull(syncState, "SyncState cannot be null.");
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Model)) {
            return false;
        }

        final Model model = (Model) o;

        // We are only checking id, because otherwise some parts of the app might misbehave
        // For example BaseModelAdapter contains Set<BaseModel> selectedItems
        // noinspection RedundantIfStatement
        return !(Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(model.id)) && id.equals(model.id);
    }

    @Override public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    protected abstract Column getIdColumn();

    protected abstract Column getModelStateColumn();

    protected abstract Column getSyncStateColumn();

    protected abstract E createEntity();

    public void updateFromEntity(E entity) {
        setId((String) entity.get("id"));
        setModelState(ModelState.valueOf((String) entity.get("model_state")));
        setSyncState(SyncState.Synced);
    }

    public E asEntity() {
        final E entity = createEntity();
        entity.set("id", id);
        entity.set("model_state", modelState.name());
        return entity;
    }

    public String getId() {
        return id;
    }

    public Model setId(String id) {
        this.id = id;
        return this;
    }

    public ModelState getModelState() {
        return modelState;
    }

    public Model setModelState(ModelState modelState) {
        this.modelState = modelState;
        return this;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public Model setSyncState(SyncState syncState) {
        this.syncState = syncState;
        return this;
    }

    public boolean hasId() {
        return !Strings.isNullOrEmpty(id);
    }
}
