package com.code44.finance.db.model;

import android.database.Cursor;
import com.code44.finance.db.Tables;

import java.util.Date;

public abstract class DBRecord
{
    protected long id;
    protected String serverId;
    protected Date timestamp;
    protected int syncState;
    protected int deleteState;

    protected static void initBase(DBRecord record, Cursor c, long id, String tableName)
    {
        final int iServerId = c.getColumnIndex(tableName + "_" + Tables.SUFFIX_SERVER_ID);
        final int iDeleteState = c.getColumnIndex(tableName + "_" + Tables.SUFFIX_DELETE_STATE);

        record.setId(id);

        if (iServerId >= 0)
            record.setServerId(c.getString(iServerId));

        if (iDeleteState >= 0)
            record.setDeleteState(c.getInt(iDeleteState));
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;

    }

    public String getServerId()
    {
        return serverId;
    }

    public void setServerId(String serverId)
    {
        this.serverId = serverId;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public int getSyncState()
    {
        return syncState;
    }

    public void setSyncState(int syncState)
    {
        this.syncState = syncState;
    }

    public int getDeleteState()
    {
        return deleteState;
    }

    public void setDeleteState(int deleteState)
    {
        this.deleteState = deleteState;
    }
}