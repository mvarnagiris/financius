package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.App;
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
    private int periodLength;

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
        periodLength = getInteger("periodLength", 1);

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

    public Type getType() {
        return type;
    }

    public int getPeriodLength() {
        return periodLength;
    }

    public void setTypeAndLength(Type type, int periodLength) {
        this.type = type;
        this.periodLength = periodLength;
        setString("type", type.toString());
        setInteger("periodLength", periodLength);
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
                return Period.days(periodLength);
            case WEEK:
                return Period.weeks(periodLength);
            case MONTH:
                return Period.months(periodLength);
            case YEAR:
                return Period.years(periodLength);
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
