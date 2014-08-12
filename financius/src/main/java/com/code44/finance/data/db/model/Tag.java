package com.code44.finance.data.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.text.TextUtils;

import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

public class Tag extends BaseModel {
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

// TODO   public static Tag from(CategoryEntity entity) {
//        final Tag category = new Tag();
//        category.setServerId(entity.getId());
//        category.setModelState(ModelState.valueOf(entity.getModelState()));
//        category.setSyncState(SyncState.SYNCED);
//        category.setTitle(entity.getTitle());
//        category.setColor(entity.getColor());
//        category.setCategoryType(CategoryType.valueOf(entity.getCategoryType()));
//        category.setCategoryOwner(CategoryOwner.valueOf(entity.getCategoryOwner()));
//        category.setSortOrder(entity.getSortOrder());
//        return category;
//    }

    @Override protected Column getIdColumn() {
        return Tables.Tags.ID;
    }

    @Override protected Column getServerIdColumn() {
        return Tables.Tags.SERVER_ID;
    }

    @Override protected Column getModelStateColumn() {
        return Tables.Tags.MODEL_STATE;
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Tags.SYNC_STATE;
    }

    @Override protected void fromParcel(Parcel parcel) {
        setTitle(parcel.readString());
    }

    @Override protected void toParcel(Parcel parcel) {
        parcel.writeString(getTitle());
    }

    @Override protected void toValues(ContentValues values) {
        values.put(Tables.Tags.TITLE.getName(), title);
    }

    @Override protected void fromCursor(Cursor cursor, String columnPrefixTable) {
        int index;

        // Title
        index = cursor.getColumnIndex(Tables.Tags.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }
    }

    @Override public void checkValues() throws IllegalStateException {
        super.checkValues();

        if (TextUtils.isEmpty(title)) {
            throw new IllegalStateException("Title cannot be empty");
        }
    }

// TODO   public CategoryEntity toEntity() {
//        final CategoryEntity entity = new CategoryEntity();
//        entity.setId(getServerId());
//        entity.setModelState(getModelState().toString());
//        entity.setTitle(getTitle());
//        entity.setColor(getColor());
//        entity.setCategoryType(getCategoryType().toString());
//        entity.setCategoryOwner(getCategoryOwner().toString());
//        entity.setSortOrder(getSortOrder());
//        return entity;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
