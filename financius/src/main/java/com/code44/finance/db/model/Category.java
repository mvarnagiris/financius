package com.code44.finance.db.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.providers.CategoriesProvider;

import nl.qbusict.cupboard.CupboardFactory;

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
            expenseCategory = CupboardFactory.cupboard().withContext(App.getAppContext())
                    .query(CategoriesProvider.uriCategory(EXPENSE_ID), Category.class).get();
        }
        return expenseCategory;
    }

    public static Category getIncome() {
        if (incomeCategory == null) {
            incomeCategory = CupboardFactory.cupboard().withContext(App.getAppContext())
                    .query(CategoriesProvider.uriCategory(INCOME_ID), Category.class).get();
        }
        return incomeCategory;
    }

    public static Category getTransfer() {
        if (transferCategory == null) {
            transferCategory = CupboardFactory.cupboard().withContext(App.getAppContext())
                    .query(CategoriesProvider.uriCategory(TRANSFER_ID), Category.class).get();
        }
        return transferCategory;
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
