package com.code44.finance.utils;

import android.content.ContentValues;
import android.database.Cursor;
import com.code44.finance.App;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.Tables;

public class CategoriesUtils
{
    public static ContentValues getValues(long parentId, String title, int level, int type, int color)
    {
        ContentValues values = new ContentValues();

        values.put(Tables.Categories.PARENT_ID, parentId);
        values.put(Tables.Categories.TITLE, title);
        values.put(Tables.Categories.LEVEL, level);
        values.put(Tables.Categories.TYPE, type);
        values.put(Tables.Categories.COLOR, color);

        return values;
    }

    public static class OrderValuesUpdater implements DataHelper.ValuesUpdater
    {
        @Override
        public void updateValues(ContentValues values)
        {
            // Get values
            //noinspection ConstantConditions
            final long itemId = values.containsKey(Tables.Categories.ID) ? values.getAsLong(Tables.Categories.ID) : 0;
            //noinspection ConstantConditions
            final long parentId = values.containsKey(Tables.Categories.PARENT_ID) ? values.getAsLong(Tables.Categories.PARENT_ID) : 0;
            //noinspection ConstantConditions
            final int level = values.containsKey(Tables.Categories.LEVEL) ? values.getAsInteger(Tables.Categories.LEVEL) : 1;

            // Find order
            int order = 0;
            int parentOrder = 0;
            Cursor c = null;

            if (itemId == 0)
            {
                // Creating new item
                try
                {
                    //noinspection ConstantConditions
                    c = DBHelper.get(App.getAppContext()).getReadableDatabase().query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.T_ID}, level == 1 ? Tables.Categories.LEVEL + "=?" : Tables.Categories.PARENT_ID + "=?", level == 1 ? new String[]{"1"} : new String[]{String.valueOf(parentId)}, null, null, null);
                    if (c != null && c.moveToFirst())
                        order = c.getCount();
                }
                finally
                {
                    if (c != null && !c.isClosed())
                        c.close();
                }
            }
            else
            {
                // Updating item
                try
                {
                    //noinspection ConstantConditions
                    c = DBHelper.get(App.getAppContext()).getReadableDatabase().query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.ORDER}, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(itemId)}, null, null, null);
                    if (c != null && c.moveToFirst())
                        order = c.getInt(0);
                }
                finally
                {
                    if (c != null && !c.isClosed())
                        c.close();
                }
            }

            try
            {
                //noinspection ConstantConditions
                c = DBHelper.get(App.getAppContext()).getReadableDatabase().query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.ORDER}, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(parentId)}, null, null, null);
                if (c != null && c.moveToFirst())
                    parentOrder = c.getInt(0);
            }
            finally
            {
                if (c != null && !c.isClosed())
                    c.close();
            }

            values.put(Tables.Categories.ORDER, order);
            values.put(Tables.Categories.PARENT_ORDER, parentOrder);
        }
    }
}