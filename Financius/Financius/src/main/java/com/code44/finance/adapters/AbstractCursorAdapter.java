package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

/**
 * Takes care of finding cursor columns' indexes.
 *
 * @author Mantas Varnagiris
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractCursorAdapter extends CursorAdapter
{
    protected long[] selectedIDs;

    public AbstractCursorAdapter(Context context, Cursor c)
    {
        super(context, c, true);
        if (c != null)
            findIndexes(c);
    }

    /**
     * Get columns' indexes here.
     *
     * @param c Cursor.
     */
    protected abstract void findIndexes(Cursor c);

    @Override
    public Cursor swapCursor(Cursor newCursor)
    {
        if (newCursor != null)
            findIndexes(newCursor);
        return super.swapCursor(newCursor);
    }

    public void setSelectedIDs(long[] selectedIDs)
    {
        this.selectedIDs = selectedIDs;
        notifyDataSetChanged();
    }

    protected boolean isSelected(long id)
    {
        if (selectedIDs == null || selectedIDs.length == 0)
            return false;

        for (int i = 0; i < selectedIDs.length; i++)
            if (selectedIDs[i] == id)
                return true;

        return false;
    }
}