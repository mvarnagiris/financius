package com.code44.finance.graphs.pie;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.code44.finance.R;

import java.util.Arrays;
import java.util.List;

public class PieChartView extends View {
    private final RectF rect = new RectF();
    private final RectF outlineRect = new RectF();
    private final RectF inlineRect = new RectF();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint inlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Type type;
    private float donutWidthRatio;
    private int emptyColor;
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

        // Init
        outlinePaint.setStyle(Paint.Style.STROKE);
        inlinePaint.setStyle(Paint.Style.STROKE);
        setType(Type.DONUT);
        setDonutWidthRatio(0.3f);
        setOutlineWidth(getResources().getDimension(R.dimen.divider));
        setOutlineColor(Color.WHITE);
        setInlineWidth(outlinePaint.getStrokeWidth());
        setInlineColor(Color.WHITE);
        setEmptyColor(Color.WHITE);

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

        if (hasOutline()) {
            size -= outlinePaint.getStrokeWidth() * 2;
            leftOffset += outlinePaint.getStrokeWidth();
            topOffset += outlinePaint.getStrokeWidth();
        }

        if (type == Type.DONUT) {
            final float strokeWidth = size / 2 * donutWidthRatio;
            final float halfStrokeSize = strokeWidth / 2.0f;
            paint.setStrokeWidth(strokeWidth);
            size -= strokeWidth;
            leftOffset += halfStrokeSize;
            topOffset += halfStrokeSize;
        }

        rect.set(leftOffset, topOffset, leftOffset + size, topOffset + size);

        if (hasOutline()) {
            final float outlineSize = size + outlinePaint.getStrokeWidth() + (type == Type.DONUT ? paint.getStrokeWidth() : 0);
            final float outlineLeftOffset = leftOffset - (outlinePaint.getStrokeWidth() / 2.0f) - (type == Type.DONUT ? paint.getStrokeWidth() / 2.0f : 0);
            final float outlineTopOffset = topOffset - (outlinePaint.getStrokeWidth() / 2.0f) - (type == Type.DONUT ? paint.getStrokeWidth() / 2.0f : 0);
            outlineRect.set(outlineLeftOffset, outlineTopOffset, outlineLeftOffset + outlineSize, outlineTopOffset + outlineSize);
        }

        if (hasInline()) {
            final float inlineSize = size - inlinePaint.getStrokeWidth() - paint.getStrokeWidth();
            final float inlineLeftOffset = leftOffset + (paint.getStrokeWidth() / 2.0f) + (inlinePaint.getStrokeWidth() / 2.0f);
            final float inlineTopOffset = topOffset + (paint.getStrokeWidth() / 2.0f) + (inlinePaint.getStrokeWidth() / 2.0f);
            inlineRect.set(inlineLeftOffset, inlineTopOffset, inlineLeftOffset + inlineSize, inlineTopOffset + inlineSize);
        }
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
            paint.setColor(emptyColor);
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.0f, paint);
        }

        if (hasOutline()) {
            canvas.drawCircle(outlineRect.centerX(), outlineRect.centerY(), outlineRect.width() / 2.0f, outlinePaint);
        }

        if (hasInline()) {
            canvas.drawCircle(inlineRect.centerX(), inlineRect.centerY(), inlineRect.width() / 2.0f, inlinePaint);
        }
    }

    public void setType(Type type) {
        if (type == null) {
            throw new NullPointerException("Type cannot be null.");
        }

        this.type = type;
        switch (type) {
            case PIE:
                paint.setStyle(Paint.Style.FILL);
                break;

            case DONUT:
                paint.setStyle(Paint.Style.STROKE);
                break;
        }
        requestLayout();
    }

    public void setDonutWidthRatio(float donutWidthRatio) {
        if (Float.compare(donutWidthRatio, 0) < 0 || Float.compare(donutWidthRatio, 1) > 0) {
            throw new IllegalArgumentException("Donut width ratio must be [0, 1].");
        }

        this.donutWidthRatio = donutWidthRatio;
        requestLayout();
    }

    public void setOutlineWidth(float outlineWidth) {
        if (Float.compare(outlineWidth, 0) < 0) {
            throw new IllegalArgumentException("Outline width must be >= 0.");
        }

        outlinePaint.setStrokeWidth(outlineWidth);
        requestLayout();
    }

    public void setOutlineColor(int outlineColor) {
        outlinePaint.setColor(outlineColor);
        invalidate();
    }

    public void setInlineWidth(float inlineWidth) {
        if (Float.compare(inlineWidth, 0) < 0) {
            throw new IllegalArgumentException("Inline width must be >= 0.");
        }

        inlinePaint.setStrokeWidth(inlineWidth);
        requestLayout();
    }

    public void setInlineColor(int inlineColor) {
        inlinePaint.setColor(inlineColor);
        invalidate();
    }

    public void setPieChartData(PieChartData pieChartData) {
        if (pieChartData == null) {
            this.pieChartData = PieChartData.builder().build();
        } else {
            this.pieChartData = pieChartData;
        }

        requestLayout();
    }

    public void setEmptyColor(int emptyColor) {
        this.emptyColor = emptyColor;
        invalidate();
    }

    private boolean hasOutline() {
        return Float.compare(outlinePaint.getStrokeWidth(), 0) > 0;
    }

    private boolean hasInline() {
        return type == Type.DONUT && Float.compare(inlinePaint.getStrokeWidth(), 0) > 0;
    }

    public static enum Type {
        PIE, DONUT
    }
}
