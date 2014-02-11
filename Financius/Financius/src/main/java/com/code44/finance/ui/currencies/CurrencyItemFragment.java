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
import com.code44.finance.views.cards.CurrencyAccountCardView;

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_DEFAULT_CURRENCY, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_ACCOUNTS:
            {
                Uri uri = AccountsProvider.uriAccounts();
                String[] projection = new String[]{Tables.Accounts.T_ID, Tables.Accounts.TITLE, Tables.Accounts.CURRENCY_ID, Tables.Currencies.CODE};
                String selection = Tables.Accounts.DELETE_STATE + "=? and " + Tables.Accounts.ORIGIN + "<>?";
                String[] selectionArgs = new String[]{String.valueOf(Tables.DeleteState.NONE), String.valueOf(Tables.Accounts.Origin.SYSTEM)};

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
            }

            case LOADER_DEFAULT_CURRENCY:
            {
                Uri uri = CurrenciesProvider.uriCurrencies();
                String[] projection = new String[]{Tables.Currencies.CODE};
                String selection = Tables.Currencies.IS_DEFAULT + "=?";
                String[] selectionArgs = new String[]{"1"};

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
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
        API.deleteCurrencies(itemIds);
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        Uri uri = CurrenciesProvider.uriCurrency(itemId);
        String[] projection = new String[]{Tables.Currencies.T_ID, Tables.Currencies.CODE, Tables.Currencies.EXCHANGE_RATE};

        return new CursorLoader(getActivity(), uri, projection, null, null, null);
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
            format_TV.setText(AmountUtils.formatAmount(c.getLong(iId), 1000.00));
            exchangeRate_TV.setText("\u21C4 " + c.getDouble(iExchangeRate));

            updateDefaultCurrency();
        }

        getLoaderManager().restartLoader(LOADER_ACCOUNTS, null, this);
    }

    private void bindAccounts(Cursor c)
    {
        // Remove accounts views
        container_V.removeAllViews();

        if (c != null && c.moveToFirst())
        {
            // Add accounts views
            final int iId = c.getColumnIndex(Tables.Accounts.ID);
            final int iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
            final int iCurrencyId = c.getColumnIndex(Tables.Accounts.CURRENCY_ID);
            final int iCurrencyCode = c.getColumnIndex(Tables.Currencies.CODE);
            do
            {
                final CurrencyAccountCardView card_V = new CurrencyAccountCardView(getActivity());
                final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
                card_V.setLayoutParams(lp);
                //noinspection ConstantConditions
                card_V.setData(itemId, code_TV.getText().toString(), c.getLong(iId), c.getString(iTitle), c.getLong(iCurrencyId), c.getString(iCurrencyCode));
                container_V.addView(card_V);
            }
            while (c.moveToNext());
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

        //noinspection ConstantConditions
        if (code_TV.getText().toString().equalsIgnoreCase(defaultCurrencyCode))
        {
            currentCurrency_TV.setVisibility(View.GONE);
            exchangeRate_TV.setVisibility(View.GONE);
        }
        else
        {
            currentCurrency_TV.setText(defaultCurrencyCode);
            currentCurrency_TV.setVisibility(View.VISIBLE);
            exchangeRate_TV.setVisibility(View.VISIBLE);
        }
    }
}