package com.code44.finance.ui.currencies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import com.code44.finance.R;
import com.code44.finance.adapters.ItemFragmentAdapter;
import com.code44.finance.ui.ItemPagerActivity;

public class CurrencyItemActivity extends ItemPagerActivity
{
    public static void startItem(Context context, int position)
    {
        final Intent intent = makeIntent(context, CurrencyItemActivity.class, position);
        context.startActivity(intent);
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
    protected String getActivityTitle()
    {
        return getString(R.string.currency);
    }

    @Override
    protected ItemFragmentAdapter createAdapter(Context context, FragmentManager fm)
    {
        return new ItemFragmentAdapter(context, fm, CurrencyItemFragment.class);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return CurrencyListFragment.createItemsLoader(this);
    }
}
