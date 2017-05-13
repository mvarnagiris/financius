package com.code44.finance.utils.interval;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.squareup.otto.Produce;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Interval based on user settings and phone timestamp.
 */
@Singleton
public class CurrentInterval extends BaseInterval {
    @Inject public CurrentInterval(@ApplicationContext Context context, EventBus eventBus, GeneralPrefs generalPrefs) {
        super(context, eventBus, generalPrefs.getIntervalIntervalType(), generalPrefs.getIntervalLength());
        eventBus.register(this);
    }

    @Produce public CurrentInterval produceCurrentInterval() {
        return this;
    }
}
