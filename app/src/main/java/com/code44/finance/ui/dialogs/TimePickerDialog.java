package com.code44.finance.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.widget.TimePicker;

import org.joda.time.DateTime;

public class TimePickerDialog extends BaseDialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {
    private static final String ARG_HOUR_OF_DAY = "ARG_HOUR_OF_DAY";
    private static final String ARG_MINUTE = "ARG_MINUTE";

    private static final String FRAGMENT_TIME_PICKER = TimePickerDialog.class.getName() + ".FRAGMENT_TIME_PICKER";

    public static void show(FragmentManager fragmentManager, int requestCode, long timestamp) {
        final DateTime date = new DateTime(timestamp);
        show(fragmentManager, requestCode, date.getHourOfDay(), date.getMinuteOfHour());
    }

    public static void show(FragmentManager fragmentManager, int requestCode, int hourOfDay, int minute) {
        final Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_HOUR_OF_DAY, hourOfDay);
        args.putInt(ARG_MINUTE, minute);

        final TimePickerDialog fragment = new TimePickerDialog();
        fragment.setArguments(args);
        fragment.show(fragmentManager, FRAGMENT_TIME_PICKER);
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int hourOfDay = getArguments().getInt(ARG_HOUR_OF_DAY);
        final int minute = getArguments().getInt(ARG_MINUTE);

        return new android.app.TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
    }

    @Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        getEventBus().post(new TimeSelected(getArguments().getInt(ARG_REQUEST_CODE), hourOfDay, minute));
    }

    public static final class TimeSelected {
        private final int requestCode;
        private final int hourOfDay;
        private final int minute;

        public TimeSelected(int requestCode, int hourOfDay, int minute) {
            this.requestCode = requestCode;
            this.hourOfDay = hourOfDay;
            this.minute = minute;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public int getHourOfDay() {
            return hourOfDay;
        }

        public int getMinute() {
            return minute;
        }
    }
}
