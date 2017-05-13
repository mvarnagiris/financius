package com.code44.finance.utils.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CircleTransformation implements Transformation {
    @Override public Bitmap transform(Bitmap source) {
        final int size = Math.min(source.getWidth(), source.getHeight());

        final int x = (source.getWidth() - size) / 2;
        final int y = (source.getHeight() - size) / 2;

        final Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        final Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        final Canvas canvas = new Canvas(bitmap);
        final Paint paint = new Paint();
        final BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        final float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        onHaveCircle(canvas, size);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override public String key() {
        return "circle";
    }

    protected void onHaveCircle(Canvas canvas, int size) {
    }
}
