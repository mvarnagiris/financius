package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.ui.ItemActivity;
import com.code44.finance.ui.ItemFragment;

public class CurrencyItemActivity extends ItemActivity
{
    public static void startItem(Context context, long itemId, View expandFrom)
    {
        final Intent intent = makeIntent(context, CurrencyItemActivity.class, itemId);
        start(context, intent, expandFrom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        //noinspection ConstantConditions
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    protected ItemFragment createItemFragment(long itemId)
    {
        return CurrencyItemFragment.newInstance(itemId);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.currency);
    }
}
