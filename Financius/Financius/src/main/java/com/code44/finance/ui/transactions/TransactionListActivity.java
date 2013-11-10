package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import com.code44.finance.R;
import com.code44.finance.ui.ItemListActivity;
import com.code44.finance.ui.ItemListFragment;

public class TransactionListActivity extends ItemListActivity
{
    public static void start(Context context)
    {
        Intent intent = new Intent(context, TransactionListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected ItemListFragment createListFragment(int selectionType, long[] itemIDs)
    {
        return TransactionListFragment.newInstance(selectionType);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.transactions);
    }
}
