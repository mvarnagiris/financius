package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.adapters.AbstractCursorAdapter;
import com.code44.finance.adapters.CurrenciesAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.ItemListFragment;

public class CurrencyListFragment extends ItemListFragment
{
    public static final String RESULT_EXTRA_CODE = CurrencyListFragment.class.getName() + ".RESULT_EXTRA_CODE";
    public static final String RESULT_EXTRA_SYMBOL = CurrencyListFragment.class.getName() + ".RESULT_EXTRA_SYMBOL";

    public static CurrencyListFragment newInstance(int selectionType)
    {
        final CurrencyListFragment f = new CurrencyListFragment();
        f.setArguments(makeArgs(selectionType, null));
        return f;
    }

    public static Loader<Cursor> createItemsLoader(Context context)
    {
        final Uri uri = CurrenciesProvider.uriCurrencies(context);
        final String[] projection = new String[]{Tables.Currencies.T_ID, Tables.Currencies.CODE, Tables.Currencies.EXCHANGE_RATE, Tables.Currencies.IS_DEFAULT, Tables.Currencies.SYMBOL};
        final String selection = Tables.Currencies.DELETE_STATE + "=?";
        final String[] selectionArgs = new String[]{String.valueOf(Tables.DeleteState.NONE)};
        final String sortOrder = Tables.Currencies.IS_DEFAULT + " desc, " + Tables.Currencies.CODE;

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.currencies_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_refresh_exchange_rates:
                API.updateExchangeRates(getActivity(), false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected AbstractCursorAdapter createAdapter(Context context)
    {
        return new CurrenciesAdapter(context);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return createItemsLoader(getActivity());
    }

    @Override
    protected void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras)
    {
        outExtras.putString(RESULT_EXTRA_CODE, c.getString(c.getColumnIndex(Tables.Currencies.CODE)));
        outExtras.putString(RESULT_EXTRA_SYMBOL, c.getString(c.getColumnIndex(Tables.Currencies.SYMBOL)));
    }

    @Override
    protected void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c, View view)
    {
        CurrencyItemActivity.startItem(context, itemId, view);
    }

    @Override
    protected void startItemCreate(Context context, View view)
    {
        CurrencyEditActivity.startItemEdit(context, 0);
    }
}