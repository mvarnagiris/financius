package com.code44.finance.utils;

import android.content.Context;
import com.code44.finance.R;

public class LayoutType
{
    public static final int DEFAULT = 0;
    public static final int DEFAULT_LAND = 1;
    public static final int SW600DP = 2;
    public static final int SW600DP_LAND = 3;
    public static final int SW720DP = 4;
    public static final int SW720DP_LAND = 5;

    public static int get(Context context)
    {
        return context.getResources().getInteger(R.integer.layout_type);
    }

    public static boolean isTablet(Context context)
    {
        final int layoutType = get(context);
        return layoutType == SW600DP_LAND || layoutType == SW720DP_LAND || layoutType == SW600DP || layoutType == SW720DP;
    }

    public static boolean isTabletLandscape(Context context)
    {
        final int layoutType = get(context);
        return layoutType == SW600DP_LAND || layoutType == SW720DP_LAND;
    }
}
