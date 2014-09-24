package com.code44.finance.utils;

import android.content.Context;

import com.squareup.otto.Produce;

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
        // TODO Implement
        notifyChanged();
    }

    public void next() {
        // TODO Implement
        notifyChanged();
    }

    public void reset() {
        // TODO Implement
        notifyChanged();
    }
}
