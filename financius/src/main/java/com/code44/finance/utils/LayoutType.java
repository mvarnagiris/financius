package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.R;

public final class LayoutType {
    private static final int DEFAULT = 0;
    private static final int DEFAULT_LAND = 1;
    private static final int SW600 = 2;
    private static final int SW600_LAND = 3;
    private static final int SW720 = 4;
    private static final int SW720_LAND = 5;

    private final Context context;

    public LayoutType(Context context) {
        this.context = context;
    }

    public boolean isDefault() {
        final int layoutType = getLayoutType();
        return layoutType == DEFAULT || layoutType == DEFAULT_LAND;
    }

    public boolean isSW600() {
        final int layoutType = getLayoutType();
        return layoutType == SW600 || layoutType == SW600_LAND;
    }

    public boolean isSW720() {
        final int layoutType = getLayoutType();
        return layoutType == SW720 || layoutType == SW720_LAND;
    }

    public boolean isPortrait() {
        final int layoutType = getLayoutType();
        return layoutType % 2 == 0;
    }

    public boolean isLandscape() {
        return !isPortrait();
    }

    private int getLayoutType() {
        return context.getResources().getInteger(R.integer.layout_type);
    }
}
