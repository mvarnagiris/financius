package com.code44.finance.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;

public final class ThemeUtils {
    private ThemeUtils() {
    }

    public static int getColor(Context context, @AttrRes int resId) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{resId});
        final int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static Drawable getDrawable(Context context, @AttrRes int resId) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{resId});
        final Drawable drawable = a.getDrawable(0);
        a.recycle();
        return drawable;
    }
}
