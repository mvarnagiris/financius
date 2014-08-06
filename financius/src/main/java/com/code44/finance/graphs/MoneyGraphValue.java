package com.code44.finance.graphs;

public class MoneyGraphValue implements GraphValue {
    private final long value;

    public MoneyGraphValue(long value) {
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
