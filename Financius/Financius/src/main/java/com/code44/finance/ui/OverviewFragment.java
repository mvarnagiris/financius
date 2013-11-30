package com.code44.finance.ui;

import android.animation.LayoutTransition;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.code44.finance.R;
import com.code44.finance.adapters.OverviewCardsAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.db.model.Account;
import com.code44.finance.db.model.CategoriesPeriodReport;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.ui.accounts.AccountEditActivity;
import com.code44.finance.ui.transactions.TransactionEditActivity;
import com.code44.finance.utils.LayoutType;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.cards.AccountsCardView;
import com.code44.finance.views.cards.CategoriesReportCardView;
import com.code44.finance.views.cards.CreateAccountCardView;
import com.code44.finance.views.cards.TransactionsCardView;
import com.code44.finance.views.reports.ExpenseGraphView;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.List;

public class OverviewFragment extends AbstractFragment implements MainActivity.NavigationContentFragment, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, AdapterView.OnItemClickListener
{
    private static final int LOADER_ACCOUNTS = 1;
    private static final int LOADER_TRANSACTIONS = 2;
    private static final int LOADER_REPORTS = 3;
    private View bottomBar_V;
    // -----------------------------------------------------------------------------------------------------------------
    private OverviewCardsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(LayoutType.isTablet(getActivity()));

