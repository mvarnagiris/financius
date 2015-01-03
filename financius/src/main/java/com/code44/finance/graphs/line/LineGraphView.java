package com.code44.finance.graphs.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        if (isInEditMode()) {
            final LineGraphData.Builder builder = new LineGraphData.Builder()
                    .setColor(ThemeUtils.getColor(getContext(), R.attr.textColorNegative))
                    .setLineWidth(getResources().getDimension(R.dimen.divider))
                    .setSmooth(true)
                    .setUseGlobalMinMax(true);

            final Random random = new Random();
            for (int i = 0; i < 30; i++) {
                builder.addValue(new LineGraphValue(random.nextFloat() * 1000));
            }
            setLineGraphData(builder.build());
        }
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

        invalidateMinMax();
        final GraphsInfo graphsInfo = invalidateLineDataCache(0, 0);

        // Check if paths are out of bounds
        final float[] topBottomDelta = getTopBottomOffsetToFitGraphs(graphsInfo);
        final float topDelta = topBottomDelta[0];
        final float bottomDelta = topBottomDelta[1];
        if (Float.compare(topDelta, 0) > 0 || Float.compare(bottomDelta, 0) > 0) {
            lineDataCache.clear();
            invalidateLineDataCache(topDelta, bottomDelta);
        }

        invalidate();
    }

    private void invalidateMinMax() {
        minValue = Double.MAX_VALUE;
        maxValue = Double.MIN_VALUE;
        for (LineGraphData lineGraphData : lineGraphDataList) {
            if (!lineGraphData.isUsingGlobalMinMax()) {
                continue;
            }

            if (Double.compare(lineGraphData.getMinValue(), minValue) < 0) {
                minValue = lineGraphData.getMinValue();
            }

            if (Double.compare(lineGraphData.getMaxValue(), maxValue) > 0) {
                maxValue = lineGraphData.getMaxValue();
            }
        }
    }

    private GraphsInfo invalidateLineDataCache(float topDelta, float bottomDelta) {
        final GraphsInfo graphsInfo = getGraphsInfo(topDelta, bottomDelta);
        for (LineGraphData lineGraphData : lineGraphDataList) {
            lineDataCache.put(lineGraphData, getLineData(lineGraphData, graphsInfo));
        }
        return graphsInfo;
    }

    private float[] getTopBottomOffsetToFitGraphs(GraphsInfo graphsInfo) {
        final RectF bounds = new RectF();
        float topDelta = 0;
        float bottomDelta = 0;
        for (LineData line : lineDataCache.values()) {
            line.getPath().computeBounds(bounds, true);
            final float lineHalfWidth = line.getPaint().getStrokeWidth() / 2;
            final float currentTopDelta = Math.max(0, graphsInfo.getBounds().top - bounds.top + lineHalfWidth);
            final float currentBottomDelta = Math.max(0, bounds.bottom - graphsInfo.getBounds().bottom + lineHalfWidth);
            topDelta = Math.max(currentTopDelta, topDelta);
            bottomDelta = Math.max(currentBottomDelta, bottomDelta);
        }

        return new float[]{topDelta, bottomDelta};
    }

    private GraphsInfo getGraphsInfo(float extraTopSpace, float extraBottomSpace) {
        float paddingHorizontal = 0;
        float paddingVertical = 0;

        for (LineGraphData lineGraphData : lineGraphDataList) {
            final Drawable dividerDrawable = lineGraphData.getDividerDrawable();
            paddingHorizontal = Math.max(paddingHorizontal, Math.max(dividerDrawable != null ? dividerDrawable.getIntrinsicWidth() : 0, lineGraphData.getLineWidth()));
            paddingVertical = Math.max(paddingVertical, Math.max(dividerDrawable != null ? dividerDrawable.getIntrinsicHeight() : 0, lineGraphData.getLineWidth()));
        }
        paddingHorizontal /= 2;
        paddingVertical /= 2;
        final RectF bounds = new RectF(paddingHorizontal, paddingVertical + extraTopSpace, getMeasuredWidth() - paddingHorizontal, getMeasuredHeight() - paddingVertical - extraBottomSpace);
        return new GraphsInfo(bounds);
    }

    private LineData getLineData(LineGraphData lineGraphData, GraphsInfo graphsInfo) {
        final List<PointF> points = getPoints(lineGraphData, graphsInfo);
        final PathMaker pathMaker = lineGraphData.isSmooth() ? new SmoothPathMaker() : new SharpPathMaker();
        final Path path = pathMaker.makePath(points);
        final Paint paint = createLinePaint(lineGraphData);

        return new LineData(points, path, paint);
    }

    private List<PointF> getPoints(LineGraphData lineGraphData, GraphsInfo graphsInfo) {
        final List<PointF> points = new ArrayList<>();
        for (int i = 0, size = lineGraphData.size(); i < size; i++) {
            final LineGraphValue value = lineGraphData.getValue(i);
            if (value == null) {
                points.add(null);
            } else {
                final PointF point = getPoint(i, lineGraphData, graphsInfo, value);
                points.add(point);
            }
        }
        return points;
    }

    private PointF getPoint(int index, LineGraphData lineGraphData, GraphsInfo graphsInfo, LineGraphValue value) {
        if (value == null) {
            return null;
        }

        final float x;
        if (index == 0) {
            x = graphsInfo.getBounds().left;
        } else if (index == lineGraphData.size() - 1) {
            x = graphsInfo.getBounds().right;
        } else {
            final float step = graphsInfo.getBounds().width() / (lineGraphData.size() - 1);
            x = graphsInfo.getBounds().left + (step * index);
        }

        final double minValue;
        final double maxValue;
        if (lineGraphData.isUsingGlobalMinMax()) {
            minValue = this.minValue;
            maxValue = this.maxValue;
        } else {
            minValue = lineGraphData.getMinValue();
            maxValue = lineGraphData.getMaxValue();
        }

        final float ratio;
        if (Double.compare(minValue, maxValue) == 0) {
            ratio = 0.5f;
        } else {
            ratio = (float) ((value.getValue() - minValue) / (maxValue - minValue));
        }

        final float height = graphsInfo.getBounds().height();
        final float y = graphsInfo.getBounds().bottom - (height * ratio);

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

    private static class GraphsInfo {
        final RectF bounds;

        private GraphsInfo(RectF bounds) {
            this.bounds = bounds;
        }

        public RectF getBounds() {
            return bounds;
        }
    }
}
