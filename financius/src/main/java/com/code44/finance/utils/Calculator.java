package com.code44.finance.utils;

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
        final Part.Action action = currentPart.getAction(Type.DECIMAL);
        switch (action) {
            case APPEND:
                currentPart.append(DECIMAL);
                break;

            case OVERWRITE:
            case IGNORE:
            case NEW:
            default:
                // Ignore
                break;
        }
    }

    public void number(int number) {
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("Number must be [0, 9]");
        }

        final Part.Action action = currentPart.getAction(Type.NUMBER);
        switch (action) {
            case APPEND:
                currentPart.append(DECIMAL);
                break;

            case OVERWRITE:
            case IGNORE:
            case NEW:
            default:
                // Ignore
                break;
        }
    }

    public long calculate() {
//        String result = "";
//        try {
//            result = interpreter.eval(expressionBuilder.toString()).toString();
//        } catch (EvalError evalError) {
//            evalError.printStackTrace();
//        }
//
//        clear();
//        expressionBuilder.append(result);
        return 0;
    }

    public boolean hasExpression() {
        return parts.size() > 0;
    }

    public void delete() {
        // TODO
    }

    public void clear() {
        // TODO
    }

    private void addOperator(String value) {
        doAction(Type.OPERATOR, value);
    }

    private void doAction(Type type, String value) {
        final Part.Action action = currentPart.getAction(type);
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

        public abstract Action getAction(Type type);

        public void append(String value) {
            if (!TextUtils.isEmpty(value)) {
                stringBuilder.append(value);
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
        public Action getAction(Type type) {
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
    }

    private static class NumberPart extends Part {
        private NumberPart(String number) {
            super(number);
        }

        @Override
        public Action getAction(Type type) {
            switch (type) {
                case OPERATOR:
                    return Action.NEW;

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

        private boolean containsDecimal() {
            return stringBuilder.toString().contains(DECIMAL);
        }
    }
}
