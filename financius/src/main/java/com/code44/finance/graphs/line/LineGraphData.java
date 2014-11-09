package com.code44.finance.graphs.line;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineGraphData {
    private final List<LineGraphValue> values;
    private final double maxValue;
    private final double minValue;
    private final int color;
    private final float lineWidth;
    private final Drawable dividerDrawable;
    private final boolean isSmooth;
    private final boolean useGlobalMinMax;

    private LineGraphData(List<LineGraphValue> values, double maxValue, double minValue, int color, float lineWidth, Drawable dividerDrawable, boolean isSmooth, boolean useGlobalMinMax) {
        this.values = values;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.color = color;
        this.lineWidth = lineWidth;
        this.dividerDrawable = dividerDrawable;
        this.isSmooth = isSmooth;
        this.useGlobalMinMax = useGlobalMinMax;
    }

    public List<LineGraphValue> getValues() {
        return values;
    }

    public LineGraphValue getValue(int position) {
        return values.get(position);
    }

    public int size() {
        return values.size();
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public int getColor() {
        return color;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public Drawable getDividerDrawable() {
        return dividerDrawable;
    }

    public boolean isSmooth() {
        return isSmooth;
    }

    public boolean isUsingGlobalMinMax() {
        return useGlobalMinMax;
    }

    public static class Builder {
        private List<LineGraphValue> values;
        private double maxValue;
        private double minValue;
        private int color;
        private float lineWidth;
        private Drawable dividerDrawable;
        private boolean isSmooth = true;
        private boolean useGlobalMinMax = true;

        public Builder setValues(List<LineGraphValue> values) {
            this.values = values;
            return this;
        }

        public Builder addValues(List<LineGraphValue> values) {
            if (values == null) {
                return this;
            }

            if (this.values == null) {
                this.values = new ArrayList<>();
            }

            this.values.addAll(values);
            return this;
        }

        public Builder addValue(LineGraphValue value) {
            if (this.values == null) {
                this.values = new ArrayList<>();
            }

            this.values.add(value);
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setLineWidth(float lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        public Builder setDividerDrawable(Drawable dividerDrawable) {
            this.dividerDrawable = dividerDrawable;
            return this;
        }

        public Builder setSmooth(boolean isSmooth) {
            this.isSmooth = isSmooth;
            return this;
        }

        public Builder setUseGlobalMinMax(boolean useGlobalMinMax) {
            this.useGlobalMinMax = useGlobalMinMax;
            return this;
        }

        public LineGraphData build() {
            ensureSaneDefaults();
            return new LineGraphData(values, maxValue, minValue, color, lineWidth, dividerDrawable, isSmooth, useGlobalMinMax);
        }

        private void ensureSaneDefaults() {
            if (values == null) {
                values = Collections.emptyList();
            }
            findMinMax();

            if (color == 0) {
                color = Color.BLACK;
            }
        }

        private void findMinMax() {
            if (values.size() > 0) {
                minValue = Double.MAX_VALUE;
                maxValue = Double.MIN_VALUE;
                for (LineGraphValue value : values) {
                    if (value == null) {
                        continue;
                    }

                    if (Double.compare(value.getValue(), minValue) < 0) {
                        minValue = value.getValue();
                    }

                    if (Double.compare(value.getValue(), maxValue) > 0) {
                        maxValue = value.getValue();
                    }
                }
            } else {
                minValue = 0;
                maxValue = 0;
            }
        }
    }
}
