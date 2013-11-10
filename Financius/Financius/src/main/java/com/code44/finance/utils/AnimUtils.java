package com.code44.finance.utils;

import android.animation.ObjectAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class AnimUtils
{
    public static void shake(View v)
    {
        final float distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, v.getResources().getDisplayMetrics());
        final ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", 0, distance, 0, -distance, 0);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(4);
        animator.setDuration(150);
        animator.start();
    }
}