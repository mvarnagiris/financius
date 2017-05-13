package com.code44.finance.utils.picasso;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class ChainedTransformation implements Transformation {
    private final Transformation[] transformations;

    public ChainedTransformation(Transformation... transformations) {
        this.transformations = transformations == null ? new Transformation[0] : transformations;
    }

    @Override public Bitmap transform(Bitmap source) {
        Bitmap bitmap = source;
        for (Transformation transformation : transformations) {
            bitmap = transformation.transform(bitmap);
        }
        return bitmap;
    }

    @Override public String key() {
        final StringBuilder sb = new StringBuilder();
        for (Transformation transformation : transformations) {
            sb.append(transformation.key());
        }
        return sb.toString();
    }
}
