package com.code44.finance.utils;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class EventBus extends Bus {
    private static EventBus singleton;

    public EventBus() {
        super(ThreadEnforcer.MAIN);
    }

    public static synchronized EventBus get() {
        if (singleton == null) {
            singleton = new EventBus();
        }
        return singleton;
    }
}
