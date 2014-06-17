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

    public long getResult() {
        double result = 0;
        try {
            result = (double) interpreter.eval(expressionBuilder.toString());
        } catch (EvalError evalError) {
            evalError.printStackTrace();
        }

        return (long) (result * 100);
    }
}
