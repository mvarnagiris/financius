package com.code44.finance.ui.budgets;

import android.content.Context;
import android.content.Intent;
import com.code44.finance.R;
import com.code44.finance.ui.ItemListActivity;
import com.code44.finance.ui.ItemListFragment;

@SuppressWarnings("UnusedDeclaration")
public class BudgetListActivity extends ItemListActivity
{
    public static void startList(Context context)
    {
        Intent intent = makeIntent(context, BudgetListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected ItemListFragment createListFragment(int selectionType, long[] itemIDs)
    {
        return BudgetListFragment.newInstance();
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.budgets);
    }
}
