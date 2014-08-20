package com.code44.finance.utils;

import android.view.View;

public final class FieldValidationUtils {
    private FieldValidationUtils() {
    }

    public static void onError(final View view) {
        view.animate().alpha(0).withEndAction(new Runnable() {
            @Override public void run() {
                view.animate().alpha(1);
            }
        });
    }
}
