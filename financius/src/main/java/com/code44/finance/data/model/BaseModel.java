package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.data.db.Column;

public abstract class BaseModel implements Parcelable {
    private long localId;

    protected BaseModel() {
    }

    protected BaseModel(Parcel parcel) {
        localId = parcel.readLong();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(localId);
    }

    public ContentValues asContentValues() {
        prepareForContentValues();
        validateForContentValues();

        final ContentValues values = new ContentValues();

        if (localId != 0) {
            values.put(getLocalIdColumn().getName(), localId);
        }

        return new ContentValues();
    }

    public void prepareForContentValues() {
    }

    public void validateForContentValues() {
    }

    public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        int index;

        // Local id
        index = cursor.getColumnIndex(getLocalIdColumn().getName(columnPrefixTable));
        if (index >= 0) {
            localId = cursor.getLong(index);
        }
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    protected abstract Column getLocalIdColumn();
}
