package com.code44.finance.ui.currencies;

import android.content.Context;
import com.code44.finance.ui.ItemEditActivity;
import com.code44.finance.ui.ItemEditFragment;

public class CurrencyEditActivity extends ItemEditActivity
{
    public static void startItemEdit(Context context, long itemId)
    {
        start(context, makeIntent(context, CurrencyEditActivity.class, itemId), null);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return CurrencyEditFragment.newInstance(itemId);
    }
}
