package com.code44.finance.ui.transactions;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.db.model.Account;
import com.code44.finance.db.model.Category;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.services.AbstractService;
import com.code44.finance.services.CurrenciesRestService;
import com.code44.finance.ui.ItemEditFragment;
import com.code44.finance.ui.accounts.AccountListActivity;
import com.code44.finance.ui.accounts.AccountListFragment;
import com.code44.finance.ui.categories.CategoryListActivity;
import com.code44.finance.ui.categories.CategoryListFragment;
import com.code44.finance.ui.dialogs.DateTimeDialog;
import com.code44.finance.utils.AnimUtils;
import com.code44.finance.utils.CurrencyHelper;
import com.code44.finance.utils.TransactionAutoHelper;
import com.code44.finance.utils.TransactionsUtils;
import com.code44.finance.views.cards.*;
import de.greenrobot.event.EventBus;

public class TransactionEditFragment extends ItemEditFragment implements View.OnClickListener, DateTimeDialog.DialogCallbacks, AmountCardView.Callback
{
    private static final String STATE_DATE = "STATE_DATE";
    private static final String STATE_ACCOUNT_FROM_ID = "STATE_ACCOUNT_FROM_ID";
    private static final String STATE_ACCOUNT_FROM_TITLE = "STATE_ACCOUNT_FROM_TITLE";
    private static final String STATE_ACCOUNT_FROM_CURRENCY_ID = "STATE_ACCOUNT_FROM_CURRENCY_ID";
    private static final String STATE_ACCOUNT_FROM_CURRENCY_CODE = "STATE_ACCOUNT_FROM_CURRENCY_CODE";
    private static final String STATE_ACCOUNT_FROM_CURRENCY_EXCHANGE_RATE = "STATE_ACCOUNT_FROM_CURRENCY_EXCHANGE_RATE";
    private static final String STATE_ACCOUNT_TO_ID = "STATE_ACCOUNT_TO_ID";
    private static final String STATE_ACCOUNT_TO_TITLE = "STATE_ACCOUNT_TO_TITLE";
    private static final String STATE_ACCOUNT_TO_CURRENCY_ID = "STATE_ACCOUNT_TO_CURRENCY_ID";
    private static final String STATE_ACCOUNT_TO_CURRENCY_CODE = "STATE_ACCOUNT_TO_CURRENCY_CODE";
    private static final String STATE_ACCOUNT_TO_CURRENCY_EXCHANGE_RATE = "STATE_ACCOUNT_TO_CURRENCY_EXCHANGE_RATE";
    private static final String STATE_EXCHANGE_RATE = "STATE_EXCHANGE_RATE";
    private static final String STATE_CATEGORY_ID = "STATE_CATEGORY_ID";
    private static final String STATE_CATEGORY_TITLE = "STATE_CATEGORY_TITLE";
    private static final String STATE_CATEGORY_COLOR = "STATE_CATEGORY_COLOR";
    private static final String STATE_CATEGORY_TYPE = "STATE_CATEGORY_TYPE";
    private static final String STATE_EXPENSE_CATEGORY_ID = "STATE_EXPENSE_CATEGORY_ID";
    private static final String STATE_EXPENSE_CATEGORY_TITLE = "STATE_EXPENSE_CATEGORY_TITLE";
    private static final String STATE_EXPENSE_CATEGORY_COLOR = "STATE_EXPENSE_CATEGORY_COLOR";
    private static final String STATE_INCOME_CATEGORY_ID = "STATE_INCOME_CATEGORY_ID";
    private static final String STATE_INCOME_CATEGORY_TITLE = "STATE_INCOME_CATEGORY_TITLE";
    private static final String STATE_INCOME_CATEGORY_COLOR = "STATE_INCOME_CATEGORY_COLOR";
    private static final String STATE_AMOUNT = "STATE_AMOUNT";
    private static final String STATE_NOTE = "STATE_NOTE";
    private static final String STATE_STATE = "STATE_STATE";
    private static final String STATE_SHOW_IN_TOTALS = "STATE_SHOW_IN_TOTALS";
    private static final String STATE_USER_SET_ACCOUNT_FROM = "STATE_USER_SET_ACCOUNT_FROM";
    private static final String STATE_USER_SET_ACCOUNT_TO = "STATE_USER_SET_ACCOUNT_TO";
    private static final String STATE_USER_SET_CATEGORY_INCOME = "STATE_USER_SET_CATEGORY_INCOME";
    private static final String STATE_USER_SET_CATEGORY_EXPENSE = "STATE_USER_SET_CATEGORY_EXPENSE";
    private static final String STATE_AUTO_TRANSACTION_SET_FOR_TYPE = "STATE_AUTO_TRANSACTION_SET_FOR_TYPE";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String FRAGMENT_DATE_TIME = "FRAGMENT_DATE_TIME";
    // -----------------------------------------------------------------------------------------------------------------
    private static final int REQUEST_DATE = 1;
    private static final int REQUEST_TIME = 2;
    private static final int REQUEST_ACCOUNT_FROM = 3;
    private static final int REQUEST_ACCOUNT_TO = 4;
    private static final int REQUEST_EXCHANGE_RATE = 5;
    private static final int REQUEST_CATEGORY = 6;
    private static final int REQUEST_AMOUNT = 7;
    private static final int REQUEST_AMOUNT_TO = 8;
    // -----------------------------------------------------------------------------------------------------------------
    private static final int LOADER_ACCOUNTS = 1;
    private static final int LOADER_CATEGORIES = 2;
    private static final int LOADER_TRANSACTIONS = 3;
    // -----------------------------------------------------------------------------------------------------------------
    private final TransactionAutoHelper transactionAutoHelper = TransactionAutoHelper.getInstance();
    // -----------------------------------------------------------------------------------------------------------------
    private AmountCardView amount_CV;
    private AccountCardView accountFrom_CV;
    private AccountCardView accountTo_CV;
    private View accountsSeparator_V;
    private CategoryCardView category_CV;
    private EditTextCardView note_CV;
    private DateCardView date_CV;
    private CheckBox confirmed_CB;
    private CheckBox showInTotals_CB;
    // -----------------------------------------------------------------------------------------------------------------
    private int categoryType;
    private long expenseCategoryId;
    private String expenseCategoryTitle;
    private int expenseCategoryColor;
    private long incomeCategoryId;
    private String incomeCategoryTitle;
    private int incomeCategoryColor;
    private boolean userSetAccountFrom;
    private boolean userSetAccountTo;
    private boolean userSetCategoryIncome;
    private boolean userSetCategoryExpense;
    private boolean autoTransactionSetForType = false;
    private boolean doingAutoTransaction = false;

