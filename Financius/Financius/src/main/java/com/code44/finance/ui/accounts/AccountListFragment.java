package com.code44.finance.ui.accounts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.adapters.AbstractCursorAdapter;
import com.code44.finance.adapters.AccountsAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.ui.ItemListFragment;
import com.code44.finance.ui.MainActivity;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrencyHelper;

public class AccountListFragment extends ItemListFragment implements MainActivity.NavigationContentFragment
{
    public static final String RESULT_EXTRA_TITLE = AccountListFragment.class.getName() + ".RESULT_EXTRA_TITLE";
    public static final String RESULT_EXTRA_CURRENCY_ID = AccountListFragment.class.getName() + ".RESULT_EXTRA_CURRENCY_ID";
    public static final String RESULT_EXTRA_CURRENCY_CODE = AccountListFragment.class.getName() + ".RESULT_EXTRA_CURRENCY_CODE";
    public static final String RESULT_EXTRA_CURRENCY_EXCHANGE_RATE = AccountListFragment.class.getName() + ".RESULT_EXTRA_CURRENCY_EXCHANGE_RATE";
    private TextView balance_TV;

    public static AccountListFragment newInstance(int selectionType)
    {
        final AccountListFragment f = new AccountListFragment();
        f.setArguments(makeArgs(selectionType, null));
        return f;
    }

    public static Loader<Cursor> createItemsLoader(Context context, int selectionType)
    {
        final Uri uri = AccountsProvider.uriAccounts();
        final String[] projection = new String[]{Tables.Accounts.T_ID, Tables.Accounts.TITLE, Tables.Accounts.BALANCE, Tables.Accounts.CURRENCY_ID, Tables.Accounts.SHOW_IN_TOTALS, Tables.Currencies.CODE, Tables.Currencies.EXCHANGE_RATE};
        final String selection = Tables.Accounts.ORIGIN + "<>? and " + Tables.Accounts.DELETE_STATE + "=?" + (selectionType != SELECTION_TYPE_NONE ? " and " + Tables.Accounts.SHOW_IN_SELECTION + "=?" : "");
        final String[] selectionArgs = selectionType != SELECTION_TYPE_NONE ? new String[]{String.valueOf(Tables.Categories.Origin.SYSTEM), String.valueOf(Tables.DeleteState.NONE), "1"} : new String[]{String.valueOf(Tables.Categories.Origin.SYSTEM), String.valueOf(Tables.DeleteState.NONE)};
        final String sortOrder = Tables.Accounts.TITLE;

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_account_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final View separator_V = view.findViewById(R.id.separator_V);
        balance_TV = (TextView) view.findViewById(R.id.balance_TV);
        final View balanceContainer_V = view.findViewById(R.id.container_V);

        // Setup
        if (selectionType != SELECTION_TYPE_NONE)
        {
            separator_V.setVisibility(View.GONE);
            balanceContainer_V.setVisibility(View.GONE);
        }
    }

    @Override
    public String getTitle()
    {
        return getString(R.string.accounts);
    }

    @Override
    protected AbstractCursorAdapter createAdapter(Context context)
    {
        return new AccountsAdapter(context);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return createItemsLoader(getActivity(), selectionType);
    }

    @Override
    protected void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras)
    {
        outExtras.putString(RESULT_EXTRA_TITLE, c.getString(c.getColumnIndex(Tables.Accounts.TITLE)));
        outExtras.putLong(RESULT_EXTRA_CURRENCY_ID, c.getLong(c.getColumnIndex(Tables.Accounts.CURRENCY_ID)));
        outExtras.putString(RESULT_EXTRA_CURRENCY_CODE, c.getString(c.getColumnIndex(Tables.Currencies.CODE)));
        outExtras.putDouble(RESULT_EXTRA_CURRENCY_EXCHANGE_RATE, c.getDouble(c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE)));
    }

    @Override
    protected void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c, View view)
    {
        AccountItemActivity.startItem(context, position);
    }

    @Override
    protected void startItemCreate(Context context, View view)
    {
        AccountEditActivity.startItemEdit(context, 0);
    }

    @Override
    protected void bindItems(Cursor c)
    {
        if (selectionType == SELECTION_TYPE_NONE)
        {
            // Find total balance
            final long mainCurrencyId = CurrencyHelper.get().getMainCurrencyId();
            double balance = 0;
            if (c != null && c.moveToFirst())
            {
                final int iBalance = c.getColumnIndex(Tables.Accounts.BALANCE);
                final int iIncludeInTotals = c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS);
                final int iCurrencyId = c.getColumnIndex(Tables.Accounts.CURRENCY_ID);
                final int iExchangeRate = c.getColumnIndex(Tables.Currencies.EXCHANGE_RATE);

                do
                {
                    if (c.getInt(iIncludeInTotals) != 0)
                        balance += c.getLong(iCurrencyId) != mainCurrencyId ? c.getDouble(iBalance) * c.getDouble(iExchangeRate) : c.getDouble(iBalance);
                }
                while (c.moveToNext());
            }

            // Set values
            balance_TV.setText(AmountUtils.formatAmount(mainCurrencyId, balance));
            balance_TV.setTextColor(AmountUtils.getBalanceColor(getActivity(), balance));
        }
        super.bindItems(c);
    }
}