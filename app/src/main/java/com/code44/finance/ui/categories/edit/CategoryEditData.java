package com.code44.finance.ui.categories.edit;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.utils.ThemeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

class CategoryEditData extends ModelEditActivity.ModelEditData<Category> {
    public static final Parcelable.Creator<CategoryEditData> CREATOR = new Parcelable.Creator<CategoryEditData>() {
        public CategoryEditData createFromParcel(Parcel in) {
            return new CategoryEditData(in);
        }

        public CategoryEditData[] newArray(int size) {
            return new CategoryEditData[size];
        }
    };

    private final int textColorPositive;
    private final int textColorNegative;
    private TransactionType transactionType;
    private Integer color;
    private String title;

    public CategoryEditData(@NonNull Context context) {
        checkNotNull(context, "Context cannot be null.");
        textColorPositive = ThemeUtils.getColor(context, R.attr.textColorPositive);
        textColorNegative = ThemeUtils.getColor(context, R.attr.textColorNegative);
    }

    public CategoryEditData(Parcel in) {
        super(in);
        textColorPositive = in.readInt();
        textColorNegative = in.readInt();
        transactionType = (TransactionType) in.readSerializable();
        color = (Integer) in.readSerializable();
        title = in.readString();
    }

    @Override public Category createModel() {
        final Category category = new Category();
        category.setId(getId());
        category.setTransactionType(getTransactionType());
        category.setColor(getColor());
        category.setTitle(getTitle());
        return category;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(textColorPositive);
        dest.writeInt(textColorNegative);
        dest.writeSerializable(transactionType);
        dest.writeSerializable(color);
        dest.writeString(title);
    }

    public TransactionType getTransactionType() {
        if (transactionType != null) {
            return transactionType;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTransactionType();
        }

        return TransactionType.Expense;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getColor() {
        if (color != null) {
            return color;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getColor();
        }

        return getTransactionType() == TransactionType.Expense ? textColorNegative : textColorPositive;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getTitle() {
        if (title != null) {
            return title;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTitle();
        }

        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
