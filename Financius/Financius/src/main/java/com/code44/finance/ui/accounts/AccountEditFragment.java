package com.code44.finance.ui.accounts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.ui.ItemEditFragment;
import com.code44.finance.ui.currencies.CurrencyListActivity;
import com.code44.finance.ui.currencies.CurrencyListFragment;
import com.code44.finance.ui.transactions.CalculatorActivity;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.AnimUtils;

public class AccountEditFragment extends ItemEditFragment implements View.OnClickListener
{
    private static final int LOADER_DEFAULT_CURRENCY = 1;
    // -----------------------------------------------------------------------------------------------------------------
    private static final int REQUEST_CURRENCY = 1;
    private static final int REQUEST_BALANCE = 2;
    // -----------------------------------------------------------------------------------------------------------------
    private static final String STATE_TITLE = "STATE_TITLE";
    private static final String STATE_CURRENCY_ID = "STATE_CURRENCY_ID";
    private static final String STATE_CURRENCY_CODE = "STATE_CURRENCY_CODE";
    private static final String STATE_BALANCE = "STATE_BALANCE";
    private static final String STATE_SHOW_IN_TOTALS = "STATE_SHOW_IN_TOTALS";
    private static final String STATE_SHOW_IN_SELECTION = "STATE_SHOW_IN_SELECTION";
    private static final String STATE_NOTE = "STATE_NOTE";
    private EditText title_ET;
    private Button currency_B;
    private Button balance_B;
    private CheckBox includeInTotals_CB;
    private CheckBox showInSelection_CB;
    private EditText note_ET;
    // -----------------------------------------------------------------------------------------------------------------
    private long currencyId;

    public static AccountEditFragment newInstance(long itemId)
    {
        AccountEditFragment f = new AccountEditFragment();
        f.setArguments(makeArgs(itemId));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_account_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_ET = (EditText) view.findViewById(R.id.title_ET);
        currency_B = (Button) view.findViewById(R.id.currency_B);
        balance_B = (Button) view.findViewById(R.id.balance_B);
        includeInTotals_CB = (CheckBox) view.findViewById(R.id.includeInBalance_CB);
        showInSelection_CB = (CheckBox) view.findViewById(R.id.showInSelection_CB);
        note_ET = (EditText) view.findViewById(R.id.note_ET);

        // Setup
        currency_B.setOnClickListener(this);
        balance_B.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Loader
        if (itemId == 0)
            getLoaderManager().initLoader(LOADER_DEFAULT_CURRENCY, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_TITLE, getTitle());
        outState.putLong(STATE_CURRENCY_ID, getCurrencyId());
        outState.putString(STATE_CURRENCY_CODE, getCurrencyCode());
        outState.putDouble(STATE_BALANCE, getBalance());
        outState.putBoolean(STATE_SHOW_IN_TOTALS, isShowInTotals());
        outState.putBoolean(STATE_SHOW_IN_SELECTION, isShowInSelection());
        outState.putString(STATE_NOTE, getNote());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_CURRENCY:
                    final long currencyId = data.getLongExtra(CurrencyListFragment.RESULT_EXTRA_ITEM_ID, 0);
                    final String currencyCode = data.getStringExtra(CurrencyListFragment.RESULT_EXTRA_CODE);
                    setCurrency(currencyId, currencyCode);
                    break;

                case REQUEST_BALANCE:
                    setBalance(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_AMOUNT, 0));
                    break;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_DEFAULT_CURRENCY:
                Uri uri = CurrenciesProvider.uriCurrencies();
                String[] projection = {Tables.Currencies.T_ID, Tables.Currencies.CODE};
                String selection = Tables.Currencies.IS_DEFAULT + "=?";
                String[] selectionArgs = new String[]{"1"};
                String sortOrder = null;

