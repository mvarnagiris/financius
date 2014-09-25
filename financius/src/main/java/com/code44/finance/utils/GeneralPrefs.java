package com.code44.finance.utils;

import android.content.Context;

public class GeneralPrefs extends Prefs {
    private static final String PREFIX = "general_";

    private final EventBus eventBus;

    private boolean isAutoUpdateCurrencies;
    private long autoUpdateCurrenciesTimestamp;
    private String lastFileExportPath;
    private BaseInterval.Type intervalType;
    private int intervalLength;

    public GeneralPrefs(Context context, EventBus eventBus) {
        super(context);
        this.eventBus = eventBus;
        refresh();
    }

    @Override protected String getPrefix() {
        return PREFIX;
    }

    public void notifyChanged() {
        eventBus.post(this);
    }

    public void refresh() {
        isAutoUpdateCurrencies = getBoolean("isAutoUpdateCurrencies", true);
        autoUpdateCurrenciesTimestamp = getLong("autoUpdateCurrenciesTimestamp", 0);
        lastFileExportPath = getString("lastFileExportPath", null);
        intervalType = BaseInterval.Type.valueOf(getString("intervalType", BaseInterval.Type.MONTH.toString()));
        intervalLength = getInteger("intervalLength", 1);
    }

    public void clear() {
        clear("isAutoUpdateCurrencies", "autoUpdateCurrenciesTimestamp", "lastFileExportPath", "intervalType", "intervalLength");
        refresh();
        notifyChanged();
    }

    public boolean isAutoUpdateCurrencies() {
        return isAutoUpdateCurrencies;
    }

    public void setAutoUpdateCurrencies(boolean isAutoUpdateCurrencies) {
        this.isAutoUpdateCurrencies = isAutoUpdateCurrencies;
        setBoolean("isAutoUpdateCurrencies", isAutoUpdateCurrencies);
        notifyChanged();
    }

    public long getAutoUpdateCurrenciesTimestamp() {
        return autoUpdateCurrenciesTimestamp;
    }

    public void setAutoUpdateCurrenciesTimestamp(long autoUpdateCurrenciesTimestamp) {
        this.autoUpdateCurrenciesTimestamp = autoUpdateCurrenciesTimestamp;
        setLong("autoUpdateCurrenciesTimestamp", autoUpdateCurrenciesTimestamp);
        notifyChanged();
    }

    public String getLastFileExportPath() {
        return lastFileExportPath;
    }

    public void setLastFileExportPath(String lastFileExportPath) {
        this.lastFileExportPath = lastFileExportPath;
        setString("lastFileExportPath", lastFileExportPath);
        notifyChanged();
    }

    public BaseInterval.Type getIntervalType() {
        return intervalType;
    }

    public int getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalTypeAndLength(BaseInterval.Type intervalType, int intervalLength) {
        this.intervalType = intervalType;
        this.intervalLength = intervalLength;
        setString("intervalType", intervalType.toString());
        setInteger("intervalLength", intervalLength);
        notifyChanged();
    }
}
