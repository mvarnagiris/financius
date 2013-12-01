package com.code44.finance.ui.budgets;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.code44.finance.R;
import com.code44.finance.adapters.AbstractCursorAdapter;
import com.code44.finance.adapters.BudgetsAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.BudgetsProvider;
import com.code44.finance.ui.ItemListFragment;
import com.code44.finance.ui.MainActivity;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.PeriodChangerView;
import de.greenrobot.event.EventBus;

public class BudgetListFragment extends ItemListFragment implements MainActivity.NavigationContentFragment, PeriodChangerView.PeriodChangerListener, View.OnClickListener
{
    private PeriodChangerView periodChanger_V;

    public static BudgetListFragment newInstance()
    {
        final Bundle args = makeArgs(SELECTION_TYPE_NONE, null);

        final BudgetListFragment f = new BudgetListFragment();
        f.setArguments(args);
        return f;
    }

    public static Loader<Cursor> createItemsLoader(Context context)
    {
        final PeriodHelper periodHelper = PeriodHelper.getDefault(context);

        final Uri uri = BudgetsProvider.uriBudgets(context);
        final String[] projection = new String[]{Tables.Budgets.T_ID, Tables.Budgets.TITLE, Tables.Budgets.NOTE, Tables.Budgets.AMOUNT, Tables.Budgets.INCLUDE_IN_TOTAL_BUDGET, Tables.Budgets.S_SUM, Tables.Budgets.PERIOD};
        final String selection = Tables.Budgets.DELETE_STATE + "=? and (ifnull(" + Tables.Transactions.DATE + ", '') = '' or " + Tables.Transactions.DATE + " between ? and ?))"
                + " GROUP BY (" + Tables.Budgets.T_ID;
        final String[] selectionArgs = new String[]{String.valueOf(Tables.DeleteState.NONE), String.valueOf(periodHelper.getActiveStart()), String.valueOf(periodHelper.getActiveEnd())};
        final String sortOrder = Tables.Budgets.TITLE;

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Register events
        EventBus.getDefault().register(this, PeriodHelper.PeriodTypeChangedEvent.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_budget_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        periodChanger_V = (PeriodChangerView) view.findViewById(R.id.periodChanger_V);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        periodChanger_V.setListener(this);
        periodChanger_V.setOnClickListener(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister events
        EventBus.getDefault().unregister(this, PeriodHelper.PeriodTypeChangedEvent.class);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.periodChanger_V:
                PeriodHelper.getDefault(getActivity()).resetActive();
                periodChanger_V.updateViews();
                getLoaderManager().restartLoader(LOADER_ITEMS, null, this);
                break;
        }
    }

    @Override
    public String getTitle()
    {
        return getString(R.string.budgets);
    }

    @Override
    public void onPeriodChanged()
    {
        getLoaderManager().restartLoader(LOADER_ITEMS, null, this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(PeriodHelper.PeriodTypeChangedEvent event)
    {
        periodChanger_V.updateViews();
        getLoaderManager().restartLoader(LOADER_ITEMS, null, this);
    }

    @Override
    protected AbstractCursorAdapter createAdapter(Context context)
    {
        return new BudgetsAdapter(context);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return createItemsLoader(getActivity());
    }

    @Override
    protected void bindItems(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            final PeriodHelper periodHelper = PeriodHelper.getDefault(getActivity());
            final int periodType = periodHelper.getType();

            final int iSum = c.getColumnIndex(Tables.Budgets.SUM);
            final int iAmount = c.getColumnIndex(Tables.Budgets.AMOUNT);
            final int iPeriodType = c.getColumnIndex(Tables.Budgets.PERIOD);

            final long time = System.currentTimeMillis();
            double totalSum = 0;
            double totalAmount = 0;
            int period;
            do
            {
                totalSum += c.getDouble(iSum);

                period = c.getInt(iPeriodType);
                if (period == periodType)
                {
                    totalAmount += c.getDouble(iAmount);
                }
                else
                {
                    final int dayCountInBudgetPeriod = PeriodHelper.getDayCountInPeriod(PeriodHelper.getPeriodStart(period, time), PeriodHelper.getPeriodEnd(period, time));
                    final int dayCountInSelectedPeriod = PeriodHelper.getDayCountInPeriod(PeriodHelper.getPeriodStart(periodType, time), PeriodHelper.getPeriodEnd(periodType, time));
                    final double amountInSelectedPeriod = c.getDouble(iAmount) / dayCountInBudgetPeriod * dayCountInSelectedPeriod;
                    totalAmount += amountInSelectedPeriod;
                }
            }
            while (c.moveToNext());

            ((BudgetsAdapter) adapter).bindHeader(periodType, periodHelper.getActiveStart(), periodHelper.getActiveEnd(), totalSum, totalAmount);
        }
        super.bindItems(c);
    }

    @Override
    protected void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras)
    {
        // Ignore
    }

    @Override
    protected void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c, View view)
    {
        BudgetItemActivity.startItem(context, position - 1);
    }

    @Override
    protected void startItemCreate(Context context, View view)
    {
        BudgetEditActivity.startItemEdit(context, 0);
    }
}
