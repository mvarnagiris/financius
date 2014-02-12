package com.code44.finance.ui.transactions;

import android.content.Context;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.ui.ItemActivity;
import com.code44.finance.ui.ItemFragment;

public class TransactionItemActivity extends ItemActivity
{
    public static void startItem(Context context, long itemId, View expandFrom)
    {
        start(context, makeIntent(context, TransactionItemActivity.class, itemId), expandFrom);
    }

    @Override
    protected ItemFragment createItemFragment(long itemId)
    {
        return TransactionItemFragment.newInstance(itemId);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.transaction);
    }
}