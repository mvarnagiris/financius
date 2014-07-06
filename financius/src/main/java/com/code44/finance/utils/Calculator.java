package com.code44.finance.utils;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import java.util.LinkedList;

import bsh.Interpreter;

public class Calculator {
    private static final String DECIMAL = ".";
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String DIVIDE = "/";
    private static final String MULTIPLY = "*";

    private final Interpreter interpreter;
    private final LinkedList<Part> parts;

    private Part currentPart;

    public Calculator() {
        this.interpreter = new Interpreter();
        this.parts = new LinkedList<>();
        clear();
    }

    public void plus() {
        addOperator(PLUS);
    }

    public void minus() {
        addOperator(MINUS);
    }

    public void multiply() {
        addOperator(MULTIPLY);
    }

    public void divide() {
        addOperator(DIVIDE);
    }

    public void decimal() {
        doAction(Type.DECIMAL, DECIMAL);
    }

    public void number(int number) {
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("Number must be [0, 9]");
        }

        doAction(Type.NUMBER, String.valueOf(number));
    }

    public void calculate() {
        String result = "";
        try {
            result = interpreter.eval(getExpression()).toString();
            Double number = Double.parseDouble(result);
            result = Double.isInfinite(number) ? null : String.valueOf(number);
        } catch (Exception ignore) {
        }

        clear();
        currentPart = new NumberPart(result);
    }

    public long getResult() {
        calculate();
        long result = 0;
        try {
            final Double number = Double.parseDouble(currentPart.toString());
            result = (long) (number * 100);
        } catch (Exception ignore) {
        }

        return result;
    }

    public boolean hasExpression() {
        return parts.size() > 0;
    }

    public String getExpression() {
        final StringBuilder sb = new StringBuilder();
        for (Part part : parts) {
            sb.append(part.toString());
        }
        sb.append(currentPart.toString());
        return sb.toString();
    }

    public CharSequence getFormattedExpression() {
        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (Part part : parts) {
            ssb.append(part.toFormattedString());
        }
        ssb.append(currentPart.toFormattedString());
        return ssb;
    }

    public void delete() {
        if (currentPart.delete()) {
            // Should remove this part
            if (parts.size() == 0) {
                clear();
            } else {
                currentPart = parts.removeLast();
            }
        }
    }

    public void clear() {
        parts.clear();
        currentPart = createPart(Type.NUMBER, null);
    }

    private void addOperator(String value) {
        doAction(Type.OPERATOR, value);
    }

    private void doAction(Type type, String value) {
        final Part.Action action = currentPart.getAction(type, parts.size(), value);
        switch (action) {
            case NEW:
                parts.add(currentPart);
                currentPart = createPart(type, value);
                break;

            case OVERWRITE:
                currentPart = createPart(type, value);
                break;

            case APPEND:
                currentPart.append(value);
                break;

            case IGNORE:
            default:
                // Ignore
                break;
        }
    }

    private Part createPart(Type type, String value) {
        switch (type) {
            case NUMBER:
                return new NumberPart(value);

            case OPERATOR:
                return new OperatorPart(value);

            default:
                throw new IllegalArgumentException("Cannot create part for type " + type);
        }
    }

    private static enum Type {
        OPERATOR, DECIMAL, NUMBER
    }

    private static abstract class Part {
        protected final StringBuilder stringBuilder;

        protected Part(String initialValue) {
            this.stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(initialValue)) {
                stringBuilder.append(initialValue);
            }
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }

        public abstract Action getAction(Type type, int partIndex, String value);

        public abstract CharSequence toFormattedString();

        public void append(String value) {
            if (!TextUtils.isEmpty(value)) {
                stringBuilder.append(value);
            }
        }

        public boolean delete() {
            if (stringBuilder.length() == 0) {
                return true;
            } else {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                return stringBuilder.length() == 0;
            }
        }

        public static enum Action {
            IGNORE, APPEND, OVERWRITE, NEW
        }
    }

    private static class OperatorPart extends Part {
        private OperatorPart(String operator) {
            super(operator);
        }

        @Override
        public Action getAction(Type type, int partIndex, String value) {
            switch (type) {
                case OPERATOR:
                    return Action.OVERWRITE;

                case DECIMAL:
                case NUMBER:
                    return Action.NEW;

                default:
                    throw new IllegalArgumentException("Type " + type + " is not supported.");
            }
        }

        @Override
        public CharSequence toFormattedString() {
            return toString();
        }
    }

    private static class NumberPart extends Part {
        private static final int MAX_DECIMALS = 10;

        private NumberPart(String number) {
            super(ensureMaxDecimals(number));
        }

        private static String ensureMaxDecimals(String number) {
            if (TextUtils.isEmpty(number)) {
                // No number
                return number;
            }

            final int decimalPosition = number.indexOf(DECIMAL);
            if (decimalPosition < 0) {
                // No decimal
                return number;
            }

            if (number.length() - decimalPosition <= MAX_DECIMALS) {
                // Not too many decimals
                return number;
            }

            return number.substring(0, decimalPosition + MAX_DECIMALS);
        }

        @Override
        public Action getAction(Type type, int partIndex, String value) {
            switch (type) {
                case OPERATOR:
                    if (partIndex == 0 && stringBuilder.length() == 0 && MINUS.equals(value)) {
                        return Action.APPEND;
                    } else if (partIndex == 0 && stringBuilder.length() == 0) {
                        return Action.IGNORE;
                    } else {
                        return Action.NEW;
                    }

                case DECIMAL:
                    if (containsDecimal()) {
                        return Action.IGNORE;
                    } else {
                        return Action.APPEND;
                    }

                case NUMBER:
                    return Action.APPEND;

                default:
                    throw new IllegalArgumentException("Type " + type + " is not supported.");
            }
        }

        @Override
        public CharSequence toFormattedString() {
            if (stringBuilder.length() == 0) {
                return "0";
            } else if (stringBuilder.charAt(0) == DECIMAL.charAt(0)) {
                return "0" + super.toString();
            } else {
                return stringBuilder.toString();
            }
        }

        @Override
        public void append(String value) {
            final String number = stringBuilder.toString();
            final int decimalPosition = number.indexOf(DECIMAL);
            if (decimalPosition < 0 || number.length() - decimalPosition <= MAX_DECIMALS) {
                super.append(value);
            }
        }

        @Override
        public String toString() {
            if (containsDecimal()) {
                return super.toString();
            } else {
                return super.toString() + ".";
            }
        }

        private boolean containsDecimal() {
            return stringBuilder.toString().contains(DECIMAL);
        }
    }
}
