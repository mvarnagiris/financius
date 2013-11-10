package com.code44.finance.ui.reports;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.adapters.CategoriesReportAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.db.model.CategoriesPeriodReport;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.ui.AbstractFragment;
import com.code44.finance.ui.MainActivity;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;
import com.code44.finance.utils.LayoutType;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.PeriodChangerView;
import com.code44.finance.views.reports.PieChartView;
import de.greenrobot.event.EventBus;

public class CategoriesReportFragment extends AbstractFragment implements LoaderManager.LoaderCallbacks<Cursor>, PeriodChangerView.PeriodChangerListener, MainActivity.NavigationContentFragment, View.OnClickListener
{
    private static final int LOADER_TRANSACTIONS = 1;
    // -----------------------------------------------------------------------------------------------------------------
    private PeriodChangerView periodChanger_V;
    private View headerContainer_V;
    private View fakeHeader_V;
    private PieChartView pieChart_V;
    private TextView expense_TV;
    private TextView income_TV;
    private TextView transfer_TV;
    private ListView list_V;
    // -----------------------------------------------------------------------------------------------------------------
    private CategoriesReportAdapter adapter;

    public static CategoriesReportFragment newInstance()
    {
        return new CategoriesReportFragment();
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
        return inflater.inflate(R.layout.fragment_categories_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        periodChanger_V = (PeriodChangerView) view.findViewById(R.id.periodChanger_V);
        headerContainer_V = view.findViewById(R.id.headerContainer_V);
        pieChart_V = (PieChartView) view.findViewById(R.id.pieChart_V);
        expense_TV = (TextView) view.findViewById(R.id.expense_TV);
        income_TV = (TextView) view.findViewById(R.id.income_TV);
        transfer_TV = (TextView) view.findViewById(R.id.transfer_TV);
        list_V = (ListView) view.findViewById(R.id.list_V);
        fakeHeader_V = new View(getActivity());
        fakeHeader_V.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        periodChanger_V.setListener(this);
        periodChanger_V.setOnClickListener(this);
        adapter = new CategoriesReportAdapter(getActivity());
        if (!LayoutType.isTabletLandscape(getActivity()))
            list_V.addHeaderView(fakeHeader_V, null, false);
        list_V.setAdapter(adapter);
        if (!LayoutType.isTabletLandscape(getActivity()))
        {
            //noinspection ConstantConditions
            headerContainer_V.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        //noinspection ConstantConditions
                        headerContainer_V.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    else
                        //noinspection ConstantConditions,deprecation
                        headerContainer_V.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    fakeHeader_V.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, headerContainer_V.getHeight()));
                }
            });
            list_V.setOnScrollListener(new AbsListView.OnScrollListener()
            {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState)
                {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {
                    if (totalItemCount > 0 && firstVisibleItem == 0)
                    {
                        final float max = fakeHeader_V.getHeight() / 4;
                        final float delta = -fakeHeader_V.getTop();
                        if (delta <= max)
                        {
                            final float progress = delta / max;

                            headerContainer_V.setAlpha(1.0f - (0.6f * progress));
                            headerContainer_V.setScaleX(1.0f - (0.1f * progress));
                            headerContainer_V.setScaleY(1.0f - (0.1f * progress));
                            headerContainer_V.setTranslationY(-delta / 3);
                        }
                    }
                }
            });
        }

        // Loader
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister events
        EventBus.getDefault().unregister(this, PeriodHelper.PeriodTypeChangedEvent.class);
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
            case LOADER_TRANSACTIONS:
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
                selectionArgs = new String[]{String.valueOf(Tables.Transactions.State.CONFIRMED), "1", String.valueOf(Tables.DeleteState.NONE), String.valueOf(periodHelper.getActiveStart()), String.valueOf(periodHelper.getActiveEnd())};
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
            case LOADER_TRANSACTIONS:
                bindTransactions(cursor);
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
            case R.id.periodChanger_V:
                PeriodHelper.getDefault(getActivity()).resetActive();
                periodChanger_V.updateViews();
                getLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
                break;
        }
    }

    @Override
    public void onPeriodChanged()
    {
        getLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(PeriodHelper.PeriodTypeChangedEvent event)
    {
        periodChanger_V.updateViews();
        getLoaderManager().restartLoader(LOADER_TRANSACTIONS, null, this);
    }

    @Override
    public String getTitle()
    {
        return getString(R.string.categories_report);
    }

    private void bindTransactions(Cursor c)
    {
        final CategoriesPeriodReport report = CategoriesPeriodReport.from(c);
        pieChart_V.bind(report.getExpenseList());
        final long mainCurrencyId = CurrenciesHelper.getDefault(getActivity()).getMainCurrencyId();
        expense_TV.setText(AmountUtils.formatAmount(getActivity(), mainCurrencyId, report.getTotalExpense()));
        income_TV.setText(AmountUtils.formatAmount(getActivity(), mainCurrencyId, report.getTotalIncome()));
        transfer_TV.setText(AmountUtils.formatAmount(getActivity(), mainCurrencyId, report.getTotalTransfer()));
        adapter.setReport(report);
    }
}