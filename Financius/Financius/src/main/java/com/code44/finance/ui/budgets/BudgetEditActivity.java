package com.code44.finance.ui.budgets;

import android.content.Context;
import com.code44.finance.ui.ItemEditActivity;
import com.code44.finance.ui.ItemEditFragment;
import com.code44.finance.ui.transactions.TransactionEditActivity;

public class BudgetEditActivity extends ItemEditActivity
{
    public static void startItemEdit(Context context, long itemId)
    {
        context.startActivity(makeIntent(context, BudgetEditActivity.class, itemId));
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return BudgetEditFragment.newInstance(itemId);
    }
}
