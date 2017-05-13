package com.code44.finance.utils.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Screens {
    private final Tracker tracker;

    public Screens(Tracker tracker) {
        this.tracker = tracker;
    }

    public void trackScreen(Screens.Screen screen) {
        tracker.setScreenName(screen.getName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void clearScreen() {
        tracker.setScreenName(Screens.Screen.None.getName());
    }

    public enum Screen {
        None(null),
        Overview("Overview"),
        AccountList("Account list"),
        Account("Account"),
        AccountEdit("Account edit"),
        TransactionList("Transaction list"),
        Transaction("Transaction"),
        TransactionEdit("Transaction edit"),
        CategoriesReport("Categories report"),
        Settings("Settings"),
        About("About"),
        YourData("Your data"),
        Export("Export"),
        Import("Import"),
        CurrencyList("Currency list"),
        Currency("Currency"),
        CurrencyEdit("Currency edit"),
        CategoryList("Category list"),
        Category("Category"),
        CategoryEdit("Category edit"),
        TagList("Tag list"),
        Tag("Tag"),
        TagEdit("Tag edit"),
        Login("Login"),
        Profile("Profile");

        private final String name;

        Screen(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
