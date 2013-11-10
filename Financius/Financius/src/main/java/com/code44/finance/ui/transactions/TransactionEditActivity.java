package com.code44.finance.ui.transactions;

import android.content.Context;
import com.code44.finance.ui.ItemEditActivity;
import com.code44.finance.ui.ItemEditFragment;

public class TransactionEditActivity extends ItemEditActivity
{
    public static void startItemEdit(Context context, long itemId)
    {
        context.startActivity(makeIntent(context, TransactionEditActivity.class, itemId));
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return TransactionEditFragment.newInstance(itemId);
    }
}