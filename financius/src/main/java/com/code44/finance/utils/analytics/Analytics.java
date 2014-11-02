package com.code44.finance.utils.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Analytics {
    private final Tracker tracker;

    public Analytics(Tracker tracker) {
        this.tracker = tracker;
    }

    public void setUserId(String userId) {
        tracker.set("&uid", userId);
    }

    public void trackScreen(Screen screen) {
        tracker.setScreenName(screen.getName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void clearScreen() {
        tracker.setScreenName(Screen.None.getName());
    }

    public static enum Screen {
        None(null),
        Overview("Overview"),
        AccountList("Account list"),
        TransactionList("Transaction list"),
        CategoriesReport("Categories report");

        private final String name;

        private Screen(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
