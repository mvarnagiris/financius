package com.code44.finance.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class EventBus extends Bus {
    private static EventBus singleton;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public EventBus() {
        super(ThreadEnforcer.ANY);
    }

    public static synchronized EventBus get() {
        if (singleton == null) {
            singleton = new EventBus();
        }
        return singleton;
    }

    @Override public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    EventBus.super.post(event);
                }
            });
        }
    }
}
