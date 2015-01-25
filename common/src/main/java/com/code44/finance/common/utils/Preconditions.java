package com.code44.finance.common.utils;

public class Preconditions {
    private Preconditions() {
    }

    public static <T> T notNull(T object, String message) throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static void isNull(Object object, String message) throws IllegalStateException {
        if (object != null) {
            throw new IllegalStateException(message);
        }
    }

    public static String notEmpty(String str, String message) throws IllegalArgumentException {
        if (Strings.isEmpty(str)) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    public static String lengthEquals(String str, int length, String message) throws IllegalArgumentException {
        if (str.length() != length) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    public static int between(int value, int startInclusive, int endInclusive, String message) throws IllegalArgumentException {
        if (value < startInclusive || value > endInclusive) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static double moreOrEquals(double value, double moreOrEqualsTo, String message) throws IllegalArgumentException {
        if (Double.compare(value, moreOrEqualsTo) < 0) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    public static double equals(double value, double equals, String message) throws IllegalArgumentException {
        if (Double.compare(value, equals) != 0) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    public static double moreOrEquals(long value, long moreOrEqualsTo, String message) throws IllegalArgumentException {
        if (value < moreOrEqualsTo) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    public static int more(int value, int moreThan, String message) throws IllegalArgumentException {
        if (value <= moreThan) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    public static double more(double value, double moreThan, String message) throws IllegalArgumentException {
        if (Double.compare(value, moreThan) <= 0) {
            throw new IllegalStateException(message);
        }
        return value;
    }

    public static boolean isTrue(boolean value, String message) throws IllegalArgumentException {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
        return true;
    }

    public static boolean isFalse(boolean value, String message) throws IllegalArgumentException {
        if (value) {
            throw new IllegalArgumentException(message);
        }
        return true;
    }
}
