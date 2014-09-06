package com.code44.finance.utils;

import android.content.Context;

public class GeneralPrefs extends Prefs {
    private static final String PREFIX = "general_";

    private final EventBus eventBus;

    private boolean isAutoUpdateCurrencies;
    private long autoUpdateCurrenciesTimestamp;
    private String lastFileExportPath;

    public GeneralPrefs(Context context, EventBus eventBus) {
        super(context);
        this.eventBus = eventBus;
        refresh();
    }

    public void notifyChanged() {
        eventBus.post(this);
    }

    public void refresh() {
        isAutoUpdateCurrencies = getBoolean("isAutoUpdateCurrencies", true);
        autoUpdateCurrenciesTimestamp = getLong("autoUpdateCurrenciesTimestamp", 0);
        lastFileExportPath = getString("lastFileExportPath", null);
    }

    public void clear() {
        clear("isAutoUpdateCurrencies", "autoUpdateCurrenciesTimestamp", "lastFileExportPath");
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

    public String getLastFileExportPath() {
        return lastFileExportPath;
    }

    public void setLastFileExportPath(String lastFileExportPath) {
        this.lastFileExportPath = lastFileExportPath;
        setString("lastFileExportPath", lastFileExportPath);
    }

    @Override protected String getPrefix() {
        return PREFIX;
    }
}
