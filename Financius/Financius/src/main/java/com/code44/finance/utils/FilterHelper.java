package com.code44.finance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import de.greenrobot.event.EventBus;

public class FilterHelper
{
    private static final String PREFIX = "filter_helper_";
    // -----------------------------------------------------------------------------------------------------------------
    private static FilterHelper instance;
    // -----------------------------------------------------------------------------------------------------------------
    private Context context;
    private long periodStart;
    private long periodEnd;

    private FilterHelper(Context context)
    {
        this.context = context.getApplicationContext();

        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        periodStart = prefs.getLong(PREFIX + "periodStart", 0);
        periodEnd = prefs.getLong(PREFIX + "periodEnd", 0);
    }

    public static FilterHelper getDefault(Context context)
    {
        if (instance == null)
            instance = new FilterHelper(context);
        return instance;
    }

    public long getPeriodStart()
    {
        return periodStart;
    }

    public void setPeriodStart(long periodStart)
    {
        this.periodStart = periodStart;
        PrefsHelper.getPrefs(context).edit().putLong(PREFIX + "periodStart", periodStart).apply();
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public long getPeriodEnd()
    {
        return periodEnd;
    }

    public void setPeriodEnd(long periodEnd)
    {
        this.periodEnd = periodEnd;
        PrefsHelper.getPrefs(context).edit().putLong(PREFIX + "periodEnd", periodEnd).apply();
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public boolean isPeriodSet()
    {
        return periodStart > 0 || periodEnd > 0;
    }

    public void clearPeriod()
    {
        periodStart = 0;
        periodEnd = 0;
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public void clearAll()
    {
        // clearPeriod
        periodStart = 0;
        periodEnd = 0;

        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public static class FilterChangedEvent
    {
    }
}