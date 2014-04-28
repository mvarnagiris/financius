package com.code44.finance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import de.greenrobot.event.EventBus;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.Calendar;

@SuppressWarnings("UnusedDeclaration")
public class PeriodHelper
{
    public static final int TYPE_DAY = 1;
    public static final int TYPE_WEEK = 2;
    public static final int TYPE_MONTH = 3;
    public static final int TYPE_YEAR = 4;
    public static final int TYPE_CUSTOM = 5;
    // -----------------------------------------------------------------------------------------------------------------
    private static PeriodHelper instance = null;
    private final Context context;
    // -----------------------------------------------------------------------------------------------------------------
    private int type;
    private long activeStart;
    private long activeEnd;

    private PeriodHelper(Context context)
    {
        this.context = context.getApplicationContext();

        // Read period preferences
        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        type = prefs.getInt(PrefsHelper.PREF_PERIOD_TYPE, TYPE_MONTH);
        activeStart = prefs.getLong(PrefsHelper.PREF_PERIOD_ACTIVE_START, getCurrentStart());
        activeEnd = prefs.getLong(PrefsHelper.PREF_PERIOD_ACTIVE_END, getCurrentEnd());
    }

    public static PeriodHelper getDefault(Context context)
    {
        if (instance == null)
            instance = new PeriodHelper(context);
        return instance;
    }

    /**
     * Calculates how many hours are in given period. If one of the dates only partially covers the hour, it still counts as full hour.
     *
     * @param start Start of the period.
     * @param end   End of the period.
     * @return Number of days in given period. If {@code end < start}, returns -1.
     */
    public static int getHourCountInPeriod(long start, long end)
    {
        if (end < start)
            return -1;

        return (int) Math.ceil((double) (end - start) / DateUtils.HOUR_IN_MILLIS);
    }

    /**
     * Calculates how many days are in given period. If one of the dates only partially covers the day, it still counts as full day.
     *
     * @param start Start of the period.
     * @param end   End of the period.
     * @return Number of days in given period. If {@code end < start}, returns -1.
     */
    public static int getDayCountInPeriod(long start, long end)
    {
        if (end < start)
            return -1;

        return Days.daysBetween(new DateTime(start).withTimeAtStartOfDay(), new DateTime(end).withTimeAtStartOfDay()).getDays() + 1;
    }

    /**
     * Calculates how many months are in given period. If one of the dates only partially covers the month, it still counts as full month.
     *
     * @param start Start of the period.
     * @param end   End of the period.
     * @return Number of days in given period. If {@code end < start}, returns -1.
     */
    public static int getMonthCountInPeriod(long start, long end)
    {
        if (end < start)
            return -1;

        final Calendar cal = Calendar.getInstance();
        final int monthCountInYear = cal.getMaximum(Calendar.MONTH);

        cal.setTimeInMillis(start);
        final int startYear = cal.get(Calendar.YEAR);
        final int startMonth = cal.get(Calendar.MONTH) + 1;

        cal.setTimeInMillis(end);
        final int endYear = cal.get(Calendar.YEAR);
        final int endMonth = cal.get(Calendar.MONTH) + 1;

        int monthsCount;

        if (startYear != endYear)
        {
            monthsCount = monthCountInYear * Math.max(0, endYear - startYear - 1);
            monthsCount += monthCountInYear - startMonth + 1;
            monthsCount += endMonth;
        }
        else
        {
            monthsCount = endMonth - startMonth + 1;
        }

        return monthsCount;
    }

    public static int getItemsCounts(int periodType, long date)
    {
        switch (periodType)
        {
            case TYPE_DAY:
                return 24;

            case TYPE_WEEK:
                return 7;

            case TYPE_MONTH:
            {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(date);
                return c.getMaximum(Calendar.DAY_OF_MONTH);
            }

            case TYPE_YEAR:
            {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(date);
                return c.getMaximum(Calendar.MONTH) + 1;
            }
        }

        return 0;
    }

    public static String getPeriodTitle(Context context, int type, long start, long end)
    {
        final String result;
        switch (type)
        {
            case TYPE_DAY:
                result = DateUtils.formatDateTime(context, start, 0);
                break;

            case TYPE_WEEK:
                result = DateUtils.formatDateRange(context, start, end, 0);
                break;

            case TYPE_MONTH:
                final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
                builder.appendMonthOfYearText();

                if (new DateTime(System.currentTimeMillis()).year().get() != new DateTime(start).year().get())
                    builder.appendLiteral(' ').appendYear(4, 4);

                result = builder.toFormatter().print(start);
                break;

            case TYPE_YEAR:
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(start);
                result = String.valueOf(c.get(Calendar.YEAR));
                break;

            default:
                result = "?";
                break;
        }

        return result;
    }

