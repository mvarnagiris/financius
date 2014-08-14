package com.code44.finance.ui.overview;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartValue;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.AccountsView;
import com.code44.finance.views.OverviewGraphView;

import java.util.ArrayList;
import java.util.List;

public class OverviewFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final int LOADER_ACCOUNTS = 1;

    private final PeriodHelper periodHelper = PeriodHelper.get();

    private OverviewGraphView overviewGraph_V;
    private AccountsView accounts_V;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        overviewGraph_V = (OverviewGraphView) view.findViewById(R.id.overviewGraph_V);
        accounts_V = (AccountsView) view.findViewById(R.id.accounts_V);

        // Setup
        overviewGraph_V.setOnClickListener(this);
        setOverviewGraph(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ACCOUNTS:
                return Tables.Accounts.getQuery().selection(" and " + Tables.Accounts.INCLUDE_IN_TOTALS + "=?", "1").asCursorLoader(getActivity(), AccountsProvider.uriAccounts());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_ACCOUNTS:
                onAccountsLoaded(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overviewGraph_V:
                break;
        }
    }

    @Override
    public String getTitle() {
        return periodHelper.getTitle();
    }

    private void onAccountsLoaded(Cursor cursor) {
        final List<Account> accounts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                accounts.add(Account.from(cursor));
            } while (cursor.moveToNext());
        }
        accounts_V.setAccounts(accounts);
    }

    private void setOverviewGraph(Cursor cursor) {
        final PieChartData pieChartData = PieChartData.builder()
                .addValues(new PieChartValue(152151, 0xff8bc34a))
                .addValues(new PieChartValue(107458, 0xff03a9f4))
                .addValues(new PieChartValue(57590, 0xffffc107))
                .addValues(new PieChartValue(40302, 0xff673ab7))
                .build();
        overviewGraph_V.setPieChartData(pieChartData);
        overviewGraph_V.setTotalExpense(pieChartData.getTotalValue());
    }
}
