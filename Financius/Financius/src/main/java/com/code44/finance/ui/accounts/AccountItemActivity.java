package com.code44.finance.ui.accounts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import com.code44.finance.R;
import com.code44.finance.adapters.ItemFragmentAdapter;
import com.code44.finance.ui.ItemListFragment;
import com.code44.finance.ui.ItemPagerActivity;

public class AccountItemActivity extends ItemPagerActivity
{
    public static void startItem(Context context, int position)
    {
        final Intent intent = makeIntent(context, AccountItemActivity.class, position);
        context.startActivity(intent);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.account);
    }

    @Override
    protected ItemFragmentAdapter createAdapter(Context context, FragmentManager fm)
    {
        return new ItemFragmentAdapter(context, fm, AccountItemFragment.class);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return AccountListFragment.createItemsLoader(this, ItemListFragment.SELECTION_TYPE_NONE);
    }
}
