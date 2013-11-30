package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.ItemFragment;
import com.code44.finance.utils.AmountUtils;

public class CurrencyItemFragment extends ItemFragment
{
    private static final int LOADER_ACCOUNTS = 1;
    private static final int LOADER_DEFAULT_CURRENCY = 2;
    // -----------------------------------------------------------------------------------------------------------------
    private LinearLayout container_V;
    private TextView code_TV;
    private TextView format_TV;
    private TextView currentCurrency_TV;
    private TextView exchangeRate_TV;
    private TextView accounts_TV;
    // -----------------------------------------------------------------------------------------------------------------
    private String defaultCurrencyCode;

    public static CurrencyItemFragment newInstance(long itemId)
    {
        final CurrencyItemFragment f = new CurrencyItemFragment();
        f.setArguments(makeArgs(itemId));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        container_V = (LinearLayout) view.findViewById(R.id.container_V);
        code_TV = (TextView) view.findViewById(R.id.code_TV);
        format_TV = (TextView) view.findViewById(R.id.format_TV);
        currentCurrency_TV = (TextView) view.findViewById(R.id.currentCurrency_TV);
        exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
        accounts_TV = (TextView) view.findViewById(R.id.accounts_TV);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
        getLoaderManager().initLoader(LOADER_DEFAULT_CURRENCY, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_ACCOUNTS:
            {
                Uri uri = AccountsProvider.uriAccounts(getActivity());
                String[] projection = new String[]{Tables.Accounts.TITLE};
                String selection = Tables.Accounts.CURRENCY_ID + "=? and " + Tables.Accounts.DELETE_STATE + "=? and " + Tables.Accounts.ORIGIN + "<>?";
                String[] selectionArgs = new String[]{String.valueOf(itemId), String.valueOf(Tables.DeleteState.NONE), String.valueOf(Tables.Accounts.Origin.SYSTEM)};
                String sortOrder = null;

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
            }

            case LOADER_DEFAULT_CURRENCY:
            {
                Uri uri = CurrenciesProvider.uriCurrencies(getActivity());
                String[] projection = new String[]{Tables.Currencies.CODE};
                String selection = Tables.Currencies.IS_DEFAULT + "=?";
                String[] selectionArgs = new String[]{"1"};
                String sortOrder = null;

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
            }
        }
        return super.onCreateLoader(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_ACCOUNTS:
                bindAccounts(cursor);
                break;

            case LOADER_DEFAULT_CURRENCY:
                bindDefaultCurrency(cursor);
                break;
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    protected void startItemEdit(Context context, long itemId, View expandFrom)
    {
        CurrencyEditActivity.startItemEdit(context, itemId);
    }

    @Override
    protected boolean onDeleteItem(Context context, long[] itemIds)
    {
        API.deleteCurrencies(context, itemIds);
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        Uri uri = CurrenciesProvider.uriCurrency(getActivity(), itemId);
        String[] projection = new String[]{Tables.Currencies.T_ID, Tables.Currencies.CODE, Tables.Currencies.EXCHANGE_RATE};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected void bindItem(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            // Find indexes
            final int iId = c.getColumnIndex(Tables.Currencies.ID);
            final int iCode = c.getColumnIndex(Tables.Currencies.CODE);
            final int iExchangeRate = c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE);

            final String code = c.getString(iCode);

            // Set values
            code_TV.setText(code);
            format_TV.setText(AmountUtils.formatAmount(getActivity(), c.getLong(iId), 1000.00));
            exchangeRate_TV.setText(getString(R.string.f_exchange_rate_x, c.getDouble(iExchangeRate)));

            updateDefaultCurrency();
        }
    }

    private void bindAccounts(Cursor c)
    {
        // Remove accounts views
        container_V.removeAllViews();

        if (c != null && c.moveToFirst())
        {
            accounts_TV.setText(R.string.l_currency_used);

            // Add accounts views
            final int iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
            View v;
            do
            {
                v = LayoutInflater.from(getActivity()).inflate(R.layout.li_account_simple, container_V, false);
                ((TextView) v.findViewById(R.id.title_TV)).setText(c.getString(iTitle));
                container_V.addView(v);
            }
            while (c.moveToNext());
        }
        else
        {
            accounts_TV.setText(R.string.l_currency_not_used);
        }
    }

    private void bindDefaultCurrency(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            final int iCode = c.getColumnIndex(Tables.Currencies.CODE);

            defaultCurrencyCode = c.getString(iCode);
            updateDefaultCurrency();
        }
    }

    private void updateDefaultCurrency()
    {
        if (TextUtils.isEmpty(code_TV.getText()) || TextUtils.isEmpty(defaultCurrencyCode))
            return;

        if (code_TV.getText().toString().equalsIgnoreCase(defaultCurrencyCode))
        {
            currentCurrency_TV.setText(getString(R.string.this_is_main_currency));
            exchangeRate_TV.setVisibility(View.GONE);
        }
        else
        {
            currentCurrency_TV.setText(getString(R.string.f_current_main_currency_x, defaultCurrencyCode));
            exchangeRate_TV.setVisibility(View.VISIBLE);
        }
    }
}