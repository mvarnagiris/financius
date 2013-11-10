package com.code44.finance.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class DateTimeDialog extends DialogFragment
{
    private static final int DIALOG_ID_DATE = 1;
    private static final int DIALOG_ID_TIME = 2;
    private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";
    private static final String ARG_DIALOG_ID = "ARG_DIALOG_ID";
    private static final String ARG_DATE = "ARG_DATE";
    private DialogCallbacks listener;

    public static DateTimeDialog newDateDialogInstance(DialogCallbacks listener, int requestCode, long timeMillis)
    {
        DateTimeDialog f = new DateTimeDialog();

        final Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_DIALOG_ID, DIALOG_ID_DATE);
        args.putLong(ARG_DATE, timeMillis);
        f.setArguments(args);
        f.setListener(listener);

        return f;
    }

    public static DateTimeDialog newTimeDialogInstance(DialogCallbacks listener, int requestCode, long timeMillis)
    {
        DateTimeDialog f = new DateTimeDialog();

        final Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_DIALOG_ID, DIALOG_ID_TIME);
        args.putLong(ARG_DATE, timeMillis);
        f.setArguments(args);
        f.listener = listener;

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Get arguments
        Bundle args = getArguments();

        final int requestCode = args.getInt(ARG_REQUEST_CODE, 0);
        final int dialogId = args.getInt(ARG_DIALOG_ID);
        final Calendar c = Calendar.getInstance();
        c.setTime(new Date(args.getLong(ARG_DATE)));

        switch (dialogId)
        {
            case DIALOG_ID_DATE:
                return new DatePickerDialog(getActivity(), new OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (listener != null)
                            listener.onDateSelected(requestCode, c.getTimeInMillis());
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            case DIALOG_ID_TIME:
            {
                return new TimePickerDialog(getActivity(), new OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        if (listener != null)
                            listener.onDateSelected(requestCode, c.getTimeInMillis());
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
            }
        }

        return null;
    }

    public void setListener(DialogCallbacks listener)
    {
        this.listener = listener;
    }

    public static interface DialogCallbacks
    {
        public void onDateSelected(int requestCode, long date);
    }
}