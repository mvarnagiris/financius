package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.App;
import com.code44.finance.R;
import com.squareup.otto.Produce;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

public class IntervalHelper extends Prefs {
    private static final String PREFIX = "interval_helper_";

    private static IntervalHelper singleton;

    private final EventBus eventBus;

    private Type type;
    private int intervalLength;

    private Interval currentInterval;

    public IntervalHelper(Context context, EventBus eventBus) {
        super(context);
        this.eventBus = eventBus;

        refresh();

        eventBus.register(this);
    }

    public static IntervalHelper get() {
        if (singleton == null) {
            singleton = new IntervalHelper(App.getContext(), EventBus.get());
        }
        return singleton;
    }

    public static Period getPeriod(int intervalLength, Type type) {
        switch (type) {
            case DAY:
                return Period.days(intervalLength);
            case WEEK:
                return Period.weeks(intervalLength);
            case MONTH:
                return Period.months(intervalLength);
            case YEAR:
                return Period.years(intervalLength);
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

    public static Interval getInterval(long millis, Period period, Type type) {
        final DateTime currentTime = new DateTime(millis);

        final DateTime intervalStart;
        switch (type) {
            case DAY:
                intervalStart = currentTime.withTimeAtStartOfDay();
                break;
            case WEEK:
                intervalStart = currentTime.weekOfWeekyear().roundFloorCopy();
                break;
            case MONTH:
                intervalStart = currentTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
                break;
            case YEAR:
                intervalStart = currentTime.withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }

        return new Interval(intervalStart, period);
    }

    public static String getIntervalTitle(Context context, Interval interval, Type type) {
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

    @Override protected String getPrefix() {
        return PREFIX;
    }

    @Produce public IntervalHelper produceIntervalHelper() {
        return this;
    }

    public void refresh() {
        type = Type.valueOf(getString("type", Type.MONTH.toString()));
        intervalLength = getInteger("intervalLength", 1);

        invalidateCurrentIntervalIfNecessary();
        notifyChanged();
    }

    public Interval getCurrentInterval() {
        return currentInterval;
    }

    public String getCurrentIntervalTitle() {
        return getIntervalTitle(getContext(), currentInterval, type);
    }

    public String getIntervalTypeTitle() {
        switch (type) {
            case DAY:
                return getContext().getString(R.string.day);
            case WEEK:
                return getContext().getString(R.string.week);
            case MONTH:
                return getContext().getString(R.string.month);
            case YEAR:
                return getContext().getString(R.string.year);
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
    }

    public Type getType() {
        return type;
    }

    public int getIntervalLength() {
        return intervalLength;
    }

    public void setTypeAndLength(Type type, int intervalLength) {
        this.type = type;
        this.intervalLength = intervalLength;
        setString("type", type.toString());
        setInteger("intervalLength", intervalLength);
        currentInterval = null;
        refresh();
    }

    private void invalidateCurrentIntervalIfNecessary() {
        final long currentTime = System.currentTimeMillis();
        if (currentInterval != null && currentInterval.getEndMillis() > currentTime) {
            return;
        }

        currentInterval = getInterval(currentTime, getPeriod(), getType());
    }

    private Period getPeriod() {
        return getPeriod(intervalLength, type);
    }

    private void notifyChanged() {
        eventBus.post(this);
    }

    public static enum Type {
        DAY, WEEK, MONTH, YEAR
    }
}
