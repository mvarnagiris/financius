package com.code44.finance.views.cards;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;

import com.code44.finance.R;
import com.code44.finance.views.AutoResizeTextView;

public class TimeCardView extends CardViewV2
{
    private final AutoResizeTextView time_TV;
    // -----------------------------------------------------------------------------------------------------------------
    private long date;

    public TimeCardView(Context context)
    {
        this(context, null);
    }

    public TimeCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TimeCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        //noinspection ConstantConditions
        int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        container_V.setPadding(padding, container_V.getPaddingTop(), padding, container_V.getPaddingBottom());
        time_TV = new AutoResizeTextView(context);
        time_TV.setGravity(Gravity.CENTER_VERTICAL);
        time_TV.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        //noinspection ConstantConditions
        time_TV.setTextColor(getResources().getColor(R.color.text_primary));
        time_TV.setTextSize(getResources().getDimension(R.dimen.text_xxxlarge));
        time_TV.setMinTextSize(getResources().getDimension(R.dimen.text_xsmall));
        time_TV.setMinHeight(getResources().getDimensionPixelSize(R.dimen.recommended_touch_size) - container_V.getPaddingTop() - container_V.getPaddingBottom());
        setContentView(time_TV);

        setDate(System.currentTimeMillis());
    }

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
        time_TV.setText(DateUtils.formatDateTime(getContext(), date, DateUtils.FORMAT_SHOW_TIME));
    }
}
