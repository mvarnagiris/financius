package com.code44.finance.db.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.db.Column;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.QueryBuilder;

public class Category extends BaseModel {
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
    private Type type;
    private Owner owner;
    private int sortOrder;

    public Category() {
        super();
        setTitle(null);
        setType(Type.EXPENSE);
        setOwner(Owner.USER);
        setSortOrder(0);
    }

    public Category(Parcel in) {
        super(in);
        setTitle(in.readString());
        setType(Type.fromInt(in.readInt()));
        setOwner(Owner.fromInt(in.readInt()));
        setSortOrder(in.readInt());
    }

    public static Category getExpense() {
        if (expenseCategory == null) {
            final ContentResolver contentResolver = App.getAppContext().getContentResolver();
            final Uri uri = CategoriesProvider.uriCategory(EXPENSE_ID);
            final Cursor cursor = QueryBuilder.with(contentResolver, uri)
                    .query();

            expenseCategory = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return expenseCategory;
    }

    public static Category getIncome() {
        if (incomeCategory == null) {
            final ContentResolver contentResolver = App.getAppContext().getContentResolver();
            final Uri uri = CategoriesProvider.uriCategory(INCOME_ID);
            final Cursor cursor = QueryBuilder.with(contentResolver, uri)
                    .query();

            incomeCategory = Category.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return incomeCategory;
    }

    public static Category getTransfer() {
        if (transferCategory == null) {
            final ContentResolver contentResolver = App.getAppContext().getContentResolver();
            final Uri uri = CategoriesProvider.uriCategory(TRANSFER_ID);
            final Cursor cursor = QueryBuilder.with(contentResolver, uri)
                    .query();

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(getTitle());
        dest.writeInt(getType().asInt());
        dest.writeInt(getOwner().asInt());
        dest.writeInt(getSortOrder());
    }

    @Override
    public void checkValues() throws IllegalStateException {
        super.checkValues();

        if (TextUtils.isEmpty(title)) {
            throw new IllegalStateException("Title cannot be empty");
        }

        if (type == null) {
            throw new IllegalStateException("Type cannot be null.");
        }

        if (owner == null) {
            throw new IllegalStateException("Owner cannot be null.");
        }
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Categories.ID;
    }

    @Override
    protected Column getItemStateColumn() {
        return Tables.Categories.ITEM_STATE;
    }

    @Override
    protected Column getSyncStateColumn() {
        return Tables.Categories.SYNC_STATE;
    }

    @Override
    public ContentValues asContentValues() {
        final ContentValues values = super.asContentValues();

        values.put(Tables.Categories.TITLE.getName(), title);
        values.put(Tables.Categories.TYPE.getName(), type.asInt());
        values.put(Tables.Categories.OWNER.getName(), owner.asInt());
        values.put(Tables.Categories.SORT_ORDER.getName(), sortOrder);

        return values;
    }

    @Override
    protected void updateFrom(Cursor cursor, String columnPrefixTable) {
        super.updateFrom(cursor, columnPrefixTable);

        int index;

        index = cursor.getColumnIndex(Tables.Categories.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Tables.Categories.TYPE.getName(columnPrefixTable));
        if (index >= 0) {
            setType(Type.fromInt(cursor.getInt(index)));
        }

        index = cursor.getColumnIndex(Tables.Categories.OWNER.getName(columnPrefixTable));
        if (index >= 0) {
            setOwner(Owner.fromInt(cursor.getInt(index)));
        }

        index = cursor.getColumnIndex(Tables.Categories.SORT_ORDER.getName(columnPrefixTable));
        if (index >= 0) {
            setSortOrder(cursor.getInt(index));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public static enum Type {
        EXPENSE(Type.VALUE_EXPENSE), INCOME(Type.VALUE_INCOME), TRANSFER(Type.VALUE_TRANSFER);

        private static final int VALUE_EXPENSE = 1;
        private static final int VALUE_INCOME = 2;
        private static final int VALUE_TRANSFER = 3;

        private final int value;

        private Type(int value) {
            this.value = value;
        }

        public static Type fromInt(int value) {
            switch (value) {
                case VALUE_EXPENSE:
                    return EXPENSE;

                case VALUE_INCOME:
                    return INCOME;

                case VALUE_TRANSFER:
                    return TRANSFER;

                default:
                    throw new IllegalArgumentException("Value " + value + " is not supported.");
            }
        }

        public int asInt() {
            return value;
        }
    }

    public static enum Owner {
        SYSTEM(Owner.VALUE_SYSTEM), USER(Owner.VALUE_USER);

        private static final int VALUE_SYSTEM = 1;
        private static final int VALUE_USER = 2;

        private final int value;

        private Owner(int value) {
            this.value = value;
        }

        public static Owner fromInt(int value) {
            switch (value) {
                case VALUE_SYSTEM:
                    return SYSTEM;

                case VALUE_USER:
                    return USER;

                default:
                    throw new IllegalArgumentException("Value " + value + " is not supported.");
            }
        }

        public int asInt() {
            return value;
        }
    }
}
