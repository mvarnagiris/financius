package com.code44.finance.graphs.line;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.Collections;
import java.util.List;

public class LineGraphData {
    private final List<LineGraphValue> values;
    private final int color;
    private final float lineWidth;
    private final Drawable dividerDrawable;
    private final boolean isSmooth;
    private final int startIndex;
    private final int endIndex;

    private LineGraphData(List<LineGraphValue> values, int color, float lineWidth, Drawable dividerDrawable, boolean isSmooth, int startIndex, int endIndex) {
        this.values = values;
        this.color = color;
        this.lineWidth = lineWidth;
        this.dividerDrawable = dividerDrawable;
        this.isSmooth = isSmooth;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public List<LineGraphValue> getValues() {
        return values;
    }

    public LineGraphValue getValueForGraph(int index) {
        if (index < startIndex || index > Math.min(endIndex, startIndex + values.size() - 1)) {
            return null;
        }

        return values.get(index - startIndex);
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

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public static class Builder {
        private List<LineGraphValue> values;
        private int color;
        private float lineWidth;
        private Drawable dividerDrawable;
        private boolean isSmooth;
        private int startIndex;
        private int endIndex;

        public Builder() {
            startIndex = -1;
            endIndex = -1;
        }

        public Builder setValues(List<LineGraphValue> values) {
            this.values = values;
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

        public Builder setStartIndex(int startIndex) {
            this.startIndex = startIndex;
            return this;
        }

        public Builder setEndIndex(int endIndex) {
            this.endIndex = endIndex;
            return this;
        }

        public LineGraphData build() {
            ensureSaneDefaults();
            return new LineGraphData(values, color, lineWidth, dividerDrawable, isSmooth, startIndex, endIndex);
        }

        private void ensureSaneDefaults() {
            if (values == null) {
                values = Collections.emptyList();
            }

            if (color == 0) {
                color = Color.BLACK;
            }

            if (lineWidth == 0) {
                lineWidth = 4;
            }

            if (startIndex < 0) {
                startIndex = 0;
            }

            if (endIndex < 0) {
                endIndex = values.size() - 1;
            }
        }
    }
}
