package com.code44.finance.utils.interval;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.utils.EventBus;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BaseInterval {
    protected final Context context;
    protected final EventBus eventBus;

    protected IntervalType intervalType;
    protected int length;

    protected Interval interval;

    protected BaseInterval(Context context, EventBus eventBus, IntervalType intervalType, int length) {
        this.context = checkNotNull(context, "Context cannot be null.");
        this.eventBus = checkNotNull(eventBus, "EventBus cannot be null.");
        this.intervalType = checkNotNull(intervalType, "Type cannot be null.");
        checkArgument(length > 0, "Length must be > 0.");
        this.length = length;
        reset();
    }

    public static Period getPeriod(IntervalType intervalType, int length) {
        final Period period;
        switch (intervalType) {
            case Day:
                period = Period.days(length);
                break;
            case Week:
                period = Period.weeks(length);
                break;
            case Month:
                period = Period.months(length);
                break;
            case Year:
                period = Period.years(length);
                break;
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
        return period;
    }

    public static Period getSubPeriod(IntervalType intervalType, int length) {
        final Period period;
        switch (intervalType) {
            case Day:
                period = Period.hours(length);
                break;
            case Week:
                period = Period.days(length);
                break;
            case Month:
                period = Period.days(length);
                break;
            case Year:
                period = Period.months(length);
                break;
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
        return period;
    }

    public static Interval getInterval(long millis, Period period, IntervalType intervalType) {
        final DateTime currentTime = new DateTime(millis);
        final DateTime intervalStart;

        switch (intervalType) {
            case Day:
                intervalStart = currentTime.withTimeAtStartOfDay();
                break;
            case Week:
                intervalStart = currentTime.weekOfWeekyear().roundFloorCopy();
                break;
            case Month:
                intervalStart = currentTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
                break;
            case Year:
                intervalStart = currentTime.withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }

        return new Interval(intervalStart, period);
    }

    public static String getTitle(Context context, Interval interval, IntervalType intervalType) {
        switch (intervalType) {
            case Day:
                return DateTimeFormat.mediumDate().print(interval.getStart());
            case Week:
                return DateUtils.formatDateTime(context, interval.getStart(), DateUtils.FORMAT_ABBREV_ALL) + " - " + DateUtils.formatDateTime(context, interval
                        .getEnd()
                        .minusMillis(1), DateUtils.FORMAT_ABBREV_ALL);
            case Month:
                return DateUtils.formatDateTime(context, interval.getStart(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY);
            case Year:
                return interval.getStart().year().getAsText();
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
    }

    public static String getSubTypeShortestTitle(Interval interval, IntervalType intervalType) {
        switch (intervalType) {
            case Day:
                return interval.getStart().hourOfDay().getAsShortText();
            case Week:
                return interval.getStart().dayOfWeek().getAsShortText();
            case Month:
                return interval.getStart().dayOfMonth().getAsShortText();
            case Year:
                return interval.getStart().monthOfYear().getAsShortText();
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
    }

    public IntervalType getIntervalType() {
        return intervalType;
    }

    public int getLength() {
        return length;
    }

    public void setTypeAndLength(IntervalType intervalType, int length) {
        checkNotNull(intervalType, "Type cannot be null.");
        checkArgument(length > 0, "Length must be > 0.");
        final boolean changed = this.intervalType != intervalType || this.length != length;
        this.intervalType = intervalType;
        this.length = length;
        if (changed) {
            reset();
        }
    }

    public Interval getInterval() {
        return interval;
    }

    public String getTitle() {
        return getTitle(context, interval, intervalType);
    }

    public String getTypeTitle() {
        switch (intervalType) {
            case Day:
                return context.getString(R.string.day);
            case Week:
                return context.getString(R.string.week);
            case Month:
                return context.getString(R.string.month);
            case Year:
                return context.getString(R.string.year);
            default:
                throw new IllegalArgumentException("Type " + intervalType + " is not supported.");
        }
    }

    public void reset() {
        interval = getInterval(System.currentTimeMillis(), getPeriodForType(), intervalType);
        notifyChanged();
    }

    protected Period getPeriodForType() {
        return getPeriod(intervalType, length);
    }

    protected void notifyChanged() {
        eventBus.post(this);
    }
}
