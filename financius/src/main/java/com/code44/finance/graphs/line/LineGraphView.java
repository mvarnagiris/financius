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

import com.code44.finance.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LineGraphView extends View {
    public static final int VISIBLE_SIZE_SHOW_ALL = 0;

    private final List<LineGraphData> lineGraphDataList;
    private final Map<LineGraphData, LineData> graphPaths;
    private LineGraphValue minValue;
    private LineGraphValue maxValue;
    private int visibleSize;

    public LineGraphView(Context context) {
        this(context, null);
    }

    public LineGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Init
        lineGraphDataList = new ArrayList<>();
        graphPaths = new HashMap<>();
        visibleSize = VISIBLE_SIZE_SHOW_ALL;

        // Edit mode
        //if (isInEditMode()) {
        final List<LineGraphValue> values = new ArrayList<>();
        final Random random = new Random();
        int startingValue = random.nextInt(100);
        for (int i = 0; i < 15; i++) {
            startingValue = startingValue + (random.nextInt(5) * (random.nextBoolean() ? -1 : 1));
            values.add(new IntLineGraphValue(startingValue));
        }
        final LineGraphData lineGraphData = new LineGraphData.Builder()
                .setColor(0xff0099cc)
                .setValues(values)
                .setLineWidth(context.getResources().getDimension(R.dimen.space_small))
                .setSmooth(true)
                .build();
        setLineGraphData(lineGraphData);
        //}
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        prepareGraphs();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (LineGraphData lineGraphData : lineGraphDataList) {
            final LineData lineData = graphPaths.get(lineGraphData);
            canvas.drawPath(lineData.getPath(), lineData.getPaint());
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
        prepareGraphs();
    }

    public void setVisibleSize(int visibleSize) {
        this.visibleSize = visibleSize;
        prepareGraphs();
    }

    private void prepareGraphs() {
        graphPaths.clear();
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0) {
            invalidate();
            return;
        }

        final GraphPrepareData graphPrepareData = prepareGraphPrepareData();
        for (LineGraphData lineGraphData : lineGraphDataList) {
            graphPaths.put(lineGraphData, prepareGraph(lineGraphData, graphPrepareData));
        }

        invalidate();
    }

    private GraphPrepareData prepareGraphPrepareData() {
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
        final RectF bounds = new RectF(paddingHorizontal, paddingVertical, getMeasuredWidth() - paddingHorizontal, getMeasuredHeight() - paddingVertical);

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
        if (lineGraphData.isSmooth()) {
            return prepareGraphSmooth(lineGraphData, graphPrepareData);
        } else {
            return prepareGraphSharp(lineGraphData, graphPrepareData);
        }
    }

    private LineData prepareGraphSharp(LineGraphData lineGraphData, GraphPrepareData graphPrepareData) {
        final List<PointF> points = new ArrayList<>();
        final Path path = new Path();

        for (int i = 0, size = graphPrepareData.getVisibleSize(); i < size; i++) {
            final LineGraphValue value = lineGraphData.getValueForGraph(i);
            if (value == null) {
                points.add(null);
            } else {
                final boolean shouldMove = points.size() == 0 || points.get(i - 1) == null;
                final PointF point = getPoint(i, graphPrepareData, value);
                points.add(point);

                if (point != null) {
                    if (shouldMove) {
                        path.moveTo(point.x, point.y);
                    } else {
                        path.lineTo(point.x, point.y);
                    }
                }
            }
        }

        return new LineData(points, path, createLinePaint(lineGraphData));
    }

    private LineData prepareGraphSmooth(LineGraphData lineGraphData, GraphPrepareData graphPrepareData) {
        final List<PointF> points = new ArrayList<>();
        final Path path = new Path();

        for (int i = 0, size = graphPrepareData.getVisibleSize(); i < size; i++) {
            final LineGraphValue value = lineGraphData.getValueForGraph(i);
            if (value == null) {
                points.add(null);
            } else {
                final PointF previousPoint = points.size() == 0 ? null : points.get(i - 1);
                final boolean shouldMove = previousPoint == null;
                final PointF point = getPoint(i, graphPrepareData, value);
                points.add(point);

                if (point != null) {
                    if (shouldMove) {
                        path.moveTo(point.x, point.y);
                    } else {
                        final PointF nextPoint = getPoint(i + 1, graphPrepareData, lineGraphData.getValueForGraph(i + 1));
                        final PointF deltaPoint = new PointF();

                        final PointF firstPoint = nextPoint != null ? nextPoint : point;
                        deltaPoint.x = ((firstPoint.x - previousPoint.x) / 3);
                        deltaPoint.y = ((firstPoint.y - previousPoint.y) / 3);
                        path.cubicTo(previousPoint.x + deltaPoint.x, previousPoint.y + deltaPoint.y, point.x - deltaPoint.x, point.y - deltaPoint.y, point.x, point.y);
                    }
                }
            }
        }

        return new LineData(points, path, createLinePaint(lineGraphData));
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
