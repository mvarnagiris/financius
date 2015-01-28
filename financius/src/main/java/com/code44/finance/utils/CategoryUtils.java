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
}
