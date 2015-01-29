package com.code44.finance.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;

import com.code44.finance.R;

import org.joda.time.DateTime;

public class DatePickerDialog extends BaseDialogFragment implements android.app.DatePickerDialog.OnDateSetListener {
    private static final String ARG_YEAR = "ARG_YEAR";
    private static final String ARG_MONTH_OF_YEAR = "ARG_MONTH_OF_YEAR";
    private static final String ARG_DAY_OF_MONTH = "ARG_DAY_OF_MONTH";

    private static final String FRAGMENT_DATE_PICKER = DatePickerDialog.class.getName() + ".FRAGMENT_DATE_PICKER";

    public static void show(FragmentManager fragmentManager, int requestCode, long timestamp) {
        final DateTime date = new DateTime(timestamp);
        show(fragmentManager, requestCode, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

    public static void show(FragmentManager fragmentManager, int requestCode, int year, int monthOfYear, int dayOfMonth) {
        final Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH_OF_YEAR, monthOfYear);
        args.putInt(ARG_DAY_OF_MONTH, dayOfMonth);

        final DatePickerDialog fragment = new DatePickerDialog();
        fragment.setArguments(args);
        fragment.show(fragmentManager, FRAGMENT_DATE_PICKER);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppTheme);
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int year = getArguments().getInt(ARG_YEAR);
        final int monthOfYear = getArguments().getInt(ARG_MONTH_OF_YEAR);
        final int dayOfMonth = getArguments().getInt(ARG_DAY_OF_MONTH);

        return new android.app.DatePickerDialog(getActivity(), this, year, monthOfYear - 1, dayOfMonth);
    }

    @Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        getEventBus().post(new DateSelected(getArguments().getInt(ARG_REQUEST_CODE), year, monthOfYear + 1, dayOfMonth));
    }

    public static final class DateSelected {
        private final int requestCode;
        private final int year;
        private final int monthOfYear;
        private final int dayOfMonth;

        public DateSelected(int requestCode, int year, int monthOfYear, int dayOfMonth) {
            this.requestCode = requestCode;
            this.year = year;
            this.monthOfYear = monthOfYear;
            this.dayOfMonth = dayOfMonth;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public int getYear() {
            return year;
        }

        public int getMonthOfYear() {
            return monthOfYear;
        }

        public int getDayOfMonth() {
            return dayOfMonth;
        }
    }
}
