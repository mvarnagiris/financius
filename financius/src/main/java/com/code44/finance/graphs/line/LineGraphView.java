package com.code44.finance.graphs.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineGraphView extends View {
    public static final int VISIBLE_SIZE_SHOW_ALL = 0;

    private final List<LineGraphData> lineGraphDataList;
    private final Map<LineGraphData, Path> graphPaths;
    private LineGraphData.LineGraphValue minValue;
    private LineGraphData.LineGraphValue maxValue;
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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        prepareGraphs();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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

        final boolean findVisibleSize = this.visibleSize == VISIBLE_SIZE_SHOW_ALL;
        int visibleSize = this.visibleSize;
        float paddingHorizontal = 0;
        float paddingVertical = 0;
        for (LineGraphData lineGraphData : lineGraphDataList) {
            if (findVisibleSize) {
                visibleSize = Math.max(lineGraphData.getEndIndex() + 1, visibleSize);
            }
            final Drawable dividerDrawable = lineGraphData.getDividerDrawable();
            paddingHorizontal = Math.max(paddingHorizontal, Math.max(dividerDrawable.getIntrinsicWidth(), lineGraphData.getLineWidth()));
            paddingVertical = Math.max(paddingVertical, Math.max(dividerDrawable.getIntrinsicHeight(), lineGraphData.getLineWidth()));

        }
        paddingHorizontal /= 2;
        paddingVertical /= 2;
        final RectF bounds = new RectF(paddingHorizontal, paddingVertical, getMeasuredWidth() - paddingHorizontal, getMeasuredHeight() - paddingVertical);

        for (LineGraphData lineGraphData : lineGraphDataList) {
            graphPaths.put(lineGraphData, prepareGraph(lineGraphData, visibleSize, bounds));
        }

        invalidate();
    }

    private Path prepareGraph(LineGraphData lineGraphData, int visibleSize, RectF bounds) {
        if (lineGraphData.isSmooth()) {
            return prepareGraphSmooth(lineGraphData, visibleSize);
        } else {
            return prepareGraphSharp(lineGraphData, visibleSize);
        }
    }

    private Path prepareGraphSharp(LineGraphData lineGraphData, int visibleSize) {
        final Path path = new Path();

        final int start = lineGraphData.getStartIndex();
        final int end = Math.min(visibleSize, lineGraphData.getEndIndex());
        for (int i = startIndex, end = ; i < end; i++) {
        }

        return path;
    }

    private Path prepareGraphSmooth(LineGraphData lineGraphData, int visibleSize) {
        final Path path = new Path();
        return path;
    }

    private PointF getPoint(int index, int size, LineGraphData.LineGraphValue minValue, LineGraphData.LineGraphValue maxValue, RectF bounds, LineGraphData.LineGraphValue value) {
        // TODO Implement
        return null;
    }

    private static class LineData {
        private final List<PointF> points;
        private final Path path;

        private LineData(List<PointF> points, Path path) {
            this.points = points;
            this.path = path;
        }
    }
}
