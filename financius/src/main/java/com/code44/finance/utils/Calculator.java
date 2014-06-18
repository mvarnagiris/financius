package com.code44.finance.utils;

import bsh.EvalError;
import bsh.Interpreter;

public class Calculator {
    private final StringBuilder expressionBuilder;
    private final Interpreter interpreter;

    public Calculator() {
        this.expressionBuilder = new StringBuilder();
        this.interpreter = new Interpreter();
    }

    public void append(String value) {
        expressionBuilder.append(value);
    }

    public void delete() {
        if (expressionBuilder.length() > 0) {
            expressionBuilder.deleteCharAt(expressionBuilder.length() - 1);
        }
    }

    public void clear() {
        expressionBuilder.delete(0, expressionBuilder.length());
    }

    public void calculate() {
        String result = "";
        try {
            result = interpreter.eval(expressionBuilder.toString()).toString();
        } catch (EvalError evalError) {
            evalError.printStackTrace();
        }

        clear();
        expressionBuilder.append(result);
    }

    public String getExpression() {
        return expressionBuilder.toString();
    }

    public static enum Value {
        DIVIDE;
    }
}
