package com.code44.finance.utils;

import android.content.ContentValues;
import com.code44.finance.db.Tables;

public class CategoriesUtils
{
    public static ContentValues getValues(long parentId, String title, int level, int type, int color, int order, int parentOrder, int origin)
    {
        ContentValues values = new ContentValues();

        values.put(Tables.Categories.PARENT_ID, parentId);
        values.put(Tables.Categories.TITLE, title);
        values.put(Tables.Categories.LEVEL, level);
        values.put(Tables.Categories.TYPE, type);
        values.put(Tables.Categories.COLOR, color);
        values.put(Tables.Categories.ORIGIN, origin);
        values.put(Tables.Categories.ORDER, order);
        values.put(Tables.Categories.PARENT_ORDER, parentOrder);

        return values;
    }
}