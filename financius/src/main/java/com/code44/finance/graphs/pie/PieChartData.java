package com.code44.finance.graphs.pie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PieChartData {
    private final List<PieChartValue> values;
    private final long totalValue;

    private PieChartData(List<PieChartValue> values) {
        this.values = values;

        long total = 0;
        for (PieChartValue value : values) {
            total += value.getValue();
        }
        this.totalValue = total;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<PieChartValue> getValues() {
        return values;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public static class Builder {
        private List<PieChartValue> values;

        public Builder() {
        }

        public Builder setValues(List<PieChartValue> values) {
            this.values = values;
            return this;
        }

        public Builder addValues(PieChartValue value) {
            if (values == null) {
                values = new ArrayList<>();
            }

            values.add(value);
            return this;
        }

        public PieChartData build() {
            ensureSaneDefaults();
            return new PieChartData(values);
        }

        private void ensureSaneDefaults() {
            if (values == null) {
                values = Collections.emptyList();
            }
        }

    }
}
