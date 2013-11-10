package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public abstract class AbstractCursorFragmentAdapter extends FragmentStatePagerAdapter
{
    protected final Context context;
    protected final Class<?> fragmentClass;
    protected final String idArgumentName;
    protected Cursor cursor;
    protected int iId;

    public AbstractCursorFragmentAdapter(Context context, FragmentManager fm, Cursor c, Class<?> fragmentClass, String idArgumentName)
    {
        super(fm);
        this.context = context;
        this.cursor = c;
        this.fragmentClass = fragmentClass;
        this.idArgumentName = idArgumentName;
    }

    public long getItemId(int position)
    {
        cursor.moveToPosition(position);
        return cursor.getLong(iId);
    }

    @Override
    public Fragment getItem(int position)
    {
        final Bundle args = new Bundle();
        if (cursor.moveToPosition(position))
        {
            args.putLong(idArgumentName, cursor.getLong(iId));
            onMakeArgs(position, args);
        }
        return Fragment.instantiate(context, fragmentClass.getName(), args);
    }

    @Override
    public int getCount()
    {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @Override
    public int getItemPosition(Object object)
    {
        return cursor == null ? super.getItemPosition(object) : POSITION_NONE;
    }

    /**
     * Swaps cursors.
     *
     * @param newCursor New Cursor.
     * @return Old cursor.
     */
    public Cursor swapCursor(Cursor newCursor)
    {
        if (newCursor != null)
            findIndexes(newCursor);

        final Cursor oldCursor = cursor;
        cursor = newCursor;
        notifyDataSetChanged();

        return oldCursor;
    }

    public Cursor getCursor()
    {
        return cursor;
    }

    protected void onMakeArgs(int position, Bundle outArgs)
    {

    }

    protected void findIndexes(Cursor c)
    {
        iId = c.getColumnIndex(BaseColumns._ID);
    }
}
