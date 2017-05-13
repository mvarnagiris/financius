package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Transaction;

public final class CategoryUtils {
    private CategoryUtils() {
    }

    public static int getColor(Context context, Transaction transaction) {
        return getColor(context, transaction.getCategory(), transaction.getTransactionType());
    }

    public static int getColor(Context context, Category category, TransactionType transactionType) {
        if (category == null) {
            switch (transactionType) {
                case Expense:
                    return ThemeUtils.getColor(context, R.attr.textColorNegative);
                case Income:
                    return ThemeUtils.getColor(context, R.attr.textColorPositive);
                case Transfer:
                    return ThemeUtils.getColor(context, R.attr.textColorNeutral);
                default:
                    throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
            }
        } else {
            return category.getColor();
        }
    }

    public static String getTitle(Context context, Transaction transaction) {
        return getTitle(context, transaction.getCategory(), transaction.getTransactionType());
    }

    public static String getTitle(Context context, Category category, TransactionType transactionType) {
        if (category != null) {
            return category.getTitle();
        } else {
            switch (transactionType) {
                case Expense:
                    return context.getString(R.string.expense);
                case Income:
                    return context.getString(R.string.income);
                case Transfer:
                    return context.getString(R.string.transfer);
                default:
                    throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
            }
        }
    }
}
