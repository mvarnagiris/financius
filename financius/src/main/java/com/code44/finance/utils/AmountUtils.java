package com.code44.finance.utils;

import com.code44.finance.db.model.Transaction;

public class AmountUtils {
    public static String format(Transaction transaction) {
        return String.valueOf(transaction.getAmount());
    }
}
