package com.code44.finance.common.utils;

public class Preconditions {
    private Preconditions() {
    }

    public static <T> T checkNotNull(T object, String message) throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static String checkNotEmpty(String str, String message) throws IllegalArgumentException {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }
}
