package com.code44.finance.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.db.DBHelper;
import com.code44.finance.db.Tables;

public abstract class BaseProvider extends ContentProvider
{
    protected static final String CONTENT_URI_BASE = "content://";
    protected static final String TYPE_LIST_BASE = "vnd.android.cursor.dir/vnd.code44.";
    protected static final String TYPE_ITEM_BASE = "vnd.android.cursor.item/vnd.code44.";
    protected UriMatcher uriMatcher;
    protected SQLiteDatabase db;

    protected static String getAuthority(Context context, Class<?> clss)
    {
        return context.getPackageName() + ".providers." + clss.getSimpleName();
    }

    public static long getLocalId(SQLiteDatabase db, String tableName, String serverId)
    {
        long localId = 0;
        Cursor c = null;
        try
        {
            c = db.query(tableName, new String[]{BaseColumns._ID}, tableName + "_" + Tables.SUFFIX_SERVER_ID + "=?", new String[]{serverId}, null, null, null);
            if (c != null && c.moveToFirst())
                localId = c.getLong(0);
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }
        return localId;
    }

    public static long doUpdateOrInsert(SQLiteDatabase db, String tableName, ContentValues values, boolean returnNewId)
    {
        // Get id columns
        final String idColumn = BaseColumns._ID;
        final String serverIdColumn = tableName + "_" + Tables.SUFFIX_SERVER_ID;

        // Get ids
        final Long id = values.getAsLong(idColumn);
        final String serverId = values.getAsString(serverIdColumn);
        long newId = id != null ? id : 0;

        // Find value to check for update
        final boolean tryUpdate;
        final String columnToCheck;
        String valueToCheck;
        if (newId > 0)
        {
            // We have local ID. Will try to update.
            tryUpdate = true;
            columnToCheck = idColumn;
            valueToCheck = String.valueOf(newId);
        }
        else if (!TextUtils.isEmpty(serverId))
        {
            // Have server ID. Will try to update.
            tryUpdate = true;
            columnToCheck = serverIdColumn;
            valueToCheck = serverId;
        }
        else
        {
            // No IDs. Will not try to update.
            tryUpdate = false;
            columnToCheck = "";
            valueToCheck = "";
        }

        // Update or insert
        if (!tryUpdate || db.update(tableName, values, columnToCheck + "=?", new String[]{valueToCheck}) == 0)
        {
            newId = db.insert(tableName, null, values);
            if (newId <= 0)
                throw new SQLException("Failed to insert row");
        }

        // Get local ID if necessary
        if (newId == 0 && returnNewId && !TextUtils.isEmpty(serverId))
            newId = getLocalId(db, tableName, serverId);

        return newId;
    }

    public static int doArrayInsert(SQLiteDatabase db, String tableName, ContentValues[] valuesArray)
    {
        int count = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < valuesArray.length; i++)
        {
            doUpdateOrInsert(db, tableName, valuesArray[i], false);
            count++;
        }
        return count;
    }

    public static int doBulkInsert(SQLiteDatabase db, String tableName, ContentValues[] valuesArray)
    {
        int count = 0;
        try
        {
            db.beginTransaction();
            count = doArrayInsert(db, tableName, valuesArray);
            db.setTransactionSuccessful();
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            db.endTransaction();
        }

        return count;
    }

    public static void notifyURIs(Uri... notifyURIs)
    {
        if (notifyURIs != null && notifyURIs.length > 0)
        {
            //noinspection ConstantConditions
            final ContentResolver resolver = App.getAppContext().getContentResolver();

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < notifyURIs.length; i++)
                resolver.notifyChange(notifyURIs[i], null);
        }
    }

    @Override
    public boolean onCreate()
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        db = DBHelper.get(getContext()).getWritableDatabase();
        return (db != null);
    }
}