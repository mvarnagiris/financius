package com.code44.finance.utils.analytics;

import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Analytics {
    private final Tracker tracker;
    private final Screens screens;
    private final Events events;

    @Inject public Analytics(@AppTracker Tracker tracker) {
        this.tracker = tracker;
        screens = new Screens(tracker);
        events = new Events(tracker);
    }

    public Screens screen() {
        return screens;
    }

    public Events event() {
        return events;
    }

    public void setUserId(String userId) {
        tracker.set("&uid", userId);
    }
}
