package com.code44.finance.utils;

import android.content.Context;

public class GeneralPrefs extends Prefs {
    private static final String PREFIX = "general_";

    private boolean isAutoUpdateCurrencies;
    private long autoUpdateCurrenciesTimestamp;

    public GeneralPrefs(Context context) {
        super(context);
        refresh();
    }

    public static void notifyGeneralPrefsChanged() {
        // TODO EventBus.getDefault().post(new GeneralPrefsChanged());
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
}