        // Register events
        EventBus.getDefault().register(this, PeriodHelper.PeriodTypeChangedEvent.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final ListView list_V = (ListView) view.findViewById(R.id.list_V);
        bottomBar_V = view.findViewById(R.id.bottomBar_V);
        final View create_B = view.findViewById(R.id.create_B);

        // Setup
        create_B.setOnClickListener(this);
        bottomBar_V.setVisibility(LayoutType.isTablet(getActivity()) ? View.GONE : View.VISIBLE);
        adapter = new OverviewCardsAdapter(getActivity());
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
        list_V.setLayoutTransition(new LayoutTransition());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
        getLoaderManager().initLoader(LOADER_REPORTS, null, this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister events
        EventBus.getDefault().unregister(this, PeriodHelper.PeriodTypeChangedEvent.class);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.overview, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_create:
                TransactionEditActivity.startItemEdit(getActivity(), 0, item.getActionView());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        switch (id)
        {
            case LOADER_ACCOUNTS:
            {
                uri = AccountsProvider.uriAccounts(getActivity());
                projection = new String[]{Tables.Accounts.T_ID, Tables.Accounts.TITLE, Tables.Accounts.BALANCE, Tables.Accounts.CURRENCY_ID, Tables.Currencies.EXCHANGE_RATE, Tables.Accounts.SHOW_IN_TOTALS};
                selection = Tables.Accounts.ORIGIN + "<>? and " + Tables.Accounts.DELETE_STATE + "=?";
                selectionArgs = new String[]{String.valueOf(Tables.Categories.Origin.SYSTEM), String.valueOf(Tables.DeleteState.NONE)};
                sortOrder = Tables.Accounts.TITLE;
                break;
            }

            case LOADER_TRANSACTIONS:
            {
                final PeriodHelper periodHelper = PeriodHelper.getDefault(getActivity());
                final String format;
                switch (periodHelper.getType())
                {
                    case PeriodHelper.TYPE_YEAR:
                        format = "%Y-%m";
                        break;

                    case PeriodHelper.TYPE_DAY:
                        format = "%Y-%m-%dT%H";
                        break;

                    default:
                        format = "%Y-%m-%d";
                        break;
                }

                uri = TransactionsProvider.uriTransactions(getActivity());
                projection = new String[]
                        {
                                "min(" + Tables.Transactions.DATE + ")",
                                "sum(case " + Tables.Categories.CategoriesChild.T_TYPE + " when " + Tables.Categories.Type.INCOME + " then " + Tables.Transactions.AMOUNT + "*" + Tables.Currencies.CurrencyTo.T_EXCHANGE_RATE + " else 0 end)",
                                "sum(case " + Tables.Categories.CategoriesChild.T_TYPE + " when " + Tables.Categories.Type.EXPENSE + " then " + Tables.Transactions.AMOUNT + "*" + Tables.Currencies.CurrencyFrom.T_EXCHANGE_RATE + " else 0 end)"
                        };
                selection = Tables.Transactions.STATE + "=? and " + Tables.Categories.CategoriesChild.T_TYPE + "<>? and " + Tables.Transactions.SHOW_IN_TOTALS + "=? and " + Tables.Transactions.DELETE_STATE + "=? and " + Tables.Transactions.DATE + " between ? and ?"
                        + ") GROUP BY (strftime('" + format + "',(" + Tables.Transactions.DATE + "/ 1000), 'unixepoch', 'localtime')";
                selectionArgs = new String[]{String.valueOf(Tables.Transactions.State.CONFIRMED), String.valueOf(Tables.Categories.Type.TRANSFER), "1", String.valueOf(Tables.DeleteState.NONE), String.valueOf(periodHelper.getCurrentStart()), String.valueOf(periodHelper.getCurrentEnd())};
                sortOrder = Tables.Transactions.DATE;
                break;
            }

            case LOADER_REPORTS:
            {
                final PeriodHelper periodHelper = PeriodHelper.getDefault(getActivity());

                uri = TransactionsProvider.uriTransactions(getActivity());
                projection = new String[]
                        {
                                "min(" + Tables.Transactions.CATEGORY_ID + ")",
                                "min(" + Tables.Categories.CategoriesChild.T_PARENT_ID + ")",
                                "min(" + Tables.Categories.CategoriesChild.T_TYPE + ")",
                                "min(" + Tables.Categories.CategoriesChild.T_COLOR + ")",
                                "min(" + Tables.Categories.CategoriesChild.T_LEVEL + ")",
                                "min(" + Tables.Categories.CategoriesChild.T_TITLE + ")",
                                "min(" + Tables.Categories.CategoriesParent.T_TITLE + ")",
                                "sum(case " + Tables.Categories.CategoriesChild.T_TYPE + " when " + Tables.Categories.Type.INCOME + " then " + Tables.Transactions.AMOUNT + "*" + Tables.Currencies.CurrencyTo.T_EXCHANGE_RATE + " else " + Tables.Transactions.AMOUNT + "*" + Tables.Currencies.CurrencyFrom.T_EXCHANGE_RATE + " end)"
                        };
                selection = Tables.Transactions.STATE + "=? and " + Tables.Transactions.SHOW_IN_TOTALS + "=? and " + Tables.Transactions.DELETE_STATE + "=? and " + Tables.Transactions.DATE + " between ? and ?"
                        + ") GROUP BY (" + Tables.Transactions.CATEGORY_ID;
                selectionArgs = new String[]{String.valueOf(Tables.Transactions.State.CONFIRMED), "1", String.valueOf(Tables.DeleteState.NONE), String.valueOf(periodHelper.getCurrentStart()), String.valueOf(periodHelper.getCurrentEnd())};
                break;
            }
        }

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_ACCOUNTS:
                bindAccounts(cursor);
                break;

            case LOADER_TRANSACTIONS:
                bindTransactions(cursor);
                break;

            case LOADER_REPORTS:
                bindReports(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.create_B:
                TransactionEditActivity.startItemEdit(getActivity(), 0, v);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (id == AccountsCardView.UNIQUE_CARD_ID)
            EventBus.getDefault().post(new NavigationFragment.NavigationEvent(NavigationFragment.NavigationEvent.TYPE_ACCOUNTS));
        else if (id == TransactionsCardView.UNIQUE_CARD_ID)
            EventBus.getDefault().post(new NavigationFragment.NavigationEvent(NavigationFragment.NavigationEvent.TYPE_TRANSACTIONS));
        else if (id == CategoriesReportCardView.UNIQUE_CARD_ID)
            EventBus.getDefault().post(new NavigationFragment.NavigationEvent(NavigationFragment.NavigationEvent.TYPE_REPORTS));
        else if (id == CreateAccountCardView.UNIQUE_CARD_ID)
            AccountEditActivity.startItemEdit(getActivity(), 0);
    }

    @Override
    public String getTitle()
    {
        return getString(R.string.overview);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(PeriodHelper.PeriodTypeChangedEvent event)
    {
        getLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
        getLoaderManager().restartLoader(LOADER_REPORTS, null, this);
    }

    private void bindAccounts(Cursor c)
    {
        final List<Account> accountsList = new ArrayList<Account>();

        int visibleAccounts = 0;
        boolean showCreateAccount = true;
        if (c != null && c.moveToFirst())
        {
            showCreateAccount = false;

            final int iShowInTotals = c.getColumnIndex(Tables.Accounts.SHOW_IN_TOTALS);
            do
            {
                if (c.getInt(iShowInTotals) != 0)
                {
                    visibleAccounts++;
                    accountsList.add(Account.from(c));
                }
            }
            while (c.moveToNext());
        }

        if (visibleAccounts == 0)
        {
            if (showCreateAccount)
                adapter.addCardInfo(new CreateAccountCardView.CreateAccountCardInfo(getActivity()));
            adapter.removeCardInfo(AccountsCardView.UNIQUE_CARD_ID);
            bottomBar_V.setVisibility(View.GONE);
        }
        else
        {
            adapter.removeCardInfo(CreateAccountCardView.UNIQUE_CARD_ID);
            adapter.addCardInfo(new AccountsCardView.AccountsCardInfo(getActivity()).setAccountList(accountsList));
            bottomBar_V.setVisibility(LayoutType.isTablet(getActivity()) ? View.GONE : View.VISIBLE);
        }
    }

    private void bindTransactions(Cursor c)
    {
        double income = 0;
        double expense = 0;

        if (c != null && c.moveToFirst())
        {
            do
            {
                income += c.getDouble(1);
                expense += c.getDouble(2);
            }
            while (c.moveToNext());
        }

        if (income == 0 && expense == 0)
        {
            adapter.removeCardInfo(TransactionsCardView.UNIQUE_CARD_ID);
        }
        else
        {
            final PeriodHelper periodHelper = PeriodHelper.getDefault(getActivity());
            adapter.addCardInfo(new TransactionsCardView.TransactionsCardInfo(getActivity()).setItemList(ExpenseGraphView.prepareItems(periodHelper.getType(), periodHelper.getCurrentStart(), periodHelper.getCurrentEnd(), c)).setIncome(income).setExpense(expense));
        }
    }

    private void bindReports(Cursor c)
    {
        CategoriesPeriodReport report = CategoriesPeriodReport.from(c);
        if (report.getExpenseList().size() > 0)
            adapter.addCardInfo(new CategoriesReportCardView.CategoriesReportCardInfo(getActivity()).setReport(report));
        else
            adapter.removeCardInfo(CategoriesReportCardView.UNIQUE_CARD_ID);
    }
}