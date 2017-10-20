package com.code44.finance.ui.transactions.edit.presenters;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.Presenter;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

public class DateTimePresenter extends Presenter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_LATEST_DATE = 1001;

    private final Button dateButton;
    private final Button timeButton;

    private BaseActivity activity;

    public DateTimePresenter(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        dateButton = findView(activity, R.id.dateButton);
        timeButton = findView(activity, R.id.timeButton);

        dateButton.setOnClickListener(clickListener);
        dateButton.setOnLongClickListener(longClickListener);
        timeButton.setOnClickListener(clickListener);
        timeButton.setOnLongClickListener(longClickListener);

        this.activity = activity;
    }

    public void setDateTime(long date) {
        final DateTime dateTime = new DateTime(date);
        dateButton.setText(DateUtils.formatDateTime(dateButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_DATE));
        timeButton.setText(DateUtils.formatDateTime(timeButton.getContext(), dateTime, DateUtils.FORMAT_SHOW_TIME));
    }

    public void isSetByUser(boolean isSetByUser) {
//        dateTimeImageView.setImageAlpha(isSetByUser ? 255 : 64);
    }

    public void loadLatestTransactionDate() {
        activity.getSupportLoaderManager().initLoader(LOADER_LATEST_DATE, null, this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String localIdName = Tables.Transactions.TABLE_NAME + "." + Tables.Transactions.LOCAL_ID;
        final Query query = Query.create()
                .projection(Tables.Transactions.DATE.getName())
                .selection(localIdName + "=(SELECT MAX("
                        + localIdName
                        + ") FROM " + Tables.Transactions.TABLE_NAME + ")");
        return query.asCursorLoader(dateButton.getContext(), TransactionsProvider.uriTransactions());
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            setDateTime(data.getLong(data.getColumnIndex(Tables.Transactions.DATE.getName())));
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {}
}
