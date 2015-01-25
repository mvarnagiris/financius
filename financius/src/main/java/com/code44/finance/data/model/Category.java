package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

public class Category extends Model {
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private String title;
    private int color;
    private TransactionType transactionType;
    private int sortOrder;

    public Category() {
        super();
        setTitle(null);
        setColor(0);
        setTransactionType(TransactionType.Expense);
        setSortOrder(0);
    }

    public Category(Parcel parcel) {
        super(parcel);
        setTitle(parcel.readString());
        setColor(parcel.readInt());
        setTransactionType(TransactionType.fromInt(parcel.readInt()));
        setSortOrder(parcel.readInt());
    }

    public static Category from(Cursor cursor) {
        final Category category = new Category();
        if (cursor.getCount() > 0) {
            category.updateFromCursor(cursor, null);
        }
        return category;
    }

    @Override public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(title);
        parcel.writeInt(color);
        parcel.writeInt(transactionType.asInt());
        parcel.writeInt(sortOrder);
    }

    @Override public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();
        values.put(Tables.Categories.TRANSACTION_TYPE.getName(), transactionType.asInt());
        values.put(Tables.Categories.TITLE.getName(), title);
        values.put(Tables.Categories.COLOR.getName(), color);
        values.put(Tables.Categories.SORT_ORDER.getName(), sortOrder);
        return values;
    }

    @Override public void prepareForContentValues() {
        super.prepareForContentValues();
        if (transactionType == null) {
            transactionType = TransactionType.Expense;
        }
    }

    @Override public void validateForContentValues() throws IllegalStateException {
        super.validateForContentValues();
        Preconditions.notEmpty(title, "Title cannot be empty");
        Preconditions.notNull(transactionType, "Category type cannot be null.");
    }

    @Override public void updateFromCursor(Cursor cursor, String columnPrefixTable) {
        super.updateFromCursor(cursor, columnPrefixTable);
        int index;

        // Title
        index = cursor.getColumnIndex(Tables.Categories.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }

        // Color
        index = cursor.getColumnIndex(Tables.Categories.COLOR.getName(columnPrefixTable));
        if (index >= 0) {
            setColor(cursor.getInt(index));
        }

        // Transaction type
        index = cursor.getColumnIndex(Tables.Categories.TRANSACTION_TYPE.getName(columnPrefixTable));
        if (index >= 0) {
            setTransactionType(TransactionType.fromInt(cursor.getInt(index)));
        }

        // Sort order
        index = cursor.getColumnIndex(Tables.Categories.SORT_ORDER.getName(columnPrefixTable));
        if (index >= 0) {
            setSortOrder(cursor.getInt(index));
        }
    }

    @Override protected Column getLocalIdColumn() {
        return Tables.Categories.LOCAL_ID;
    }

    @Override protected Column getIdColumn() {
        return Tables.Categories.ID;
    }

    @Override protected Column getModelStateColumn() {
        return Tables.Categories.MODEL_STATE;
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Categories.SYNC_STATE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
