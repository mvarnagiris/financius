package com.code44.finance.ui.transactions.controllers;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

public class DateTimeViewController extends ViewController {
    private final ImageView dateTimeImageView;
    private final Button dateButton;
    private final Button timeButton;

    public DateTimeViewController(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        dateTimeImageView = findView(activity, R.id.dateTimeImageView);
        dateButton = findView(activity, R.id.dateButton);
        timeButton = findView(activity, R.id.timeButton);

        dateTimeImageView.setOnClickListener(clickListener);
        dateButton.setOnClickListener(clickListener);
        dateButton.setOnLongClickListener(longClickListener);
        timeButton.setOnClickListener(clickListener);
        timeButton.setOnLongClickListener(longClickListener);
    }

    @Override public void showError(Throwable error) {
    }

    public void setDateTime(long date) {
        final DateTime dateTime = new DateTime(date);
        dateButton.setText(DateUtils.formatDateTime(dateButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_DATE));
        timeButton.setText(DateUtils.formatDateTime(timeButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_TIME));
    }

    public void isSetByUser(boolean isSetByUser) {
//        dateTimeImageView.setImageAlpha(isSetByUser ? 255 : 64);
    }
}
