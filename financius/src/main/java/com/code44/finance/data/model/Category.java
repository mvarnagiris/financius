package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

public class Category extends Model<CategoryEntity> {
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

    public Category(Parcel in) {
        super(in);
    }

    public static Category from(Cursor cursor) {
        final Category category = new Category();
        if (cursor.getCount() > 0) {
            category.updateFrom(cursor, null);
        }
        return category;
    }

    public static Category from(CategoryEntity entity) {
        final Category category = new Category();
        category.updateFrom(entity);
        return category;
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

    @Override protected void toValues(ContentValues values) {
        values.put(Tables.Categories.TRANSACTION_TYPE.getName(), transactionType.asInt());
        values.put(Tables.Categories.TITLE.getName(), title);
        values.put(Tables.Categories.COLOR.getName(), color);
        values.put(Tables.Categories.SORT_ORDER.getName(), sortOrder);
    }

    @Override protected void toParcel(Parcel parcel) {
        parcel.writeString(title);
        parcel.writeInt(color);
        parcel.writeInt(transactionType.asInt());
        parcel.writeInt(sortOrder);
    }

    @Override protected void toEntity(CategoryEntity entity) {
        entity.setTitle(title);
        entity.setColor(color);
        entity.setCategoryType(transactionType.toString());
        entity.setSortOrder(sortOrder);
    }

    @Override protected void fromParcel(Parcel parcel) {
        setTitle(parcel.readString());
        setColor(parcel.readInt());
        setTransactionType(TransactionType.fromInt(parcel.readInt()));
        setSortOrder(parcel.readInt());
    }

    @Override protected void fromCursor(Cursor cursor, String columnPrefixTable) {
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

    @Override protected void fromEntity(CategoryEntity entity) {
        setTitle(entity.getTitle());
        setColor(entity.getColor());
        setTransactionType(TransactionType.valueOf(entity.getCategoryType()));
        setSortOrder(entity.getSortOrder());
    }

    @Override protected CategoryEntity createEntity() {
        return new CategoryEntity();
    }

    @Override public void validate() throws IllegalStateException {
        super.validate();
        Preconditions.notEmpty(title, "Title cannot be empty");
        Preconditions.notNull(transactionType, "Category type cannot be null.");
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
