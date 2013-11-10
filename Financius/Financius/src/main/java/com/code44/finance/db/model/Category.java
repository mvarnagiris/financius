package com.code44.finance.db.model;

import android.database.Cursor;
import com.code44.finance.db.Tables;

/**
 * Created by Mantas on 10/06/2013.
 */
public class Category extends DBRecord
{
    private long parentId;
    private String title;
    private int color;
    private int level;
    private int type;
    private int origin;

    public static Category from(Cursor c)
    {
        return from(c, Tables.Categories.ID);
    }

    public static Category from(Cursor c, String idColumnName)
    {
        final Category category = new Category();
        initBase(category, c, c.getLong(c.getColumnIndex(idColumnName)), Tables.Categories.TABLE_NAME);

        final int iParentId = c.getColumnIndex(Tables.Categories.PARENT_ID);
        final int iTitle = c.getColumnIndex(Tables.Categories.TITLE);
        final int iColor = c.getColumnIndex(Tables.Categories.COLOR);
        final int iLevel = c.getColumnIndex(Tables.Categories.LEVEL);
        final int iType = c.getColumnIndex(Tables.Categories.TYPE);
        final int iOrigin = c.getColumnIndex(Tables.Categories.ORIGIN);

        if (iParentId >= 0)
            category.setParentId(c.getLong(iParentId));

        if (iTitle >= 0)
            category.setTitle(c.getString(iTitle));

        if (iColor >= 0)
            category.setColor(c.getInt(iColor));

        if (iLevel >= 0)
            category.setLevel(c.getInt(iLevel));

        if (iType >= 0)
            category.setType(c.getInt(iType));

        if (iOrigin >= 0)
            category.setOrigin(c.getInt(iOrigin));

        return category;
    }

    public long getParentId()
    {
        return parentId;
    }

    public void setParentId(long parentId)
    {
        this.parentId = parentId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getOrigin()
    {
        return origin;
    }

    public void setOrigin(int origin)
    {
        this.origin = origin;
    }
}