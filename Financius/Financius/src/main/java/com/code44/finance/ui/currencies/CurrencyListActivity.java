package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.code44.finance.R;
import com.code44.finance.ui.ItemListActivity;
import com.code44.finance.ui.ItemListFragment;

public class CurrencyListActivity extends ItemListActivity
{
    public static void startList(Context context)
    {
        final Intent intent = makeIntent(context, CurrencyListActivity.class);
        context.startActivity(intent);
    }

    public static void startListSelection(Context context, Fragment fragment, int requestCode)
    {
        final Intent intent = makeIntent(context, CurrencyListActivity.class);
        startForSelect(fragment, intent, requestCode);
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
    protected ItemListFragment createListFragment(int selectionType, long[] itemIDs)
    {
        return CurrencyListFragment.newInstance(selectionType);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.currencies);
    }
}