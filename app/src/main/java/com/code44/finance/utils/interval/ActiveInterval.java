package com.code44.finance.utils.interval;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.squareup.otto.Produce;

import org.joda.time.Interval;
import org.joda.time.Period;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Interval based on user's choice.
 */
@Singleton
public class ActiveInterval extends BaseInterval {
    @Inject public ActiveInterval(@ApplicationContext Context context, EventBus eventBus, GeneralPrefs generalPrefs) {
        super(context, eventBus, generalPrefs.getIntervalIntervalType(), generalPrefs.getIntervalLength());
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
