package com.code44.finance.graphs.pie;

public class PieChartValue {
    private final long value;
    private final int color;

    public PieChartValue(long value, int color) {
        this.value = value;
        this.color = color;
    }

    public long getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }
}