    public static String getPeriodShortTitle(Context context, int type, long start, long end)
    {
        final String result;
        switch (type)
        {
            case TYPE_DAY:
                result = DateUtils.formatDateTime(context, start, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH);
                break;

            case TYPE_WEEK:
                result = DateUtils.formatDateRange(context, start, end, DateUtils.FORMAT_ABBREV_MONTH);
                break;

            case TYPE_MONTH:
                final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
                builder.appendMonthOfYearShortText();

                if (new DateTime(System.currentTimeMillis()).year().get() != new DateTime(start).year().get())
                    builder.appendLiteral(' ').appendTwoDigitYear(2);

                result = builder.toFormatter().print(start);
                break;

            case TYPE_YEAR:
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(start);
                result = String.valueOf(c.get(Calendar.YEAR));
                break;

            case TYPE_CUSTOM:
                if (start == 0 && end == 0)
                    result = "? - ?";
                else if (start > 0 && end > 0)
                    result = DateUtils.formatDateRange(context, start, end, DateUtils.FORMAT_ABBREV_MONTH);
                else if (start > 0)
                    result = DateTimeFormat.mediumDate().print(start) + " - ?";
                else
                    result = "? - " + DateTimeFormat.mediumDate().print(end);
                break;

            default:
                result = "?";
                break;
        }

        return result;
    }

    public static long getPeriodStart(int periodType, long date)
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);

        switch (periodType)
        {
            case TYPE_DAY:
            {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            }

            case TYPE_WEEK:
            {
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                final int currentDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 7 - cal.getFirstDayOfWeek()) % 7;
                cal.add(Calendar.DAY_OF_YEAR, -currentDayOfWeek);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            }

            case TYPE_MONTH:
            {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            }

            case TYPE_YEAR:
            {
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            }
        }
        return cal.getTimeInMillis();
    }

    public static long getPeriodEnd(int periodType, long date)
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getPeriodStart(periodType, date));

        switch (periodType)
        {
            case TYPE_DAY:
            {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                break;
            }

            case TYPE_WEEK:
            {
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            }

            case TYPE_MONTH:
            {
                cal.add(Calendar.MONTH, 1);
                break;
            }

            case TYPE_YEAR:
            {
                cal.add(Calendar.YEAR, 1);
                break;
            }
        }

        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTimeInMillis();
    }

    public long getCurrentStart()
    {
        return getPeriodStart(type, System.currentTimeMillis());
    }

    public long getCurrentEnd()
    {
        return getPeriodEnd(type, System.currentTimeMillis());
    }

    public long getActiveStart()
    {
        return activeStart;
    }

    public long getActiveEnd()
    {
        return activeEnd;
    }

    public void resetActive()
    {
        activeStart = getCurrentStart();
        activeEnd = getCurrentEnd();
        PrefsHelper.storeLong(context, PrefsHelper.PREF_PERIOD_ACTIVE_START, activeStart);
        PrefsHelper.storeLong(context, PrefsHelper.PREF_PERIOD_ACTIVE_END, activeEnd);
    }

    public void nextActive()
    {
        activeStart = activeEnd + 1;
        activeEnd = getPeriodEnd(type, activeStart);
        PrefsHelper.storeLong(context, PrefsHelper.PREF_PERIOD_ACTIVE_START, activeStart);
        PrefsHelper.storeLong(context, PrefsHelper.PREF_PERIOD_ACTIVE_END, activeEnd);
    }

    public void previousActive()
    {
        activeEnd = activeStart - 1;
        activeStart = getPeriodStart(type, activeEnd);
        PrefsHelper.storeLong(context, PrefsHelper.PREF_PERIOD_ACTIVE_START, activeStart);
        PrefsHelper.storeLong(context, PrefsHelper.PREF_PERIOD_ACTIVE_END, activeEnd);
    }

    public String getCurrentPeriodShortTitle()
    {
        return getPeriodShortTitle(context, type, getCurrentStart(), getCurrentEnd());
    }

    public String getActivePeriodShortTitle()
    {
        return getPeriodShortTitle(context, type, getActiveStart(), getActiveEnd());
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
        resetActive();
        PrefsHelper.storeInt(context, PrefsHelper.PREF_PERIOD_TYPE, type);
        EventBus.getDefault().post(new PeriodTypeChangedEvent());
        // TODO NotifyUtils.notifyAll(context);
    }

    public static class PeriodTypeChangedEvent
    {
    }
}