package com.code44.finance.ui.transactions.controllers;

import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

public class DateTimeViewController extends ViewController {
    private Button dateButton;
    private Button timeButton;

    public DateTimeViewController(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        dateButton = findView(activity, R.id.dateButton);
        timeButton = findView(activity, R.id.timeButton);

        dateButton.setOnClickListener(clickListener);
        dateButton.setOnLongClickListener(longClickListener);
        timeButton.setOnClickListener(clickListener);
        timeButton.setOnLongClickListener(longClickListener);
    }

    @Override protected void showError(Throwable error) {
    }

    protected void setDateTime(long date) {
        final DateTime dateTime = new DateTime(date);
        dateButton.setText(DateUtils.formatDateTime(dateButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_DATE));
        timeButton.setText(DateUtils.formatDateTime(timeButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_TIME));
    }
}
