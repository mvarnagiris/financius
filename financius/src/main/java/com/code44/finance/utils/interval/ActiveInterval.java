package com.code44.finance.utils.interval;

import android.content.Context;

import com.code44.finance.utils.EventBus;
import com.squareup.otto.Produce;

import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * Interval based on user's choice.
 */
public class ActiveInterval extends BaseInterval {
    public ActiveInterval(Context context, EventBus eventBus, Type type, int length) {
        super(context, eventBus, type, length);
        eventBus.register(this);
    }

    @Produce public ActiveInterval produceActiveInterval() {
        return this;
    }

    public void previous() {
        final Period period = getPeriodForType();
        interval = new Interval(period, interval.getStart());
        notifyChanged();
    }

    public void next() {
        final Period period = getPeriodForType();
        interval = new Interval(interval.getEnd(), period);
        notifyChanged();
    }
}
