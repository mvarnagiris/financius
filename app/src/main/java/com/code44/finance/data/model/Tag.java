package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.backend.financius.model.TagEntity;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Tag extends Model<TagEntity> {
    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    private String title;

    public Tag() {
        super();
        setTitle(null);
    }

    public Tag(Parcel parcel) {
        super(parcel);
        setTitle(parcel.readString());
    }

    public static Tag from(Cursor cursor) {
        final Tag tag = new Tag();
        if (cursor.getCount() > 0) {
            tag.updateFromCursor(cursor, null);
        }
        return tag;
    }

    public static Tag from(TagEntity entity) {
        final Tag tag = new Tag();
        tag.updateFromEntity(entity);
        return tag;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(getTitle());
    }

    @Override public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();
        values.put(Tables.Tags.TITLE.getName(), title);
        return values;
    }

    @Override public void validateForContentValues() throws IllegalStateException {
        super.validateForContentValues();
        checkState(!checkNotNull(title).isEmpty(), "Title cannot be empty.");
    }

    @Override public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        super.updateFromCursor(cursor, columnPrefixTable);
        int index;

        // Title
        index = cursor.getColumnIndex(Tables.Tags.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }
    }

    @Override public void updateFromEntity(TagEntity entity) {
        super.updateFromEntity(entity);
        setTitle(entity.getTitle());
    }

    @Override protected Column getIdColumn() {
        return Tables.Tags.ID;
    }

    @Override protected Column getModelStateColumn() {
        return Tables.Tags.MODEL_STATE;
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Tags.SYNC_STATE;
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.Tags.LOCAL_ID;
    }

    @Override protected TagEntity createEntity() {
        return new TagEntity();
    }

    @Override public TagEntity asEntity() {
        final TagEntity entity = super.asEntity();
        entity.setTitle(title);
        return entity;
    }

    public String getTitle() {
        return title;
    }

    public Tag setTitle(String title) {
        this.title = title;
        return this;
    }
}
