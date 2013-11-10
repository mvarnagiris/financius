package com.code44.finance.utils;

import android.content.ContentValues;
import android.text.TextUtils;
import com.code44.finance.db.Tables;
import com.code44.finance.db.Tables.DeleteState;
import com.code44.finance.db.Tables.SyncState;

public class CategoriesUtils
{
    /**
     * Prepare values for update or create.
     */
    public static void prepareValues(ContentValues values, String serverId, long parentId, String title, int level, int type, int color, int order, int parentOrder, int origin, int deleteState, int syncState)
    {
        if (!TextUtils.isEmpty(serverId))
            values.put(Tables.Categories.SERVER_ID, serverId);
        values.put(Tables.Categories.PARENT_ID, parentId);
        values.put(Tables.Categories.TITLE, title);
        values.put(Tables.Categories.LEVEL, level);
        values.put(Tables.Categories.TYPE, type);
        values.put(Tables.Categories.COLOR, color);
        values.put(Tables.Categories.ORIGIN, origin);
        values.put(Tables.Categories.ORDER, order);
        values.put(Tables.Categories.PARENT_ORDER, parentOrder);
        values.put(Tables.Categories.TIMESTAMP, System.currentTimeMillis());
        values.put(Tables.Categories.DELETE_STATE, deleteState);
        values.put(Tables.Categories.SYNC_STATE, syncState);
    }

    /**
     * Prepare values for update or create.
     */
    public static void prepareValues(ContentValues values, long parentId, String title, int level, int type, int color, int order, int parentOrder)
    {
        prepareValues(values, null, parentId, title, level, type, color, order, parentOrder, Tables.Categories.Origin.USER, DeleteState.NONE, SyncState.LOCAL_CHANGES);
    }
}