                //noinspection ConstantConditions
                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
        }
        return super.onCreateLoader(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_DEFAULT_CURRENCY:
                bindDefaultCurrency(cursor);
                cursorLoader.abandon();
                break;
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.accountType_B:
                // TODO Implement Account type selection
                break;

            case R.id.currency_B:
                CurrencyListActivity.startListSelection(getActivity(), this, REQUEST_CURRENCY);
                break;

            case R.id.balance_B:
                CalculatorActivity.startCalculator(this, REQUEST_BALANCE, getBalance(), true, true);
                break;
        }
    }

    @Override
    public boolean onSave(Context context, long itemId)
    {
        // Check values
        if (TextUtils.isEmpty(getTitle()))
        {
            AnimUtils.shake(title_ET);
            return false;
        }

        if (currencyId <= 0)
        {
            AnimUtils.shake(currency_B);
            return false;
        }

        if (itemId == 0)
            API.createAccount(context, getCurrencyId(), getTypeResName(), getTitle(), getNote(), getBalance(), getOverdraft(), isShowInTotals(), isShowInSelection());
        else
            API.updateAccount(context, itemId, getCurrencyId(), getTypeResName(), getTitle(), getNote(), getBalance(), getOverdraft(), isShowInTotals(), isShowInSelection());

        return true;
    }

    @Override
    public boolean onDiscard()
    {
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        Uri uri = AccountsProvider.uriAccount(itemId);
        String[] projection = {Tables.Accounts.T_ID, Tables.Accounts.TYPE_RES_NAME, Tables.Accounts.TITLE, Tables.Accounts.CURRENCY_ID, Tables.Currencies.CODE, Tables.Accounts.BALANCE, Tables.Accounts.OVERDRAFT, Tables.Accounts.SHOW_IN_TOTALS, Tables.Accounts.SHOW_IN_SELECTION, Tables.Accounts.NOTE};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected boolean bindItem(Cursor c, boolean isDataLoaded)
    {
        if (!isDataLoaded && c != null && c.moveToFirst())
        {
            // Get values
            final String typeResName = c.getString(c.getColumnIndex(Tables.Accounts.TYPE_RES_NAME));
            final String title = c.getString(c.getColumnIndex(Tables.Accounts.TITLE));
            final long currencyId = c.getLong(c.getColumnIndex(Tables.Accounts.CURRENCY_ID));
            final String currencyCode = c.getString(c.getColumnIndex(Tables.Currencies.CODE));
            final double balance = c.getDouble(c.getColumnIndex(Tables.Accounts.BALANCE));
            final double overdraft = c.getDouble(c.getColumnIndex(Tables.Accounts.OVERDRAFT));
            final boolean includeInTotals = c.getInt(c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS)) != 0;
            final boolean showInSelection = c.getInt(c.getColumnIndex(Tables.Accounts.SHOW_IN_SELECTION)) != 0;
            final String note = c.getString(c.getColumnIndex(Tables.Accounts.NOTE));

            // Set values
            setTypeResName(typeResName);
            setTitle(title);
            setCurrency(currencyId, currencyCode);
            setBalance(balance);
            setOverdraft(overdraft);
            setShowInTotals(includeInTotals);
            setShowInSelection(showInSelection);
            setNote(note);

            return true;
        }

        return isDataLoaded;
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            setTypeResName(savedInstanceState.getString(STATE_TYPE_RES_NAME));
            setTitle(savedInstanceState.getString(STATE_TITLE));
            setCurrency(savedInstanceState.getLong(STATE_CURRENCY_ID), savedInstanceState.getString(STATE_CURRENCY_CODE));
            setBalance(savedInstanceState.getDouble(STATE_BALANCE));
            setOverdraft(savedInstanceState.getDouble(STATE_OVERDRAFT));
            setShowInTotals(savedInstanceState.getBoolean(STATE_SHOW_IN_TOTALS));
            setShowInSelection(savedInstanceState.getBoolean(STATE_SHOW_IN_SELECTION));
            setNote(savedInstanceState.getString(STATE_NOTE));
        }
        else if (itemId == 0)
        {
            setTypeResName(getResources().getResourceName(R.string.ac_other));
            setTitle(null);
            // TODO Set main currency by default. No need to use loader, because we have default currency in helper singleton.
            setCurrency(0, null);
            setBalance(0);
            setOverdraft(0);
            setShowInTotals(true);
            setShowInSelection(true);
            setNote(null);
        }
    }

    private void bindDefaultCurrency(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            setCurrency(c.getLong(c.getColumnIndex(Tables.Currencies.ID)), c.getString(c.getColumnIndex(Tables.Currencies.CODE)));
        }
    }

    private long getCurrencyId()
    {
        return currencyId;
    }

    private String getCurrencyCode()
    {
        //noinspection ConstantConditions
        return currency_B.getText().toString();
    }

    private void setCurrency(long currencyId, String currencyCode)
    {
        this.currencyId = currencyId;
        currency_B.setText(currencyCode);
    }

    private String getTypeResName()
    {
        // TODO return actual value
        return getResources().getResourceEntryName(R.string.ac_other);
    }

    private void setTypeResName(@SuppressWarnings("UnusedParameters") String typeResName)
    {
        // TODO Implement
    }

    private String getTitle()
    {
        //noinspection ConstantConditions
        return title_ET.getText().toString();
    }

    private void setTitle(String title)
    {
        title_ET.setText(title);
    }

    private String getNote()
    {
        //noinspection ConstantConditions
        return note_ET.getText().toString();
    }

    private void setNote(String note)
    {
        note_ET.setText(note);
    }

    private double getBalance()
    {
        //noinspection ConstantConditions
        return AmountUtils.getAmount(balance_B.getText().toString());
    }

    private void setBalance(double balance)
    {
        if (balance == 0)
        {
            balance_B.setText(null);
        }
        else
        {
            balance_B.setText(AmountUtils.formatAmount(balance));
            balance_B.setTextColor(AmountUtils.getBalanceColor(getActivity(), balance));
        }
    }

    private double getOverdraft()
    {
        //noinspection ConstantConditions
        return AmountUtils.getAmount(overdraft_B.getText().toString());
    }

    private void setOverdraft(double overdraft)
    {
        overdraft_B.setText(AmountUtils.formatAmount(overdraft));
    }

    private boolean isShowInTotals()
    {
        return includeInTotals_CB.isChecked();
    }

    private void setShowInTotals(boolean showInTotals)
    {
        includeInTotals_CB.setChecked(showInTotals);
    }

    private boolean isShowInSelection()
    {
        return showInSelection_CB.isChecked();
    }

    private void setShowInSelection(boolean showInSelection)
    {
        showInSelection_CB.setChecked(showInSelection);
    }
}