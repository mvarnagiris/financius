package com.code44.finance.utils;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.code44.finance.R;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;

import bsh.Interpreter;

public class Calculator {
    private static final String DECIMAL = ".";
    private static final String PLUS = "+";
    private static final String MINUS = "-";
    private static final String DIVIDE = "/";
    private static final String MULTIPLY = "*";

    private final Context context;
    private final Interpreter interpreter;
    private final LinkedList<Part> parts;

    private Part currentPart;

    public Calculator(Context context) {
        this.context = context;
        this.interpreter = new Interpreter();
        this.parts = new LinkedList<>();
        clear();
    }

    public void setValue(double value) {
        clear();
        currentPart = createPart(Type.NUMBER, String.valueOf(value), null);
    }

    public void plus() {
        addOperator(PLUS, context.getString(R.string.plus));
    }

    public void minus() {
        addOperator(MINUS, context.getString(R.string.minus));
    }

    public void multiply() {
        addOperator(MULTIPLY, context.getString(R.string.multiply));
    }

    public void divide() {
        addOperator(DIVIDE, context.getString(R.string.divide));
    }

    public void decimal() {
        doAction(Type.DECIMAL, DECIMAL, DECIMAL);
    }

    public void number(int number) {
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("Number must be [0, 9]");
        }

        doAction(Type.NUMBER, String.valueOf(number), null);
    }

    public void calculate() {
        String result = "";
        try {
            result = interpreter.eval(getExpression()).toString();
            final Double number = Double.parseDouble(result);
            final NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
            format.setGroupingUsed(false);
            result = Double.isInfinite(number) ? null : format.format(number);
        } catch (Exception e) {
            e.printStackTrace();
        }

        clear();
        currentPart = new NumberPart(result);
    }

    public long getResult() {
        calculate();
        long result = 0;
        try {
            final Double number = Double.parseDouble(currentPart.toString());
            result = Math.round(number * 100);
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
        currentPart = createPart(Type.NUMBER, null, null);
    }

    private void addOperator(String value, String formattedValue) {
        doAction(Type.OPERATOR, value, formattedValue);
    }

    private void doAction(Type type, String value, String formattedValue) {
        final Part.Action action = currentPart.getAction(type, parts.size(), value);
        switch (action) {
            case NEW:
                parts.add(currentPart);
                currentPart = createPart(type, value, formattedValue);
                break;

            case OVERWRITE:
                currentPart = createPart(type, value, formattedValue);
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

    private Part createPart(Type type, String value, String formattedString) {
        switch (type) {
            case NUMBER:
                return new NumberPart(value);

            case OPERATOR:
                return new OperatorPart(value, formattedString);

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

        public int length() {
            return stringBuilder.length();
        }

        public static enum Action {
            IGNORE, APPEND, OVERWRITE, NEW
        }
    }

    private static class OperatorPart extends Part {
        final String formattedString;

        private OperatorPart(String operator, String formattedString) {
            super(operator);
            this.formattedString = formattedString;
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
            return formattedString;
        }
    }

    private static class NumberPart extends Part {
        private static final int MAX_DECIMALS = 10;

        private NumberPart(String number) {
            super(cleanupDecimals(ensureMaxDecimals(number)));
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

        private static String cleanupDecimals(String number) {
            if (TextUtils.isEmpty(number)) {
                return number;
            }

            final int decimalPosition = number.indexOf(DECIMAL);
            if (decimalPosition < 0) {
                return number;
            }

            int index = number.length() - 1;
            while (index >= decimalPosition && (number.charAt(index) == '0' || number.charAt(index) == '.')) {
                number = number.substring(0, index);
                index--;
            }

            return number;
        }

        @Override
        public Action getAction(Type type, int partIndex, String value) {
            switch (type) {
                case OPERATOR:
                    if (partIndex == 0 && stringBuilder.length() == 0 && MINUS.equals(value)) {
                        return Action.APPEND;
                    } else if ((partIndex == 0 && stringBuilder.length() == 0) || (length() == 1 && MINUS.equals(stringBuilder.toString()))) {
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
                    if (length() == 0 && value.equals("0")) {
                        return Action.IGNORE;
                    } else {
                        return Action.APPEND;
                    }

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
                return super.toString() + ".0";
            }
        }

        private boolean containsDecimal() {
            return stringBuilder.toString().contains(DECIMAL);
        }
    }
}
