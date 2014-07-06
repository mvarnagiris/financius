package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.App;

import de.greenrobot.event.EventBus;

public class GeneralPrefs extends Prefs {
    private static final String PREFIX = "general_";

    private static GeneralPrefs singleton;

    private boolean isAutoUpdateCurrencies;
    private long autoUpdateCurrenciesTimestamp;

    private GeneralPrefs(Context context) {
        super(context);
        refresh();
    }

    public synchronized static GeneralPrefs get() {
        if (singleton == null) {
            singleton = new GeneralPrefs(App.getAppContext());
        }
        return singleton;
    }

    public static void notifyGeneralPrefsChanged() {
        EventBus.getDefault().post(new GeneralPrefsChanged());
    }

    public void refresh() {
        isAutoUpdateCurrencies = getBoolean("isAutoUpdateCurrencies", true);
        autoUpdateCurrenciesTimestamp = getLong("autoUpdateCurrenciesTimestamp", 0);
    }

    public void clear() {
        clear("isAutoUpdateCurrencies", "autoUpdateCurrenciesTimestamp");
        refresh();
    }

    public boolean isAutoUpdateCurrencies() {
        return isAutoUpdateCurrencies;
    }

    public void setAutoUpdateCurrencies(boolean isAutoUpdateCurrencies) {
        this.isAutoUpdateCurrencies = isAutoUpdateCurrencies;
        setBoolean("isAutoUpdateCurrencies", isAutoUpdateCurrencies);
    }

    public long getAutoUpdateCurrenciesTimestamp() {
        return autoUpdateCurrenciesTimestamp;
    }

    public void setAutoUpdateCurrenciesTimestamp(long autoUpdateCurrenciesTimestamp) {
        this.autoUpdateCurrenciesTimestamp = autoUpdateCurrenciesTimestamp;
        setLong("autoUpdateCurrenciesTimestamp", autoUpdateCurrenciesTimestamp);
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public static class GeneralPrefsChanged {
    }
}
