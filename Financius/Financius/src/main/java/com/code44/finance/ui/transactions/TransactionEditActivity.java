package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.code44.finance.ui.ItemEditActivity;
import com.code44.finance.ui.ItemEditFragment;

public class TransactionEditActivity extends ItemEditActivity
{
    public static void startItemEdit(Context context, long itemId, View expandFromView)
    {
        final Intent intent = makeIntent(context, TransactionEditActivity.class, itemId);
        start(context, intent, expandFromView);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return TransactionEditFragment.newInstance(itemId);
    }
}