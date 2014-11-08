package com.code44.finance.graphs.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineGraphView extends View {
    private final List<LineGraphData> lineGraphDataList = new ArrayList<>();
    private final Map<LineGraphData, LineData> lineDataCache = new HashMap<>();

    private double maxValue;
    private double minValue;

    public LineGraphView(Context context) {
        this(context, null);
    }

    public LineGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateGraphs();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (LineGraphData lineGraphData : lineGraphDataList) {
            // Check if we have something to draw
            final boolean hasItems = lineGraphData.size() > 0;
            final boolean hasLineWidth = Float.compare(lineGraphData.getLineWidth(), 0) > 0;
            final boolean hasDividers = lineGraphData.getDividerDrawable() != null;
            final boolean hasSomethingToDraw = hasItems && (hasLineWidth || hasDividers);
            if (!hasSomethingToDraw) {
                continue;
            }

            final LineData lineData = lineDataCache.get(lineGraphData);

            // Draw line if we have line width
            if (hasLineWidth) {
                canvas.drawPath(lineData.getPath(), lineData.getPaint());
            }

            // Draw dividers if we have them
            if (hasDividers) {
                final Drawable dividerDrawable = lineGraphData.getDividerDrawable();
                final int drawableHalfWidth = dividerDrawable.getIntrinsicWidth() / 2;
                final int drawableHalfHeight = dividerDrawable.getIntrinsicHeight() / 2;

                for (PointF point : lineData.getPoints()) {
                    if (point == null) {
                        continue;
                    }
                    dividerDrawable.setBounds((int) point.x - drawableHalfWidth, (int) point.y - drawableHalfHeight, (int) point.x + drawableHalfWidth, (int) point.y + drawableHalfHeight);
                    dividerDrawable.draw(canvas);
                }
            }
        }
    }

    public void setLineGraphData(LineGraphData... lineGraphData) {
        this.lineGraphDataList.clear();
        addLineGraphData(lineGraphData);
    }

    public void addLineGraphData(LineGraphData... lineGraphData) {
        if (lineGraphData != null && lineGraphData.length > 0) {
            this.lineGraphDataList.addAll(Arrays.asList(lineGraphData));
        }
        invalidateGraphs();
    }

    private void invalidateGraphs() {
        lineDataCache.clear();
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            invalidate();
            return;
        }

        // Create paths.
        GraphPrepareData graphPrepareData = prepareGraphPrepareData(0, 0);
        for (LineGraphData lineGraphData : lineGraphDataList) {
            lineDataCache.put(lineGraphData, prepareGraph(lineGraphData, graphPrepareData));
        }

        // Check if paths are out of bounds
        final RectF bounds = new RectF();
        float topDelta = 0;
        float bottomDelta = 0;
        for (LineData line : lineDataCache.values()) {
            line.getPath().computeBounds(bounds, true);
            final float lineHalfWidth = line.getPaint().getStrokeWidth() / 2;
            final float currentTopDelta = Math.max(0, graphPrepareData.getBounds().top - bounds.top + lineHalfWidth);
            final float currentBottomDelta = Math.max(0, bounds.bottom - graphPrepareData.getBounds().bottom + lineHalfWidth);
            topDelta = Math.max(currentTopDelta, topDelta);
            bottomDelta = Math.max(currentBottomDelta, bottomDelta);
        }
        if (Float.compare(topDelta, 0) > 0 || Float.compare(bottomDelta, 0) > 0) {
            lineDataCache.clear();

            // Create paths
            graphPrepareData = prepareGraphPrepareData(topDelta, bottomDelta);
            for (LineGraphData lineGraphData : lineGraphDataList) {
                lineDataCache.put(lineGraphData, prepareGraph(lineGraphData, graphPrepareData));
            }
        }

        invalidate();
    }

    private GraphPrepareData prepareGraphPrepareData(float extraTopSpace, float extraBottomSpace) {
        final boolean findMinValue = this.minValue == null;
        final boolean findMaxValue = this.maxValue == null;
        final boolean findVisibleSize = this.visibleSize == VISIBLE_SIZE_SHOW_ALL;

        LineGraphValue minValue = this.minValue;
        LineGraphValue maxValue = this.maxValue;
        int visibleSize = this.visibleSize;
        float paddingHorizontal = 0;
        float paddingVertical = 0;

        for (LineGraphData lineGraphData : lineGraphDataList) {
            // Visible size
            if (findVisibleSize) {
                visibleSize = Math.max(lineGraphData.getEndIndex() + 1, visibleSize);
            }

            // MinMax values
            if (findMinValue || findMaxValue) {
                final Pair<LineGraphValue, LineGraphValue> minMaxValues = getMinMaxValues(lineGraphData.getValues());
                if (findMinValue && (minValue == null || (minMaxValues.first != null && Double.compare(minMaxValues.first.getValue(), minValue.getValue()) < 0))) {
                    minValue = minMaxValues.first;
                }

                if (findMaxValue && (maxValue == null || (minMaxValues.second != null && Double.compare(minMaxValues.second.getValue(), maxValue.getValue()) > 0))) {
                    maxValue = minMaxValues.second;
                }
            }

            // Padding for bounds
            final Drawable dividerDrawable = lineGraphData.getDividerDrawable();
            paddingHorizontal = Math.max(paddingHorizontal, Math.max(dividerDrawable != null ? dividerDrawable.getIntrinsicWidth() : 0, lineGraphData.getLineWidth()));
            paddingVertical = Math.max(paddingVertical, Math.max(dividerDrawable != null ? dividerDrawable.getIntrinsicHeight() : 0, lineGraphData.getLineWidth()));
        }
        paddingHorizontal /= 2;
        paddingVertical /= 2;
        if (minValue == null) {
            minValue = new IntLineGraphValue(0);
        }
        if (maxValue == null) {
            maxValue = new IntLineGraphValue(0);
        }
        final RectF bounds = new RectF(paddingHorizontal, paddingVertical + extraTopSpace, getMeasuredWidth() - paddingHorizontal, getMeasuredHeight() - paddingVertical - extraBottomSpace);

        return new GraphPrepareData(visibleSize, minValue, maxValue, bounds);
    }

    private Pair<LineGraphValue, LineGraphValue> getMinMaxValues(List<LineGraphValue> values) {
        LineGraphValue minValue = null;
        LineGraphValue maxValue = null;
        for (LineGraphValue value : values) {
            if (minValue == null || (value != null && Double.compare(value.getValue(), minValue.getValue()) < 0)) {
                minValue = value;
            }

            if (maxValue == null || (value != null && Double.compare(value.getValue(), maxValue.getValue()) > 0)) {
                maxValue = value;
            }
        }
        return Pair.create(minValue, maxValue);
    }

    private LineData prepareGraph(LineGraphData lineGraphData, GraphPrepareData graphPrepareData) {
        final List<PointF> points = getPoints(lineGraphData, graphPrepareData);
        final PathMaker pathMaker = lineGraphData.isSmooth() ? new SmoothPathMaker() : new SharpPathMaker();
        final Path path = pathMaker.makePath(points);
        final Paint paint = createLinePaint(lineGraphData);

        return new LineData(points, path, paint);
    }

    private List<PointF> getPoints(LineGraphData lineGraphData, GraphPrepareData graphPrepareData) {
        final List<PointF> points = new ArrayList<>();
        for (int i = 0, size = graphPrepareData.getVisibleSize(); i < size; i++) {
            final LineGraphValue value = lineGraphData.getValueForGlobalIndex(i);
            if (value == null) {
                points.add(null);
            } else {
                final PointF point = getPoint(i, graphPrepareData, value);
                points.add(point);
            }
        }
        return points;
    }

    private PointF getPoint(int index, GraphPrepareData graphPrepareData, LineGraphValue value) {
        if (value == null) {
            return null;
        }

        if (index >= graphPrepareData.getVisibleSize()) {
            return null;
        }

        final float x;
        if (index == 0) {
            x = graphPrepareData.getBounds().left;
        } else if (index == graphPrepareData.getVisibleSize() - 1) {
            x = graphPrepareData.getBounds().right;
        } else {
            final float step = graphPrepareData.getBounds().width() / (graphPrepareData.getVisibleSize() - 1);
            x = graphPrepareData.getBounds().left + (step * index);
        }

        final double minValue = graphPrepareData.getMinValue().getValue();
        final double maxValue = graphPrepareData.getMaxValue().getValue();
        final float ratio;
        if (Double.compare(minValue, maxValue) == 0) {
            ratio = 0.5f;
        } else {
            ratio = (float) ((value.getValue() - minValue) / (maxValue - minValue));
        }

        final float height = graphPrepareData.getBounds().height();
        final float y = graphPrepareData.getBounds().bottom - (height * ratio);

        return new PointF(x, y);
    }

    private Paint createLinePaint(LineGraphData lineGraphData) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(lineGraphData.getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineGraphData.getLineWidth());
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        return paint;
    }

    private static class LineData {
        private final List<PointF> points;
        private final Path path;
        private final Paint paint;

        private LineData(List<PointF> points, Path path, Paint paint) {
            this.points = points;
            this.path = path;
            this.paint = paint;
        }

        public List<PointF> getPoints() {
            return points;
        }

        public Path getPath() {
            return path;
        }

        public Paint getPaint() {
            return paint;
        }
    }

    private static class GraphPrepareData {
        final int visibleSize;
        final LineGraphValue minValue;
        final LineGraphValue maxValue;
        final RectF bounds;

        private GraphPrepareData(int visibleSize, LineGraphValue minValue, LineGraphValue maxValue, RectF bounds) {
            this.visibleSize = visibleSize;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.bounds = bounds;
        }

        public int getVisibleSize() {
            return visibleSize;
        }

        public LineGraphValue getMinValue() {
            return minValue;
        }

        public LineGraphValue getMaxValue() {
            return maxValue;
        }

        public RectF getBounds() {
            return bounds;
        }
    }
}
