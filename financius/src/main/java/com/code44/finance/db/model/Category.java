package com.code44.finance.db.model;

import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.providers.CategoriesProvider;

import nl.qbusict.cupboard.CupboardFactory;

public class Category extends BaseModel {
    public static final long EXPENSE_ID = 1;
    public static final long INCOME_ID = 2;
    public static final long TRANSFER_ID = 3;

    private static Category expenseCategory;
    private static Category incomeCategory;
    private static Category transferCategory;

    private String title;
    private Type type;
    private Owner owner;
    private int order;

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
    public void useDefaultsIfNotSet() {
        super.useDefaultsIfNotSet();

        if (type == null) {
            setType(Type.EXPENSE);
        }

        if (owner == null) {
            setOwner(Owner.USER);
        }
    }

    @Override
    public void checkRequiredValues() throws IllegalStateException {
        super.checkRequiredValues();

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public static enum Type {
        EXPENSE, INCOME, TRANSFER
    }

    public static enum Owner {
        SYSTEM, USER
    }
}
