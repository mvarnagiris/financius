package com.code44.finance.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.code44.finance.R;

public abstract class ItemEditStepsFragment extends ItemEditFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_item_edit_steps, container, false);
    }

    @Override
    protected void initLoaders()
    {
        // Ignore this. Will be handled by sub-fragments
    }

    @Override
    protected boolean bindItem(Cursor c, boolean isDataLoaded)
    {
        // Ignore this. Will be handled by sub-fragments
        return false;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        // Ignore this. Will be handled by sub-fragments
        return null;
    }
}
