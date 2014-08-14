package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.App;
import com.code44.finance.R;
import com.squareup.otto.Produce;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;

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

    public String getCurrentIntervalTitle() {
        switch (type) {
            case DAY:
                return currentInterval.getStart().dayOfMonth().getAsText();
            case WEEK:
                return currentInterval.getStart().weekOfWeekyear().getAsText();
            case MONTH:
                return currentInterval.getStart().monthOfYear().getAsText();
            case YEAR:
                return currentInterval.getStart().year().getAsText();
            default:
                throw new IllegalArgumentException("Type " + type + " is not supported.");
        }
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
        final DateTime currentTime = new DateTime();
        if (currentInterval != null && currentInterval.getEndMillis() > currentTime.getMillis()) {
            return;
        }

        currentInterval = new Interval(currentTime.dayOfMonth().withMinimumValue(), getPeriod());
    }

    private ReadablePeriod getPeriod() {
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

    private void notifyChanged() {
        eventBus.post(this);
    }

    public static enum Type {
        DAY, WEEK, MONTH, YEAR
    }
}
