package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.common.utils.Preconditions;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

public abstract class BaseInterval {
    protected final Context context;
    protected final EventBus eventBus;

    protected Type type;
    protected int length;

    protected Interval interval;

    protected BaseInterval(Context context, EventBus eventBus, Type type, int length) {
        this.context = Preconditions.checkNotNull(context, "Context cannot be null.");
        this.eventBus = Preconditions.checkNotNull(eventBus, "EventBus cannot be null.");
        this.type = Preconditions.checkNotNull(type, "Type cannot be null.");
        this.length = Preconditions.checkMore(length, 0, "Length must be > 0.");
        updateInterval();
    }

    public Type getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public void setTypeAndLength(Type type, int length) {
        Preconditions.checkNotNull(type, "Type cannot be null.");
        Preconditions.checkMore(length, 0, "Length must be > 0.");
        final boolean changed = this.type != type || this.length != length;
        this.type = type;
        this.length = length;
        if (changed) {
            updateInterval();
        }
    }

    public Interval getInterval() {
        return interval;
    }

    public String getTitle() {
        switch (type) {
            case DAY:
                return DateTimeFormat.mediumDate().print(interval.getStart());
            case WEEK:
                return DateUtils.formatDateTime(context, interval.getStart(), DateUtils.FORMAT_ABBREV_ALL) + " - " + DateUtils.formatDateTime(context, interval.getEnd().minusMillis(1), DateUtils.FORMAT_ABBREV_ALL);
            case MONTH:
                return interval.getStart().monthOfYear().getAsText();
            case YEAR:
                return interval.getStart().year().getAsText();
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

    public String getTypeTitle() {
        switch (type) {
            case DAY:
                return context.getString(R.string.day);
            case WEEK:
                return context.getString(R.string.week);
            case MONTH:
                return context.getString(R.string.month);
            case YEAR:
                return context.getString(R.string.year);
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

    protected void updateInterval() {
        final DateTime currentTime = new DateTime(System.currentTimeMillis());
        final DateTime intervalStart;
        final Period period;

        switch (type) {
            case DAY:
                intervalStart = currentTime.withTimeAtStartOfDay();
                period = Period.days(length);
                break;
            case WEEK:
                intervalStart = currentTime.weekOfWeekyear().roundFloorCopy();
                period = Period.weeks(length);
                break;
            case MONTH:
                intervalStart = currentTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
                period = Period.months(length);
                break;
            case YEAR:
                intervalStart = currentTime.withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
                period = Period.years(length);
                break;
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }

        interval = new Interval(intervalStart, period);
        notifyChanged();
    }

    protected void notifyChanged() {
        eventBus.post(this);
    }

    public static enum Type {
        DAY, WEEK, MONTH, YEAR
    }
}
