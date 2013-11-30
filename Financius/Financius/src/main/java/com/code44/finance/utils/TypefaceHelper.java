package com.code44.finance.utils;

import android.graphics.Typeface;
import android.util.SparseArray;
import com.code44.finance.FinanciusApp;

public class TypefaceHelper
{
    public static final int TYPEFACE_SERIF = 1;
    private static final SparseArray<Typeface> cache = new SparseArray<Typeface>();

    public static Typeface getTypeface(int typefaceCode)
    {
        Typeface typeface = cache.get(typefaceCode);
        if (typeface == null)
        {
            switch (typefaceCode)
            {
                case TYPEFACE_SERIF:
                    typeface = Typeface.createFromAsset(FinanciusApp.getAppContext().getAssets(), "fonts/RobotoSlab-Regular.ttf");
                    break;
            }
            cache.put(typefaceCode, typeface);
        }

        return typeface;
    }
}