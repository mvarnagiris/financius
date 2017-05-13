package com.code44.finance.utils.picasso;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Transformation;

public class OverlayTransformation implements Transformation {
    private final Drawable overlayDrawable;

    public OverlayTransformation(int color) {
        this(new ColorDrawable(color));
    }

    public OverlayTransformation(Drawable drawable) {
        this.overlayDrawable = drawable;
    }

    @Override public Bitmap transform(Bitmap source) {
        final int width = source.getWidth();
        final int height = source.getHeight();

        final Bitmap bitmap = Bitmap.createBitmap(width, height, source.getConfig());

        final Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, 0, 0, null);
        source.recycle();

        overlayDrawable.setBounds(0, 0, width, height);
        overlayDrawable.draw(canvas);

        return bitmap;
    }

    @Override public String key() {
        return "overlay";
    }
}
