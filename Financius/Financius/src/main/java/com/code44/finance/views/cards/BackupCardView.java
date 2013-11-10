package com.code44.finance.views.cards;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import com.code44.finance.R;

public class BackupCardView extends BigTextCardView
{
    @SuppressWarnings("UnusedDeclaration")
    public BackupCardView(Context context)
    {
        this(context, null);
    }

    public BackupCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BackupCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        final BackupCardInfo info = new BackupCardInfo(context);
        if (isInEditMode())
            info.setLastBackupDate(0);
        setCardInfo(info);
    }

    public static class BackupCardInfo extends BigTextCardInfo
    {
        private Context context;

        public BackupCardInfo(Context context)
        {
            super(0);
            this.context = context.getApplicationContext();

            setTitle(context.getString(R.string.backup));
            setLastBackupDate(0);
        }

        public BackupCardInfo setLastBackupDate(long date)
        {
            setSecondaryTitle(date == 0 ? null : DateUtils.getRelativeDateTimeString(context, date, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
            return this;
        }
    }
}