package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.data.model.Transaction;

public final class CategoryUtils {
    private CategoryUtils() {
    }

    public static int getColor(Context context, Transaction transaction) {
        if (transaction.getCategory() == null) {
            switch (transaction.getTransactionType()) {
                case Expense:
                    return ThemeUtils.getColor(context, R.attr.textColorNegative);
                case Income:
                    return ThemeUtils.getColor(context, R.attr.textColorPositive);
                case Transfer:
                    return ThemeUtils.getColor(context, R.attr.textColorNeutral);
                default:
                    throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
            }
        } else {
            return transaction.getCategory().getColor();
        }
    }

    public static String getTitle(Context context, Transaction transaction) {
        if (transaction.getCategory() != null) {
            return transaction.getCategory().getTitle();
        } else {
            switch (transaction.getTransactionType()) {
                case Expense:
                    return context.getString(R.string.expense);
                case Income:
                    return context.getString(R.string.income);
                case Transfer:
                    return context.getString(R.string.transfer);
                default:
                    throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
            }
        }
    }
}
