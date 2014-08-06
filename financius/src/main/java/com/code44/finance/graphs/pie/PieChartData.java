package com.code44.finance.graphs.pie;

import java.util.Collections;
import java.util.List;

public final class PieChartData {
    private final PieChartView.Type type;
    private final List<PieChartValue> values;
    private final float donutWidthRatio;
    private final long totalValue;

    private PieChartData(PieChartView.Type type, List<PieChartValue> values, float donutWidthRatio) {
        this.type = type;
        this.values = values;
        this.donutWidthRatio = donutWidthRatio;

        long total = 0;
        for (PieChartValue value : values) {
            total += value.getValue();
        }
        this.totalValue = total;
    }

    public static Builder builder() {
        return new Builder();
    }

    public PieChartView.Type getType() {
        return type;
    }

    public float getDonutWidthRatio() {
        return donutWidthRatio;
    }

    public List<PieChartValue> getValues() {
        return values;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public static class Builder {
        private PieChartView.Type type;
        private List<PieChartValue> values;
        private float donutWidthRatio;

        public Builder() {
            donutWidthRatio = 0.3f;
        }

        public Builder setType(PieChartView.Type type) {
            this.type = type;
            return this;
        }

        public Builder setDonutWidthRatio(float donutWidthRatio) {
            this.donutWidthRatio = donutWidthRatio;
            return this;
        }

        public Builder setValues(List<PieChartValue> values) {
            this.values = values;
            return this;
        }

        public PieChartData build() {
            ensureSaneDefaults();
            return new PieChartData(type, values, donutWidthRatio);
        }

        private void ensureSaneDefaults() {
            if (type == null) {
                type = PieChartView.Type.DONUT;
            }

            if (values == null) {
                values = Collections.emptyList();
            }
        }

    }
}
