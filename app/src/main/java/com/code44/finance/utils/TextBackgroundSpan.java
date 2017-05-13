package com.code44.finance.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class TextBackgroundSpan extends ReplacementSpan {
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF backgroundRect = new RectF();
    private final float radius;
    private int size;

    public TextBackgroundSpan(int color, float radius) {
        this.backgroundPaint.setColor(color);
        this.radius = radius;
    }

    @Override public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
        size = Math.round(measureText(paint, text, start, end) + radius);
        return size;
    }

    @Override public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        backgroundRect.set(x, top, x + size, bottom);
        canvas.drawRoundRect(backgroundRect, radius, radius, backgroundPaint);
        canvas.drawText(text, start, end, x + radius / 2, y, paint);
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }
}