    public static TransactionEditFragment newInstance(long itemId)
    {
        TransactionEditFragment f = new TransactionEditFragment();
        f.setArguments(makeArgs(itemId));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_transaction_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        amount_CV = (AmountCardView) view.findViewById(R.id.amount_CV);
        accountFrom_CV = (AccountCardView) view.findViewById(R.id.accountFrom_CV);
        accountTo_CV = (AccountCardView) view.findViewById(R.id.accountTo_CV);
        accountsSeparator_V = view.findViewById(R.id.accountsSeparator_TV);
        category_CV = (CategoryCardView) view.findViewById(R.id.category_CV);
        note_CV = (EditTextCardView) view.findViewById(R.id.note_CV);
        date_CV = (DateCardView) view.findViewById(R.id.date_CV);
        confirmed_CB = (CheckBox) view.findViewById(R.id.confirmed_CB);
        showInTotals_CB = (CheckBox) view.findViewById(R.id.showInTotals_CB);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        amount_CV.setOnClickListener(this);
        amount_CV.setCallback(this);
        accountFrom_CV.setOnClickListener(this);
        accountTo_CV.setOnClickListener(this);
        accountsSeparator_V.setOnClickListener(this);
        category_CV.setOnClickListener(this);
        date_CV.setOnClickListener(this);

        // Restore date time dialog fragment
        final DateTimeDialog dateTime_F = (DateTimeDialog) getFragmentManager().findFragmentByTag(FRAGMENT_DATE_TIME);
        if (dateTime_F != null)
            dateTime_F.setListener(this);

        // Loaders
        if (itemId == 0)
        {
            getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
            getLoaderManager().initLoader(LOADER_CATEGORIES, null, this);
            getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Register events
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Unregister events
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Reset listener for date time dialog
        final DateTimeDialog dateTime_F = (DateTimeDialog) getFragmentManager().findFragmentByTag(FRAGMENT_DATE_TIME);
        if (dateTime_F != null)
            dateTime_F.setListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_DATE, getDate());
        outState.putLong(STATE_ACCOUNT_FROM_ID, getAccountFromId());
        outState.putString(STATE_ACCOUNT_FROM_TITLE, getAccountFromTitle());
        outState.putLong(STATE_ACCOUNT_FROM_CURRENCY_ID, getAccountFromCurrencyId());
        outState.putString(STATE_ACCOUNT_FROM_CURRENCY_CODE, getAccountFromCurrencyCode());
        outState.putDouble(STATE_ACCOUNT_FROM_CURRENCY_EXCHANGE_RATE, getAccountFromCurrencyExchangeRate());
        outState.putLong(STATE_ACCOUNT_TO_ID, getAccountToId());
        outState.putString(STATE_ACCOUNT_TO_TITLE, getAccountToTitle());
        outState.putLong(STATE_ACCOUNT_TO_CURRENCY_ID, getAccountToCurrencyId());
        outState.putString(STATE_ACCOUNT_TO_CURRENCY_CODE, getAccountToCurrencyCode());
        outState.putDouble(STATE_ACCOUNT_TO_CURRENCY_EXCHANGE_RATE, getAccountToCurrencyExchangeRate());
        outState.putDouble(STATE_EXCHANGE_RATE, getExchangeRate());
        outState.putLong(STATE_CATEGORY_ID, getCategoryId());
        outState.putString(STATE_CATEGORY_TITLE, getCategoryTitle());
        outState.putInt(STATE_CATEGORY_COLOR, getCategoryColor());
        outState.putInt(STATE_CATEGORY_TYPE, getCategoryType());
        outState.putLong(STATE_EXPENSE_CATEGORY_ID, expenseCategoryId);
        outState.putString(STATE_EXPENSE_CATEGORY_TITLE, expenseCategoryTitle);
        outState.putInt(STATE_EXPENSE_CATEGORY_COLOR, expenseCategoryColor);
        outState.putLong(STATE_INCOME_CATEGORY_ID, incomeCategoryId);
        outState.putString(STATE_INCOME_CATEGORY_TITLE, incomeCategoryTitle);
        outState.putInt(STATE_INCOME_CATEGORY_COLOR, incomeCategoryColor);
        outState.putDouble(STATE_AMOUNT, getAmount());
        outState.putString(STATE_NOTE, getNote());
        outState.putInt(STATE_STATE, getState());
        outState.putBoolean(STATE_SHOW_IN_TOTALS, isShowInTotals());
        outState.putBoolean(STATE_USER_SET_ACCOUNT_FROM, userSetAccountFrom);
        outState.putBoolean(STATE_USER_SET_ACCOUNT_TO, userSetAccountTo);
        outState.putBoolean(STATE_USER_SET_CATEGORY_EXPENSE, userSetCategoryExpense);
        outState.putBoolean(STATE_USER_SET_CATEGORY_INCOME, userSetCategoryIncome);
        outState.putBoolean(STATE_AUTO_TRANSACTION_SET_FOR_TYPE, autoTransactionSetForType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_AMOUNT:
            {
                if (resultCode == Activity.RESULT_OK)
                    setAmount(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_AMOUNT, 0));
                break;
            }

            case REQUEST_ACCOUNT_FROM:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    userSetAccountFrom = true;
                    setAccountFrom(data.getLongExtra(AccountListFragment.RESULT_EXTRA_ITEM_ID, 0), data.getStringExtra(AccountListFragment.RESULT_EXTRA_TITLE), data.getLongExtra(AccountListFragment.RESULT_EXTRA_CURRENCY_ID, 0), data.getStringExtra(AccountListFragment.RESULT_EXTRA_CURRENCY_CODE), data.getDoubleExtra(AccountListFragment.RESULT_EXTRA_CURRENCY_EXCHANGE_RATE, 1.0));
                }
                break;
            }

            case REQUEST_ACCOUNT_TO:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    userSetAccountTo = true;
                    setAccountTo(data.getLongExtra(AccountListFragment.RESULT_EXTRA_ITEM_ID, 0), data.getStringExtra(AccountListFragment.RESULT_EXTRA_TITLE), data.getLongExtra(AccountListFragment.RESULT_EXTRA_CURRENCY_ID, 0), data.getStringExtra(AccountListFragment.RESULT_EXTRA_CURRENCY_CODE), data.getDoubleExtra(AccountListFragment.RESULT_EXTRA_CURRENCY_EXCHANGE_RATE, 1.0));
                }
                break;
            }

            case REQUEST_EXCHANGE_RATE:
            {
                if (resultCode == Activity.RESULT_OK)
                    setExchangeRate(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_AMOUNT, 0));
                break;
            }

            case REQUEST_CATEGORY:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    if (getCategoryType() == Tables.Categories.Type.EXPENSE)
                        userSetCategoryExpense = true;
                    else if (getCategoryType() == Tables.Categories.Type.INCOME)
                        userSetCategoryIncome = true;
                    setCategory(data.getLongExtra(CategoryListFragment.RESULT_EXTRA_ITEM_ID, 0), data.getStringExtra(CategoryListFragment.RESULT_EXTRA_CATEGORY_TITLE), data.getIntExtra(CategoryListFragment.RESULT_EXTRA_CATEGORY_COLOR, 0));
                }
                break;
            }

            case REQUEST_AMOUNT_TO:
            {
                if (resultCode == Activity.RESULT_OK && getExchangeRate() != 0)
                    setAmount(data.getDoubleExtra(CalculatorActivity.RESULT_EXTRA_AMOUNT, 0) / getExchangeRate());
                break;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;

        switch (id)
        {
            case LOADER_ACCOUNTS:
                uri = AccountsProvider.uriAccounts();
                projection = new String[]{Tables.Accounts.T_ID, Tables.Accounts.CURRENCY_ID, Tables.Accounts.TITLE, Tables.Currencies.EXCHANGE_RATE, Tables.Currencies.CODE};
                selection = Tables.Accounts.ORIGIN + "<>? and " + Tables.Accounts.DELETE_STATE + "=? and " + Tables.Accounts.SHOW_IN_SELECTION + "=?";
                selectionArgs = new String[]{String.valueOf(Tables.Categories.Origin.SYSTEM), String.valueOf(Tables.DeleteState.NONE), "1"};
                break;

            case LOADER_CATEGORIES:
                uri = CategoriesProvider.uriCategories();
                selection = Tables.Categories.DELETE_STATE + "=? and " + Tables.Categories.LEVEL + ">?";
                selectionArgs = new String[]{String.valueOf(Tables.DeleteState.NONE), "0"};
                break;

            case LOADER_TRANSACTIONS:
                uri = TransactionsProvider.uriTransactions();
                projection = new String[]{Tables.Transactions.T_ID, Tables.Transactions.SERVER_ID, Tables.Transactions.ACCOUNT_FROM_ID, Tables.Transactions.ACCOUNT_TO_ID, Tables.Transactions.DATE, Tables.Accounts.AccountFrom.S_TITLE,
                        Tables.Accounts.AccountTo.S_TITLE, Tables.Transactions.CATEGORY_ID, Tables.Categories.CategoriesChild.S_TITLE,
                        Tables.Categories.CategoriesChild.S_TYPE, Tables.Transactions.AMOUNT, Tables.Transactions.NOTE, Tables.Transactions.DELETE_STATE};
                selection = Tables.Transactions.STATE + "=? and " + Tables.Transactions.DELETE_STATE + "=? and " + Tables.Transactions.DATE + " between ? and ?";
                final long now = System.currentTimeMillis();
                selectionArgs = new String[]{String.valueOf(Tables.Transactions.State.CONFIRMED), String.valueOf(Tables.DeleteState.NONE), String.valueOf(now - (DateUtils.WEEK_IN_MILLIS * 12)), String.valueOf(now)};
                break;
        }

        if (uri != null)
            return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
        else
            return super.onCreateLoader(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_ACCOUNTS:
                transactionAutoHelper.setAccounts(cursor);
                cursorLoader.abandon();
                doAutoComplete();
                return;

            case LOADER_CATEGORIES:
                transactionAutoHelper.setCategories(cursor);
                cursorLoader.abandon();
                doAutoComplete();
                return;

            case LOADER_TRANSACTIONS:
                transactionAutoHelper.setTransactions(cursor);
                cursorLoader.abandon();
                doAutoComplete();
                return;
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.amount_CV:
                CalculatorActivity.startCalculator(this, REQUEST_AMOUNT, getAmount(), false, true);
                break;

            case R.id.accountFrom_CV:
                AccountListActivity.startListSelection(getActivity(), this, REQUEST_ACCOUNT_FROM);
                break;

            case R.id.accountTo_CV:
                AccountListActivity.startListSelection(getActivity(), this, REQUEST_ACCOUNT_TO);
                break;

            case R.id.accountsSeparator_TV:
                final long tempAccountId = getAccountFromId();
                final String tempAccountTitle = getAccountFromTitle();
                final long tempCurrencyId = getAccountFromCurrencyId();
                final String tempCurrencyCode = getAccountFromCurrencyCode();
                final double tempExchangeRate = getAccountFromCurrencyExchangeRate();
                setAccountFrom(getAccountToId(), getAccountToTitle(), getAccountToCurrencyId(), getAccountToCurrencyCode(), getAccountToCurrencyExchangeRate());
                setAccountTo(tempAccountId, tempAccountTitle, tempCurrencyId, tempCurrencyCode, tempExchangeRate);
                break;

            case R.id.category_CV:
                CategoryListActivity.startListSelection(getActivity(), this, REQUEST_CATEGORY, getCategoryType());
                break;

            case R.id.date_CV:
                DateTimeDialog.newDateDialogInstance(this, REQUEST_DATE, getDate()).show(getFragmentManager(), FRAGMENT_DATE_TIME);
                break;

//            case R.id.amountTo_B:
//                CalculatorActivity.startCalculator(this, REQUEST_AMOUNT_TO, getAmount() * getExchangeRate(), false, true);
//                break;
        }
    }

    @Override
    public void onDateSelected(int requestCode, long date)
    {
        setDate(date);

        if (requestCode == REQUEST_DATE)
            DateTimeDialog.newTimeDialogInstance(this, REQUEST_TIME, getDate()).show(getFragmentManager(), FRAGMENT_DATE_TIME);
    }

    @Override
    public boolean onSave(Context context, long itemId)
    {
        boolean isOK = true;

        // Check values
        if (getAccountFromId() == 0)
        {
            AnimUtils.shake(accountFrom_CV);
            isOK = false;
        }

        if (getAccountToId() == 0)
        {
            AnimUtils.shake(accountTo_CV);
            isOK = false;
        }

        if (getCategoryType() == Tables.Categories.Type.TRANSFER && getAccountFromId() == getAccountToId())
        {
            AnimUtils.shake(accountFrom_CV);
            AnimUtils.shake(accountTo_CV);
            isOK = false;
        }

        double exchangeRate = getExchangeRate();
        if (getCategoryType() == Tables.Categories.Type.TRANSFER && exchangeRate <= 0)
        {
            exchangeRate = 1.0;
        }

        if (getAmount() <= 0)
        {
            AnimUtils.shake(amount_CV);
            isOK = false;
        }

        if (isOK)
        {
            ContentValues values = TransactionsUtils.getValues(getAccountFromId(), getAccountToId(), getCategoryId(), getDate(), getAmount(), exchangeRate, getNote(), getState(), isShowInTotals());
            if (this.itemId == 0)
                API.createItem(TransactionsProvider.uriTransactions(), values);
            else
                API.updateItem(TransactionsProvider.uriTransactions(), itemId, values);
        }

        return isOK;
    }

    @Override
    public boolean onDiscard()
    {
        return true;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(CurrenciesRestService.GetExchangeRateEvent event)
    {
        if (event.getState() == AbstractService.ServiceEvent.State.SUCCEEDED && event.getFromCode().equalsIgnoreCase(getAccountFromCurrencyCode()) && event.getToCode().equalsIgnoreCase(getAccountToCurrencyCode()))
            setExchangeRate(event.getExchangeRate());
    }

    @Override
    public void onChangeCategoryType()
    {
        final int currentCategoryType = getCategoryType();
        final int newCategoryType = currentCategoryType == Tables.Categories.Type.EXPENSE ? Tables.Categories.Type.INCOME : currentCategoryType == Tables.Categories.Type.INCOME ? Tables.Categories.Type.TRANSFER : Tables.Categories.Type.EXPENSE;
        setCategoryType(newCategoryType);
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        final Uri uri = TransactionsProvider.uriTransaction(itemId);
        final String[] projection = new String[]{
                Tables.Transactions.T_ID, Tables.Transactions.DATE, Tables.Transactions.AMOUNT, Tables.Transactions.NOTE, Tables.Transactions.STATE, Tables.Transactions.EXCHANGE_RATE, Tables.Transactions.SHOW_IN_TOTALS,
                Tables.Transactions.ACCOUNT_FROM_ID, Tables.Accounts.AccountFrom.S_TITLE, Tables.Accounts.AccountFrom.S_CURRENCY_ID, Tables.Currencies.CurrencyFrom.S_CODE, Tables.Currencies.CurrencyFrom.S_EXCHANGE_RATE,
                Tables.Transactions.ACCOUNT_TO_ID, Tables.Accounts.AccountTo.S_TITLE, Tables.Accounts.AccountTo.S_CURRENCY_ID, Tables.Currencies.CurrencyTo.S_CODE, Tables.Currencies.CurrencyTo.S_EXCHANGE_RATE,
                Tables.Transactions.CATEGORY_ID, Tables.Categories.CategoriesChild.S_TITLE, Tables.Categories.CategoriesChild.S_TYPE, Tables.Categories.CategoriesChild.S_COLOR};

        return new CursorLoader(getActivity(), uri, projection, null, null, null);
    }

    @Override
    protected boolean bindItem(Cursor c, boolean isDataLoaded)
    {
        if (!isDataLoaded && c != null && c.moveToFirst())
        {
            setCategoryType(c.getInt(c.getColumnIndex(Tables.Categories.CategoriesChild.TYPE)));
            setDate(c.getLong(c.getColumnIndex(Tables.Transactions.DATE)));
            setAccountFrom(c.getLong(c.getColumnIndex(Tables.Transactions.ACCOUNT_FROM_ID)), c.getString(c.getColumnIndex(Tables.Accounts.AccountFrom.TITLE)), c.getLong(c.getColumnIndex(Tables.Accounts.AccountFrom.CURRENCY_ID)), c.getString(c.getColumnIndex(Tables.Currencies.CurrencyFrom.CODE)), c.getDouble(c.getColumnIndex(Tables.Currencies.CurrencyFrom.EXCHANGE_RATE)));
            setAccountTo(c.getLong(c.getColumnIndex(Tables.Transactions.ACCOUNT_TO_ID)), c.getString(c.getColumnIndex(Tables.Accounts.AccountTo.TITLE)), c.getLong(c.getColumnIndex(Tables.Accounts.AccountTo.CURRENCY_ID)), c.getString(c.getColumnIndex(Tables.Currencies.CurrencyTo.CODE)), c.getDouble(c.getColumnIndex(Tables.Currencies.CurrencyTo.EXCHANGE_RATE)));
            setExchangeRate(c.getDouble(c.getColumnIndex(Tables.Transactions.EXCHANGE_RATE)));
            setCategory(c.getLong(c.getColumnIndex(Tables.Transactions.CATEGORY_ID)), c.getString(c.getColumnIndex(Tables.Categories.CategoriesChild.TITLE)), c.getInt(c.getColumnIndex(Tables.Categories.CategoriesChild.COLOR)));
            setAmount(c.getDouble(c.getColumnIndex(Tables.Transactions.AMOUNT)));
            setNote(c.getString(c.getColumnIndex(Tables.Transactions.NOTE)));
            setState(c.getInt(c.getColumnIndex(Tables.Transactions.STATE)));
            setShowInTotals(c.getInt(c.getColumnIndex(Tables.Transactions.SHOW_IN_TOTALS)) != 0);
            return true;
        }

        return isDataLoaded;
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            setCategoryType(savedInstanceState.getInt(STATE_CATEGORY_TYPE));
            setDate(savedInstanceState.getLong(STATE_DATE));
            setAccountFrom(savedInstanceState.getLong(STATE_ACCOUNT_FROM_ID), savedInstanceState.getString(STATE_ACCOUNT_FROM_TITLE), savedInstanceState.getLong(STATE_ACCOUNT_FROM_CURRENCY_ID), savedInstanceState.getString(STATE_ACCOUNT_FROM_CURRENCY_CODE), savedInstanceState.getDouble(STATE_ACCOUNT_FROM_CURRENCY_EXCHANGE_RATE));
            setAccountTo(savedInstanceState.getLong(STATE_ACCOUNT_TO_ID), savedInstanceState.getString(STATE_ACCOUNT_TO_TITLE), savedInstanceState.getLong(STATE_ACCOUNT_TO_CURRENCY_ID), savedInstanceState.getString(STATE_ACCOUNT_TO_CURRENCY_CODE), savedInstanceState.getDouble(STATE_ACCOUNT_TO_CURRENCY_EXCHANGE_RATE));
            setExchangeRate(savedInstanceState.getDouble(STATE_EXCHANGE_RATE));
            setCategory(savedInstanceState.getLong(STATE_CATEGORY_ID), savedInstanceState.getString(STATE_CATEGORY_TITLE), savedInstanceState.getInt(STATE_CATEGORY_COLOR));
            expenseCategoryId = savedInstanceState.getLong(STATE_EXPENSE_CATEGORY_ID);
            expenseCategoryTitle = savedInstanceState.getString(STATE_EXPENSE_CATEGORY_TITLE);
            expenseCategoryColor = savedInstanceState.getInt(STATE_EXPENSE_CATEGORY_COLOR);
            incomeCategoryId = savedInstanceState.getLong(STATE_INCOME_CATEGORY_ID);
            incomeCategoryTitle = savedInstanceState.getString(STATE_INCOME_CATEGORY_TITLE);
            incomeCategoryColor = savedInstanceState.getInt(STATE_INCOME_CATEGORY_COLOR);
            setAmount(savedInstanceState.getDouble(STATE_AMOUNT));
            setNote(savedInstanceState.getString(STATE_NOTE));
            setState(savedInstanceState.getInt(STATE_STATE));
            setShowInTotals(savedInstanceState.getBoolean(STATE_SHOW_IN_TOTALS));
            userSetAccountFrom = savedInstanceState.getBoolean(STATE_USER_SET_ACCOUNT_FROM);
            userSetAccountTo = savedInstanceState.getBoolean(STATE_USER_SET_ACCOUNT_TO);
            userSetCategoryExpense = savedInstanceState.getBoolean(STATE_USER_SET_CATEGORY_EXPENSE);
            userSetCategoryIncome = savedInstanceState.getBoolean(STATE_USER_SET_CATEGORY_INCOME);
            autoTransactionSetForType = savedInstanceState.getBoolean(STATE_AUTO_TRANSACTION_SET_FOR_TYPE);
        }
        else if (itemId == 0)
        {
            setCategoryType(Tables.Categories.Type.EXPENSE);
            setDate(System.currentTimeMillis());
            setAccountFrom(0, null, 0, null, 1.0);
            setAccountTo(0, null, 0, null, 1.0);
            setExchangeRate(1.0);
            setCategory(Tables.Categories.IDs.EXPENSE_ID, null, 0);
            setAmount(0);
            setNote(null);
            setState(Tables.Transactions.State.CONFIRMED);
            setShowInTotals(true);
            userSetAccountFrom = false;
            userSetAccountTo = false;
            userSetCategoryExpense = false;
            userSetCategoryIncome = false;
            autoTransactionSetForType = false;

            // Loader
            getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
            getLoaderManager().initLoader(LOADER_CATEGORIES, null, this);
            getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
        }
    }

    private void doAutoComplete()
    {
        if (autoTransactionSetForType || doingAutoTransaction || !transactionAutoHelper.isDataOk())
            return;

        doingAutoTransaction = true;

        // Category
        if ((getCategoryType() == Tables.Categories.Type.EXPENSE && !userSetCategoryExpense) || (getCategoryType() == Tables.Categories.Type.INCOME && !userSetCategoryIncome))
        {
            transactionAutoHelper.setCategoryId(0);
            final Category category = transactionAutoHelper.getCategory();
            if (category != null)
                setCategory(category.getId(), category.getTitle(), category.getColor());
        }

        // Account from
        if (!userSetAccountFrom && getCategoryType() != Tables.Categories.Type.INCOME)
        {
            transactionAutoHelper.setAccountFromId(0);
            final Account account = transactionAutoHelper.getAccount(TransactionAutoHelper.AccountType.FROM);
            if (account != null)
                setAccountFrom(account.getId(), account.getTitle(), account.getCurrency().getId(), account.getCurrency().getCode(), account.getCurrency().getExchangeRate());
        }

        // Account to
        if (!userSetAccountTo && getCategoryType() != Tables.Categories.Type.EXPENSE || getAccountFromId() == getAccountToId())
        {
            transactionAutoHelper.setAccountToId(0);
            final Account account = transactionAutoHelper.getAccount(TransactionAutoHelper.AccountType.TO);
            if (account != null)
                setAccountTo(account.getId(), account.getTitle(), account.getCurrency().getId(), account.getCurrency().getCode(), account.getCurrency().getExchangeRate());
        }

        autoTransactionSetForType = true;
        doingAutoTransaction = false;
    }

    private double getAmount()
    {
        return amount_CV.getAmount();
    }

    private void setAmount(double amount)
    {
        amount_CV.setAmount(amount, getCategoryType() == Tables.Categories.Type.INCOME ? getAccountToCurrencyId() : getAccountFromCurrencyId(), getCategoryType());
    }

    private void setAccountFrom(long accountId, String title, long currencyId, String currencyCode, double exchangeRate)
    {
        if (accountId == getAccountFromId())
            return;

        accountFrom_CV.setAccount(accountId, title, currencyId, currencyCode, exchangeRate, getString(R.string.from_account));
        transactionAutoHelper.setAccountFromId(getCategoryType() == Tables.Categories.Type.INCOME ? 0 : accountId);
        checkNeedExchangeRate();
        doAutoComplete();
        setAmount(getAmount());
    }

    private long getAccountFromId()
    {
        return getCategoryType() == Tables.Categories.Type.INCOME ? Tables.Categories.IDs.INCOME_ID : accountFrom_CV.getAccountId();
    }

    private String getAccountFromTitle()
    {
        return accountFrom_CV.getAccountTitle();
    }

    private long getAccountFromCurrencyId()
    {
        return accountFrom_CV.getCurrencyId();
    }

    private String getAccountFromCurrencyCode()
    {
        return accountFrom_CV.getCurrencyCode();
    }

    private double getAccountFromCurrencyExchangeRate()
    {
        return accountFrom_CV.getCurrencyExchangeRate();
    }

    private void setAccountTo(long accountId, String title, long currencyId, String currencyCode, double exchangeRate)
    {
        if (accountId == getAccountToId())
            return;

        accountTo_CV.setAccount(accountId, title, currencyId, currencyCode, exchangeRate, getString(R.string.to_account));
        transactionAutoHelper.setAccountToId(getCategoryType() == Tables.Categories.Type.EXPENSE ? 0 : accountId);
        checkNeedExchangeRate();
        doAutoComplete();
        setAmount(getAmount());
    }

    private long getAccountToId()
    {
        return getCategoryType() == Tables.Categories.Type.EXPENSE ? Tables.Categories.IDs.EXPENSE_ID : accountTo_CV.getAccountId();
    }

    private String getAccountToTitle()
    {
        return accountTo_CV.getAccountTitle();
    }

    private long getAccountToCurrencyId()
    {
        return accountTo_CV.getCurrencyId();
    }

    private String getAccountToCurrencyCode()
    {
        return accountTo_CV.getCurrencyCode();
    }

    private double getAccountToCurrencyExchangeRate()
    {
        return accountTo_CV.getCurrencyExchangeRate();
    }

    private long getDate()
    {
        return date_CV.getDate();
    }

    private void setDate(long date)
    {
        if (date == getDate())
            return;

        date_CV.setDate(date);
        transactionAutoHelper.setDate(date);
        doAutoComplete();
    }

    private double getExchangeRate()
    {
        //noinspection ConstantConditions
        return getCategoryType() == Tables.Categories.Type.TRANSFER ? amount_CV.getExchangeRate() : 1.0;
    }

    private void setExchangeRate(double exchangeRate)
    {
        amount_CV.setExchangeRate(exchangeRate, getAccountToCurrencyId());
    }

    private void setCategory(long categoryId, String title, int color)
    {
        if (categoryId == getCategoryId())
            return;

        category_CV.setCategory(categoryId, title, color);

        switch (getCategoryType())
        {
            case Tables.Categories.Type.EXPENSE:
                expenseCategoryId = categoryId;
                expenseCategoryTitle = title;
                expenseCategoryColor = color;
                break;

            case Tables.Categories.Type.INCOME:
                incomeCategoryId = categoryId;
                incomeCategoryTitle = title;
                incomeCategoryColor = color;
                break;
        }

        transactionAutoHelper.setCategoryId(categoryId);
        doAutoComplete();
    }

    private long getCategoryId()
    {
        switch (getCategoryType())
        {
            case Tables.Categories.Type.TRANSFER:
                return Tables.Categories.IDs.TRANSFER_ID;

            case Tables.Categories.Type.EXPENSE:
                return category_CV.getCategoryId() <= 0 ? Tables.Categories.IDs.EXPENSE_ID : category_CV.getCategoryId();

            case Tables.Categories.Type.INCOME:
                return category_CV.getCategoryId() <= 0 ? Tables.Categories.IDs.INCOME_ID : category_CV.getCategoryId();
        }

        return 0;
    }

    private String getCategoryTitle()
    {
        return category_CV.getCategoryTitle();
    }

    private int getCategoryColor()
    {
        return category_CV.getCategoryColor();
    }

    private int getCategoryType()
    {
        return categoryType;
    }

    private void setCategoryType(int categoryType)
    {
        if (getCategoryType() == categoryType)
            return;

        this.categoryType = categoryType;
        transactionAutoHelper.setCategoryId(0);
        switch (categoryType)
        {
            case Tables.Categories.Type.EXPENSE:
                accountsSeparator_V.setVisibility(View.GONE);
                accountTo_CV.setVisibility(View.GONE);
                accountFrom_CV.setVisibility(View.VISIBLE);
                category_CV.setVisibility(View.VISIBLE);
                setCategory(expenseCategoryId, expenseCategoryTitle, expenseCategoryColor);
                break;

            case Tables.Categories.Type.INCOME:
                accountFrom_CV.setVisibility(View.GONE);
                accountsSeparator_V.setVisibility(View.GONE);
                accountTo_CV.setVisibility(View.VISIBLE);
                category_CV.setVisibility(View.VISIBLE);
                setCategory(incomeCategoryId, incomeCategoryTitle, incomeCategoryColor);
                break;

            case Tables.Categories.Type.TRANSFER:
                accountFrom_CV.setVisibility(View.VISIBLE);
                accountsSeparator_V.setVisibility(View.VISIBLE);
                accountTo_CV.setVisibility(View.VISIBLE);
                category_CV.setVisibility(View.GONE);
                setCategory(Tables.Categories.IDs.TRANSFER_ID, getString(R.string.transfer), 0);
                break;
        }

        checkNeedExchangeRate();

        transactionAutoHelper.setAccountFromId(0);
        transactionAutoHelper.setAccountToId(0);
        transactionAutoHelper.setCategoryType(categoryType);
        autoTransactionSetForType = false;
        doAutoComplete();

        setAmount(getAmount());
    }

    private String getNote()
    {
        return note_CV.getText();
    }

    private void setNote(String note)
    {
        note_CV.setText(note);
    }

    private int getState()
    {
        return confirmed_CB.isChecked() ? Tables.Transactions.State.CONFIRMED : Tables.Transactions.State.PENDING;
    }

    private void setState(int state)
    {
        confirmed_CB.setChecked(state == Tables.Transactions.State.CONFIRMED);
    }

    private boolean isShowInTotals()
    {
        return showInTotals_CB.isChecked();
    }

    private void setShowInTotals(boolean showInTotals)
    {
        showInTotals_CB.setChecked(showInTotals);
    }

    private void checkNeedExchangeRate()
    {
        boolean shouldBeVisible = false;
        double exchangeRate = 1.0;
        switch (getCategoryType())
        {
            case Tables.Categories.Type.EXPENSE:
            case Tables.Categories.Type.INCOME:
                shouldBeVisible = false;
                exchangeRate = 1.0;
                break;

            case Tables.Categories.Type.TRANSFER:
                shouldBeVisible = !(getAccountFromCurrencyId() == getAccountToCurrencyId() || getAccountFromId() == 0 || getAccountToId() == 0);
                exchangeRate = getAccountFromCurrencyId() == CurrencyHelper.get().getMainCurrencyId() ? 1 / getAccountToCurrencyExchangeRate() : getAccountToCurrencyId() == CurrencyHelper.get().getMainCurrencyId() ? getAccountFromCurrencyExchangeRate() : 1.0;
                break;
        }
        amount_CV.setExchangeRateVisible(shouldBeVisible);
        setExchangeRate(exchangeRate);
    }
}