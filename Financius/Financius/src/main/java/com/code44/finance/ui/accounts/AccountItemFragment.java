package com.code44.finance.ui.accounts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.ui.ItemFragment;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;

public class AccountItemFragment extends ItemFragment
{
    private static final int LOADER_TRANSACTIONS = 1;
    private static final int LOADER_TRANSACTIONS_EXPENSE = 2;
    private static final int LOADER_TRANSACTIONS_INCOME = 3;
    private TextView title_TV;
    private TextView balance_TV;
    private TextView note_TV;
    private TextView includeInTotals_TV;
    private TextView showInSelection_TV;
    private TextView lastUsed_TV;
    private TextView timesUsed_TV;
    private TextView avgExpense_TV;
    private TextView avgIncome_TV;

    public static AccountItemFragment newInstance(long itemId)
    {
        final AccountItemFragment f = new AccountItemFragment();
        f.setArguments(makeArgs(itemId));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_TV = (TextView) view.findViewById(R.id.title_TV);
        balance_TV = (TextView) view.findViewById(R.id.balance_TV);
        note_TV = (TextView) view.findViewById(R.id.note_TV);
        includeInTotals_TV = (TextView) view.findViewById(R.id.includeInTotals_TV);
        showInSelection_TV = (TextView) view.findViewById(R.id.showInSelection_TV);
        lastUsed_TV = (TextView) view.findViewById(R.id.lastUsed_TV);
        timesUsed_TV = (TextView) view.findViewById(R.id.timesUsed_TV);
        avgExpense_TV = (TextView) view.findViewById(R.id.avgExpense_TV);
        avgIncome_TV = (TextView) view.findViewById(R.id.avgIncome_TV);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
        getLoaderManager().initLoader(LOADER_TRANSACTIONS_EXPENSE, null, this);
        getLoaderManager().initLoader(LOADER_TRANSACTIONS_INCOME, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_TRANSACTIONS:
            {
                Uri uri = TransactionsProvider.uriTransactions(getActivity());
                String[] projection = new String[] {"max(" + Tables.Transactions.DATE + ")", "count(distinct " + Tables.Transactions.T_ID + ")"};
                String selection = "(" + Tables.Transactions.ACCOUNT_FROM_ID + "=? or " + Tables.Transactions.ACCOUNT_TO_ID + "=?) and " + Tables.Transactions.STATE + "=? and " + Tables.Transactions.DELETE_STATE + "=? and " + Tables.Transactions.SHOW_IN_TOTALS + "=?";
                String[] selectionArgs = new String[]{String.valueOf(itemId), String.valueOf(itemId), String.valueOf(Tables.Transactions.State.CONFIRMED), String.valueOf(Tables.DeleteState.NONE), "1"};
                String sortOrder = null;

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
            }

            case LOADER_TRANSACTIONS_EXPENSE:
            {
                Uri uri = TransactionsProvider.uriTransactions(getActivity());
                String[] projection = new String[] {"avg(" + Tables.Transactions.AMOUNT + " * " + Tables.Currencies.CurrencyFrom.T_EXCHANGE_RATE + ")"};
                String selection = Tables.Transactions.ACCOUNT_FROM_ID + "=? and " + Tables.Transactions.STATE + "=? and " + Tables.Transactions.DELETE_STATE + "=? and " + Tables.Transactions.SHOW_IN_TOTALS + "=?";
                String[] selectionArgs = new String[]{String.valueOf(itemId), String.valueOf(Tables.Transactions.State.CONFIRMED), String.valueOf(Tables.DeleteState.NONE), "1"};
                String sortOrder = null;

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
            }

            case LOADER_TRANSACTIONS_INCOME:
            {
                Uri uri = TransactionsProvider.uriTransactions(getActivity());
                String[] projection = new String[] {"avg(" + Tables.Transactions.AMOUNT + " * " + Tables.Currencies.CurrencyTo.T_EXCHANGE_RATE + ")"};
                String selection = Tables.Transactions.ACCOUNT_TO_ID + "=? and " + Tables.Transactions.STATE + "=? and " + Tables.Transactions.DELETE_STATE + "=? and " + Tables.Transactions.SHOW_IN_TOTALS + "=?";
                String[] selectionArgs = new String[]{String.valueOf(itemId), String.valueOf(Tables.Transactions.State.CONFIRMED), String.valueOf(Tables.DeleteState.NONE), "1"};
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
            case LOADER_TRANSACTIONS:
                bindTransactions(cursor);
                break;

            case LOADER_TRANSACTIONS_EXPENSE:
                bindTransactionsExpense(cursor);
                break;

            case LOADER_TRANSACTIONS_INCOME:
                bindTransactionsIncome(cursor);
                break;
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    protected void startItemEdit(Context context, long itemId, View expandFrom)
    {
        AccountEditActivity.startItemEdit(context, itemId);
    }

    @Override
    protected boolean onDeleteItem(Context context, long[] itemIds)
    {
        API.deleteAccounts(context, itemIds);
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        Uri uri = AccountsProvider.uriAccount(getActivity(), itemId);
        String[] projection = new String[]{Tables.Accounts.CURRENCY_ID, Tables.Accounts.TITLE, Tables.Accounts.NOTE, Tables.Accounts.BALANCE, Tables.Accounts.SHOW_IN_TOTALS, Tables.Accounts.SHOW_IN_SELECTION};
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
            final int iCurrencyId = c.getColumnIndex(Tables.Accounts.CURRENCY_ID);
            final int iTitle = c.getColumnIndex(Tables.Accounts.TITLE);
            final int iBalance = c.getColumnIndex(Tables.Accounts.BALANCE);
            final int iNote = c.getColumnIndex(Tables.Accounts.NOTE);
            final int iShowInTotals = c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS);
            final int iShowInSelection = c.getColumnIndex(Tables.Accounts.SHOW_IN_SELECTION);

            // Set values
            final String checkMark = "\u2714";
            final String crossMark = "\u2717";
            final String note = c.getString(iNote);
            title_TV.setText(c.getString(iTitle));
            balance_TV.setText(AmountUtils.formatAmount(getActivity(), c.getLong(iCurrencyId), c.getDouble(iBalance)));
            balance_TV.setTextColor(AmountUtils.getBalanceColor(getActivity(), c.getDouble(iBalance)));
            if (TextUtils.isEmpty(note))
            {
                note_TV.setVisibility(View.GONE);
            }
            else
            {
                note_TV.setVisibility(View.VISIBLE);
                note_TV.setText(note);
            }
            includeInTotals_TV.setText(c.getInt(iShowInTotals) != 0 ? checkMark : crossMark);
            showInSelection_TV.setText(c.getInt(iShowInSelection) != 0 ? checkMark : crossMark);
        }
    }

    private void bindTransactions(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            final long lastUsedDate = c.getLong(0);
            lastUsed_TV.setText(lastUsedDate == 0 ? getString(R.string.never) : DateUtils.getRelativeTimeSpanString(c.getLong(0), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS));
            timesUsed_TV.setText(c.getString(1));
        }
    }

    private void bindTransactionsExpense(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            avgExpense_TV.setText(AmountUtils.formatAmount(getActivity(), CurrenciesHelper.getDefault(getActivity()).getMainCurrencyId(), c.getDouble(0)));
        }
    }

    private void bindTransactionsIncome(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            avgIncome_TV.setText(AmountUtils.formatAmount(getActivity(), CurrenciesHelper.getDefault(getActivity()).getMainCurrencyId(), c.getDouble(0)));
        }
    }
}