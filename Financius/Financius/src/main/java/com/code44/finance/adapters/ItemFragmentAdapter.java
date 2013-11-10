package com.code44.finance.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import com.code44.finance.ui.ItemFragment;

public class ItemFragmentAdapter extends AbstractCursorFragmentAdapter
{
    public ItemFragmentAdapter(Context context, FragmentManager fm, Class<?> fragmentClass)
    {
        super(context, fm, null, fragmentClass, ItemFragment.ARG_ITEM_ID);
    }
}
