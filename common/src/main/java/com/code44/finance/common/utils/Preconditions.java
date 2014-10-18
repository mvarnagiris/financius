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

    public static <T> T checkNull(T object, String message) throws IllegalStateException {
        if (object != null) {
            throw new IllegalStateException(message);
        }
        return object;
    }

    public static String checkNotEmpty(String str, String message) throws IllegalArgumentException {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    public static String checkLength(String str, int length, String message) throws IllegalArgumentException {
        if (str.length() != length) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    public static int checkBetween(int value, int startInclusive, int endInclusive, String message) throws IllegalArgumentException {
        if (value < startInclusive || value > endInclusive) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static double checkLess(double value, double lessThan, String message) throws IllegalArgumentException {
        if (Double.compare(value, lessThan) < 0) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    public static int checkMore(int value, int moreThan, String message) throws IllegalArgumentException {
        if (value <= moreThan) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    public static boolean checkTrue(boolean value, String message) throws IllegalArgumentException {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
        return true;
    }

    public static boolean checkFalse(boolean value, String message) throws IllegalArgumentException {
        if (value) {
            throw new IllegalArgumentException(message);
        }
        return true;
    }
}
