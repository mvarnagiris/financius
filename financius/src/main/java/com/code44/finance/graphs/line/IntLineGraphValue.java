package com.code44.finance.graphs.line;

public class IntLineGraphValue implements LineGraphValue {
    final int value;

    public IntLineGraphValue(int value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public String getFormattedValue() {
        return String.valueOf(value);
    }
}
