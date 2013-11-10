package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.code44.finance.R;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.ui.categories.CategoryListFragment;
import com.code44.finance.utils.CategoriesUtils;
import com.code44.finance.utils.NotifyUtils;

public class CategoriesService extends AbstractItemService
{
    public static final String EXTRA_PARENT_ID = CategoriesService.class.getName() + ".EXTRA_PARENT_ID";
    public static final String EXTRA_TITLE = CategoriesService.class.getName() + ".EXTRA_TITLE";
    public static final String EXTRA_LEVEL = CategoriesService.class.getName() + ".EXTRA_LEVEL";
    public static final String EXTRA_TYPE = CategoriesService.class.getName() + ".EXTRA_TYPE";
    public static final String EXTRA_COLOR = CategoriesService.class.getName() + ".EXTRA_COLOR";
    public static final String EXTRA_SWAP_FROM = CategoriesService.class.getName() + ".EXTRA_SWAP_FROM";
    public static final String EXTRA_SWAP_TO = CategoriesService.class.getName() + ".EXTRA_SWAP_TO";
    // -----------------------------------------------------------------------------------------------------------------
    public static final int RT_SWAP_ORDER = 1;

    @Override
    protected void handleRequest(Intent intent, int requestType, long startTime, long lastSuccessfulWorkTime) throws Exception
    {
        switch (requestType)
        {
            case RT_SWAP_ORDER:
                rtSwapOrder(intent);
                break;
        }
        super.handleRequest(intent, requestType, startTime, lastSuccessfulWorkTime);
    }

    @Override
    protected void notifyOnItemUpdated()
    {
        NotifyUtils.onCategoryUpdated(this);
    }

    @Override
    protected void prepareValues(ContentValues outValues, Intent intent)
    {
        // Get values
        final long itemId = intent.getLongExtra(EXTRA_ITEM_ID, 0);
        final long parentId = intent.getLongExtra(EXTRA_PARENT_ID, 0);
        final String title = intent.getStringExtra(EXTRA_TITLE);
        final int level = intent.getIntExtra(EXTRA_LEVEL, 1);
        final int type = intent.getIntExtra(EXTRA_TYPE, Tables.Categories.Type.EXPENSE);
        final int color = intent.getIntExtra(EXTRA_COLOR, getResources().getColor(R.color.f_light_darker2));

        // Find order
        int order = 0;
        int parentOrder = 0;
        Cursor c = null;

        if (itemId == 0)
        {
            // Creating new item
            try
            {
                c = DBHelper.getInstance(this).getReadableDatabase().query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.T_ID}, level == 1 ? Tables.Categories.LEVEL + "=?" : Tables.Categories.PARENT_ID + "=?", level == 1 ? new String[]{"1"} : new String[]{String.valueOf(parentId)}, null, null, null);
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
                c = DBHelper.getInstance(this).getReadableDatabase().query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.ORDER}, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(itemId)}, null, null, null);
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
            c = DBHelper.getInstance(this).getReadableDatabase().query(Tables.Categories.TABLE_NAME, new String[]{Tables.Categories.ORDER}, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(parentId)}, null, null, null);
            if (c != null && c.moveToFirst())
                parentOrder = c.getInt(0);
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        // Prepare
        CategoriesUtils.prepareValues(outValues, parentId, title, level, type, color, order, parentOrder);
    }

    @Override
    protected Uri getUriForItems()
    {
        return CategoriesProvider.uriCategories(this);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Categories.TABLE_NAME;
    }

    private void rtSwapOrder(Intent intent) throws Exception
    {
        // Get values
        final int swapFrom = intent.getIntExtra(EXTRA_SWAP_FROM, -1);
        final int swapTo = intent.getIntExtra(EXTRA_SWAP_TO, -1);
        final int categoryType = intent.getIntExtra(EXTRA_TYPE, -1);

        // Find values for swapping
        int level = -1;
        long fromCategoryId = 0;
        long fromParentId = 0;
        int fromOrder = -1;
        int toOrder = -1;
        Cursor c = null;
        try
        {
            c = getContentResolver().query(CategoriesProvider.uriCategories(this), new String[]{Tables.Categories.T_ID, Tables.Categories.LEVEL, Tables.Categories.ORDER, Tables.Categories.PARENT_ID}, CategoryListFragment.getLoaderSelection(null), CategoryListFragment.getLoaderSelectionArgs(categoryType, null), CategoryListFragment.getLoaderSortOrder());
            if (c != null)
            {
                c.moveToPosition(swapFrom);
                fromCategoryId = c.getLong(0);
                fromParentId = c.getLong(3);
                level = c.getInt(1);
                fromOrder = c.getInt(2);

                c.moveToPosition(swapTo);
                if (level == 2)
                {
                    // When swapping sub-categories, swapTo will always be within their section
                    toOrder = c.getInt(2);
                }
                else
                {
                    // When swapping main categories, swapTo might be a subcategory, so we need to find first main category
                    do
                    {
                        if (c.getInt(1) == 1)
                        {
                            toOrder = c.getInt(2);
                            break;
                        }
                    }
                    while (c.moveToPrevious());
                }
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }


        // Swap categories
        final SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
        try
        {
            db.beginTransaction();

            final ContentValues values = new ContentValues();

            c = null;
            try
            {
                final String firstArg = level == 1 ? String.valueOf(level) : String.valueOf(fromParentId);
                final String betweenFrom = String.valueOf(fromOrder < toOrder ? fromOrder + 1 : toOrder);
                final String betweenTo = String.valueOf(fromOrder < toOrder ? toOrder : fromOrder - 1);
                c = getContentResolver().query(
                        CategoriesProvider.uriCategories(this),
                        new String[]{Tables.Categories.T_ID, Tables.Categories.ORDER},
                        level == 1 ? Tables.Categories.LEVEL + "=? and " + Tables.Categories.ORDER + " between ? and ?" : Tables.Categories.PARENT_ID + "=? and " + Tables.Categories.ORDER + " between ? and ?",
                        new String[]{firstArg, betweenFrom, betweenTo}, null);
                if (c != null && c.moveToFirst())
                {
                    int newOrder;
                    do
                    {
                        values.clear();
                        newOrder = c.getInt(1) + (swapFrom < swapTo ? -1 : 1);

                        values.put(Tables.Categories.ORDER, newOrder);
                        db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                        if (level == 1)
                        {
                            // Update subcategories for that main category as well
                            values.clear();
                            values.put(Tables.Categories.PARENT_ORDER, newOrder);
                            db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.PARENT_ID + "=?", new String[]{String.valueOf(c.getLong(0))});
                        }
                    }
                    while (c.moveToNext());
                }
            }
            finally
            {
                if (c != null && !c.isClosed())
                    c.close();
            }

            values.clear();
            values.put(Tables.Categories.ORDER, toOrder);
            db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.T_ID + "=?", new String[]{String.valueOf(fromCategoryId)});

            if (level == 1)
            {
                values.clear();
                values.put(Tables.Categories.PARENT_ORDER, toOrder);
                db.update(Tables.Categories.TABLE_NAME, values, Tables.Categories.PARENT_ID + "=?", new String[]{String.valueOf(fromCategoryId)});
            }

            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }

        NotifyUtils.onCategoryUpdated(this);
    }
}