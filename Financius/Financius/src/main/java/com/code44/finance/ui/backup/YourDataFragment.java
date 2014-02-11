package com.code44.finance.ui.backup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.services.BackupService;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.ui.dialogs.DateTimeDialog;
import com.code44.finance.ui.dialogs.ProgressDialog;
import com.code44.finance.views.cards.CSVCardView;
import de.greenrobot.event.EventBus;

public class YourDataFragment extends BaseFragment implements CSVCardView.Callback, DateTimeDialog.DialogCallbacks
{
    private static final String FRAGMENT_DATE_TIME = "FRAGMENT_DATE_TIME";
    // -----------------------------------------------------------------------------------------------------------------
    private CSVCardView csvCard_V;

    public static YourDataFragment newInstance()
    {
        return new YourDataFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_your_data, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        csvCard_V = (CSVCardView) view.findViewById(R.id.csvCard_V);

        // Setup
        csvCard_V.setCallback(this);
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
        EventBus.getDefault().registerSticky(this, BackupService.CSVExportEvent.class);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Unregister events
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestCSVPeriod(int requestCode, long date)
    {
        DateTimeDialog.newDateDialogInstance(this, requestCode, date).show(getFragmentManager(), FRAGMENT_DATE_TIME);
    }

    @Override
    public void onExportCSV(long dateFrom, long dateTo)
    {
        API.exportCSV(getActivity(), dateFrom, dateTo);
    }

    @Override
    public void onDateSelected(int requestCode, long date)
    {
        csvCard_V.setDate(requestCode, date);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(BackupService.CSVExportEvent event)
    {
        if (event.isWorking(true))
            ProgressDialog.showDialog(getFragmentManager(), getString(R.string.please_wait));
        else if (event.isFinished())
        {
            ProgressDialog.dismissDialog(getFragmentManager());

            if (event.isSuccessful())
                Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT).show();
        }
    }
}