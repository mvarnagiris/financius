package com.code44.finance.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.App;
import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.common.model.CategoryOwner;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.utils.IOUtils;

public class Category extends BaseModel<CategoryEntity> {
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public static final long EXPENSE_ID = 1;
    public static final long INCOME_ID = 2;
    public static final long TRANSFER_ID = 3;

    private static Category expenseCategory;
    private static Category incomeCategory;
    private static Category transferCategory;

    private String title;
    private int color;
    private CategoryType categoryType;
    private CategoryOwner categoryOwner;
    private int sortOrder;

    public Category() {
        super();
        setTitle(null);
        setColor(0);
        setCategoryType(CategoryType.EXPENSE);
        setCategoryOwner(CategoryOwner.USER);
        setSortOrder(0);
    }

    public Category(Parcel in) {
        super(in);
    }

    public static Category getExpense() {
        if (expenseCategory == null) {
            final Cursor cursor = Query.create()
                    .projectionLocalId(Tables.Categories.LOCAL_ID)
                    .projection(Tables.Categories.PROJECTION)
                    .selection(Tables.Categories.LOCAL_ID + "=?", String.valueOf(EXPENSE_ID))
                    .from(App.getContext(), CategoriesProvider.uriCategories())
                    .execute();

            expenseCategory = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return expenseCategory;
    }

    public static Category getIncome() {
        if (incomeCategory == null) {
            final Cursor cursor = Query.create()
                    .projectionLocalId(Tables.Categories.LOCAL_ID)
                    .projection(Tables.Categories.PROJECTION)
                    .selection(Tables.Categories.LOCAL_ID + "=?", String.valueOf(INCOME_ID))
                    .from(App.getContext(), CategoriesProvider.uriCategories())
                    .execute();

            incomeCategory = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return incomeCategory;
    }

    public static Category getTransfer() {
        if (transferCategory == null) {
            final Cursor cursor = Query.create()
                    .projectionLocalId(Tables.Categories.LOCAL_ID)
                    .projection(Tables.Categories.PROJECTION)
                    .selection(Tables.Categories.LOCAL_ID + "=?", String.valueOf(TRANSFER_ID))
                    .from(App.getContext(), CategoriesProvider.uriCategories())
                    .execute();

            transferCategory = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return transferCategory;
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
        values.put(Tables.Categories.TITLE.getName(), title);
        values.put(Tables.Categories.COLOR.getName(), color);
        values.put(Tables.Categories.TYPE.getName(), categoryType.asInt());
        values.put(Tables.Categories.OWNER.getName(), categoryOwner.asInt());
        values.put(Tables.Categories.SORT_ORDER.getName(), sortOrder);
    }

    @Override protected void toParcel(Parcel parcel) {
        parcel.writeString(title);
        parcel.writeInt(color);
        parcel.writeInt(categoryType.asInt());
        parcel.writeInt(categoryOwner.asInt());
        parcel.writeInt(sortOrder);
    }

    @Override protected void toEntity(CategoryEntity entity) {
        entity.setTitle(title);
        entity.setColor(color);
        entity.setCategoryType(categoryType.toString());
        entity.setCategoryOwner(categoryOwner.toString());
        entity.setSortOrder(sortOrder);
    }

    @Override protected void fromParcel(Parcel parcel) {
        setTitle(parcel.readString());
        setColor(parcel.readInt());
        setCategoryType(CategoryType.fromInt(parcel.readInt()));
        setCategoryOwner(CategoryOwner.fromInt(parcel.readInt()));
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

        // Type
        index = cursor.getColumnIndex(Tables.Categories.TYPE.getName(columnPrefixTable));
        if (index >= 0) {
            setCategoryType(CategoryType.fromInt(cursor.getInt(index)));
        }

        // Owner
        index = cursor.getColumnIndex(Tables.Categories.OWNER.getName(columnPrefixTable));
        if (index >= 0) {
            setCategoryOwner(CategoryOwner.fromInt(cursor.getInt(index)));
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
        setCategoryType(CategoryType.valueOf(entity.getCategoryType()));
        setCategoryOwner(CategoryOwner.valueOf(entity.getCategoryOwner()));
        setSortOrder(entity.getSortOrder());
    }

    @Override protected CategoryEntity createEntity() {
        return new CategoryEntity();
    }

    @Override public void checkValues() throws IllegalStateException {
        super.checkValues();
        Preconditions.checkNotEmpty(title, "Title cannot be empty");
        Preconditions.checkNotNull(categoryType, "Category type cannot be null.");
        Preconditions.checkNotNull(categoryOwner, "Category owner cannot be null.");
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

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public CategoryOwner getCategoryOwner() {
        return categoryOwner;
    }

    public void setCategoryOwner(CategoryOwner categoryOwner) {
        this.categoryOwner = categoryOwner;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
