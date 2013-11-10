package com.code44.finance.ui.budgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import com.code44.finance.R;
import com.code44.finance.adapters.ItemFragmentAdapter;
import com.code44.finance.ui.ItemPagerActivity;

public class BudgetItemActivity extends ItemPagerActivity
{
    public static void startItem(Context context, int position)
    {
        final Intent intent = makeIntent(context, BudgetItemActivity.class, position);
        context.startActivity(intent);
    }

    @Override
    protected ItemFragmentAdapter createAdapter(Context context, FragmentManager fm)
    {
        return new ItemFragmentAdapter(context, fm, BudgetItemFragment.class);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return BudgetListFragment.createItemsLoader(this);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.budget);
    }
}
