package com.code44.finance.common.utils;

public final class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
