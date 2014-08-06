package com.code44.finance.graphs.pie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.List;

public class PieChartView extends View {
    private final RectF rect = new RectF();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private PieChartData pieChartData;

    @SuppressWarnings("UnusedDeclaration")
    public PieChartView(Context context) {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (isInEditMode()) {
            setPieChartData(PieChartData.builder().setValues(Arrays.asList(new PieChartValue(15, 0xffe51c23), new PieChartValue(25, 0xff5677fc))).build());
        } else {
            setPieChartData(null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final int contentWidth = w - getPaddingLeft() - getPaddingRight();
        final int contentHeight = h - getPaddingTop() - getPaddingBottom();

        int size = Math.min(contentWidth, contentHeight);
        float leftOffset = getPaddingLeft() + Math.max((contentWidth - contentHeight) / 2.0f, 0.0f);
        float topOffset = getPaddingTop() + Math.max((contentHeight - contentWidth) / 2.0f, 0.0f);

        if (paint.getStyle().equals(Paint.Style.STROKE)) {
            final float strokeWidth = size / 2 * pieChartData.getDonutWidthRatio();
            final float halfStrokeSize = strokeWidth / 2.0f;
            paint.setStrokeWidth(strokeWidth);
            size -= strokeWidth;
            leftOffset += halfStrokeSize;
            topOffset += halfStrokeSize;
        }


        rect.set(leftOffset, topOffset, leftOffset + size, topOffset + size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final List<PieChartValue> values = pieChartData.getValues();
        final long total = pieChartData.getTotalValue();
        final boolean useCenter = paint.getStyle() == Paint.Style.FILL;
        float startAngle = -90.0f;

        if (values.size() > 0) {
            for (PieChartValue value : values) {
                final float sweepAngle = 360.0f * value.getValue() / total;
                paint.setColor(value.getColor());
                canvas.drawArc(rect, startAngle, sweepAngle, useCenter, paint);
                startAngle += sweepAngle;
            }
        } else {
            paint.setColor(Color.WHITE);
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.0f, paint);
        }
    }

    public void setPieChartData(PieChartData pieChartData) {
        if (pieChartData == null) {
            this.pieChartData = PieChartData.builder().build();
        } else {
            this.pieChartData = pieChartData;
        }

        switch (this.pieChartData.getType()) {
            case PIE:
                paint.setStyle(Paint.Style.FILL);
                break;

            case DONUT:
                paint.setStyle(Paint.Style.STROKE);
                break;
        }

        invalidate();
    }

    public static enum Type {
        PIE, DONUT
    }
}
