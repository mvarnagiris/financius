package com.code44.finance.utils;

import android.content.Context;

import com.squareup.otto.Produce;

/**
 * Interval based on user settings and phone timestamp.
 */
public class CurrentInterval extends BaseInterval {
    public CurrentInterval(Context context, EventBus eventBus, Type type, int length) {
        super(context, eventBus, type, length);
        eventBus.register(this);
    }

    @Produce public CurrentInterval produceCurrentInterval() {
        return this;
    }
}
