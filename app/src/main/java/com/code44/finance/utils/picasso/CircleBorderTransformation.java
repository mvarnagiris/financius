package com.code44.finance.utils.picasso;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleBorderTransformation extends CircleTransformation {
    private final Paint paint;

    public CircleBorderTransformation(float width, int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(color);
    }

    @Override public String key() {
        return super.key() + "-border(" + paint.getStrokeWidth() + ", " + paint.getColor() + ")";
    }

    @Override protected void onHaveCircle(Canvas canvas, int size) {
        super.onHaveCircle(canvas, size);

        final float center = size / 2f;
        final float radius = (size - paint.getStrokeWidth()) / 2f;
        canvas.drawCircle(center, center, radius, paint);
    }
}
