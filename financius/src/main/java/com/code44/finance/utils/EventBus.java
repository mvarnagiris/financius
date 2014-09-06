package com.code44.finance.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Inject;

public class EventBus extends Bus {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Inject public EventBus() {
        super(ThreadEnforcer.ANY);
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
