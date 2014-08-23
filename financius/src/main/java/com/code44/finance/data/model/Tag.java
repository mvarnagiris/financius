package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.code44.finance.backend.endpoint.tags.model.TagEntity;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

public class Tag extends BaseModel<TagEntity> {
    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
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

    public Tag(Parcel in) {
        super(in);
    }

    public static Tag from(Cursor cursor) {
        final Tag tag = new Tag();
        if (cursor.getCount() > 0) {
            tag.updateFrom(cursor, null);
        }
        return tag;
    }

    public static Tag from(TagEntity entity) {
        final Tag tag = new Tag();
        tag.updateFrom(entity);
        return tag;
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.Tags.LOCAL_ID;
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

    @Override protected void toValues(ContentValues values) {
        values.put(Tables.Tags.TITLE.getName(), title);
    }

    @Override protected void toParcel(Parcel parcel) {
        parcel.writeString(getTitle());
    }

    @Override protected void toEntity(TagEntity entity) {
        entity.setTitle(title);
    }

    @Override protected void fromParcel(Parcel parcel) {
        setTitle(parcel.readString());
    }

    @Override protected void fromCursor(Cursor cursor, String columnPrefixTable) {
        int index;

        // Title
        index = cursor.getColumnIndex(Tables.Tags.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }
    }

    @Override protected void fromEntity(TagEntity entity) {
        setTitle(entity.getTitle());
    }

    @Override protected TagEntity createEntity() {
        return new TagEntity();
    }

    @Override public void checkValues() throws IllegalStateException {
        super.checkValues();
        Preconditions.checkNotEmpty(title, "Title cannot be empty.");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
