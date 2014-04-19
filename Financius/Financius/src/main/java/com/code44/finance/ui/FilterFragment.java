package com.code44.finance.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.code44.finance.R;
import com.code44.finance.ui.accounts.AccountListActivity;
import com.code44.finance.ui.accounts.AccountListFragment;
import com.code44.finance.ui.dialogs.DateTimeDialog;
import com.code44.finance.utils.FilterHelper;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.FilterToggleView;
import de.greenrobot.event.EventBus;
import org.joda.time.format.DateTimeFormat;

public class FilterFragment extends BaseFragment implements View.OnClickListener, DateTimeDialog.DialogCallbacks, FilterToggleView.Callbacks
{
    private static final int REQUEST_DATE_FROM = 1;
    private static final int REQUEST_DATE_TO = 2;
    private static final int REQUEST_ACCOUNT = 3;
    // -----------------------------------------------------------------------------------------------------------------
    private static final String FRAGMENT_DATE_TIME = "FRAGMENT_DATE_TIME";
    // -----------------------------------------------------------------------------------------------------------------
    private FilterToggleView periodFilter_V;
    private FilterToggleView accountsFilter_V;
    private FilterToggleView categoriesFilter_V;
    private View periodContainer_V;
    private Button dateFrom_B;
    private Button dateTo_B;
    // -----------------------------------------------------------------------------------------------------------------
    private FilterHelper filterHelper;
    private boolean isExpanded = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_ACCOUNT: {
                if (resultCode == Activity.RESULT_OK) {
                    filterHelper.setAccountID(data.getLongExtra(AccountListFragment.RESULT_EXTRA_ITEM_ID, 0));
               }
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        periodFilter_V = (FilterToggleView) view.findViewById(R.id.periodFilter_V);
        accountsFilter_V = (FilterToggleView) view.findViewById(R.id.accountsFilter_V);
        categoriesFilter_V = (FilterToggleView) view.findViewById(R.id.categoriesFilter_V);
        periodContainer_V = view.findViewById(R.id.periodContainer_V);
        dateFrom_B = (Button) view.findViewById(R.id.dateFrom_B);
        dateTo_B = (Button) view.findViewById(R.id.dateTo_B);

        // Setup
        filterHelper = FilterHelper.getDefault(getActivity());
        periodFilter_V.setTitle(getString(R.string.period));
        accountsFilter_V.setTitle(getString(R.string.accounts));
        categoriesFilter_V.setTitle(getString(R.string.categories));
        periodFilter_V.setOnClickListener(this);
        accountsFilter_V.setOnClickListener(this);
        categoriesFilter_V.setOnClickListener(this);
        dateFrom_B.setOnClickListener(this);
        dateTo_B.setOnClickListener(this);
        periodFilter_V.setCallbacks(this);
        accountsFilter_V.setCallbacks(this);
        categoriesFilter_V.setCallbacks(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Restore date time dialog fragment
        final DateTimeDialog dateTime_F = (DateTimeDialog) getFragmentManager().findFragmentByTag(FRAGMENT_DATE_TIME);
        if (dateTime_F != null)
            dateTime_F.setListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Register events
        EventBus.getDefault().register(this, FilterHelper.FilterChangedEvent.class);

        update();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Unregister events
        EventBus.getDefault().unregister(this, FilterHelper.FilterChangedEvent.class);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.periodFilter_V:
            {
                if (isExpanded)
                    collapseFilter();
                else
                    expandFilter(v);
                break;
            }

            case R.id.accountsFilter_V:
            {
                AccountListActivity.startListSelection(getActivity(), this, REQUEST_ACCOUNT);
                break;
            }
            case R.id.categoriesFilter_V:
            {
                break;
            }


            case R.id.dateFrom_B:
                DateTimeDialog.newDateDialogInstance(this, REQUEST_DATE_FROM, filterHelper.getPeriodStart() > 0 ? filterHelper.getPeriodStart() : System.currentTimeMillis()).show(getFragmentManager(), FRAGMENT_DATE_TIME);
                break;

            case R.id.dateTo_B:
                DateTimeDialog.newDateDialogInstance(this, REQUEST_DATE_TO, filterHelper.getPeriodEnd() > 0 ? filterHelper.getPeriodEnd() : System.currentTimeMillis()).show(getFragmentManager(), FRAGMENT_DATE_TIME);
                break;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(FilterHelper.FilterChangedEvent event)
    {
        update();
    }

    @Override
    public void onDateSelected(int requestCode, long date)
    {
        if (requestCode == REQUEST_DATE_FROM)
            filterHelper.setPeriodStart(date);
        else if (requestCode == REQUEST_DATE_TO)
            filterHelper.setPeriodEnd(date);
    }

    @Override
    public void onFilterClearClick(FilterToggleView v)
    {
        switch (v.getId())
        {
            case R.id.periodFilter_V:
                filterHelper.clearPeriod();
                break;
            case R.id.accountsFilter_V:
                filterHelper.clearAccount();
                break;
        }
    }

    private void expandFilter(View v)
    {
        if (v == null)
            return;

        if (periodFilter_V.equals(v))
        {
            // TODO Set period values
            periodFilter_V.setVisibility(View.VISIBLE);
            periodContainer_V.setVisibility(View.VISIBLE);
        }
        else
        {
            periodFilter_V.setVisibility(View.GONE);
            periodContainer_V.setVisibility(View.GONE);
        }

        accountsFilter_V.setVisibility(View.VISIBLE);

        if (categoriesFilter_V.equals(v))
        {
            categoriesFilter_V.setVisibility(View.VISIBLE);
        }
        else
        {
            categoriesFilter_V.setVisibility(View.GONE);
        }

        isExpanded = true;
    }

    private void collapseFilter()
    {
        isExpanded = false;
        update();
    }

    private void update()
    {
        final String notSetStr = getString(R.string.not_set);

        if (filterHelper.isPeriodSet())
        {
            periodFilter_V.setFilterSet(true);
            periodFilter_V.setDescription(PeriodHelper.getPeriodShortTitle(getActivity(), PeriodHelper.TYPE_CUSTOM, filterHelper.getPeriodStart(), filterHelper.getPeriodEnd()));

            if (filterHelper.getPeriodStart() > 0)
                dateFrom_B.setText(DateTimeFormat.mediumDate().print(filterHelper.getPeriodStart()));
            else
                dateFrom_B.setText(notSetStr);

            if (filterHelper.getPeriodEnd() > 0)
                dateTo_B.setText(DateTimeFormat.mediumDate().print(filterHelper.getPeriodEnd()));
            else
                dateTo_B.setText(notSetStr);

        }
        else
        {
            periodFilter_V.setFilterSet(false);
            periodFilter_V.setDescription(notSetStr);
            dateFrom_B.setText(notSetStr);
            dateTo_B.setText(notSetStr);
        }

        if(filterHelper.isAccountSet()) {
            accountsFilter_V.setFilterSet(true);
            accountsFilter_V.setDescription(filterHelper.getAccountName());
        }
        else {
            accountsFilter_V.setFilterSet(false);
            accountsFilter_V.setDescription(notSetStr);
        }
        categoriesFilter_V.setDescription(notSetStr);

        if (!isExpanded)
        {
            periodFilter_V.setVisibility(View.VISIBLE);
            categoriesFilter_V.setVisibility(View.GONE);

            periodContainer_V.setVisibility(View.GONE);
        }
    }
}