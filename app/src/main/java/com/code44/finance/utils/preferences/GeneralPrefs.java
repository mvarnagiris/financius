package com.code44.finance.utils.preferences;

import android.content.Context;
import android.support.annotation.NonNull;

import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.utils.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;

public class GeneralPrefs extends PrefsObject {
    private static final String PREFERENCES_KEY = "general";

    private transient EventBus eventBus;

    private int lastVersionCode = 0;
    private boolean isAutoUpdateCurrencies = true;
    private long autoUpdateCurrenciesTimestamp = 0;
    private String lastFileExportPath = null;
    private IntervalType intervalIntervalType = IntervalType.Month;
    private int intervalLength = 1;
    private boolean analyticsOptOut = false;
    private String mainCurrencyCode = "USD";

    private GeneralPrefs() {
    }

    static GeneralPrefs getInstance(@NonNull Context context, @NonNull EventBus eventBus) {
        checkNotNull(context, "Context cannot be null.");
        GeneralPrefs generalPrefs = get(context, PREFERENCES_KEY, GeneralPrefs.class);
        if (generalPrefs == null) {
            generalPrefs = new GeneralPrefs();
        }

        generalPrefs.setContext(context);
        generalPrefs.eventBus = checkNotNull(eventBus, "EventBus cannot be null.");
        eventBus.register(generalPrefs);

        return generalPrefs;
    }

    @Override public void clear() {
        super.clear();
        lastVersionCode = 0;
        isAutoUpdateCurrencies = true;
        autoUpdateCurrenciesTimestamp = 0;
        lastFileExportPath = null;
        intervalIntervalType = IntervalType.Month;
        intervalLength = 1;
        analyticsOptOut = false;
        mainCurrencyCode = "USD";
    }

    @Override protected String getPreferencesKey() {
        return PREFERENCES_KEY;
    }

    public void notifyChanged() {
        eventBus.post(this);
    }

    public int getLastVersionCode() {
        return lastVersionCode;
    }

    public void setLastVersionCode(int lastVersionCode) {
        this.lastVersionCode = lastVersionCode;
        save();
    }

    public boolean isAutoUpdateCurrencies() {
        return isAutoUpdateCurrencies;
    }

    public void setAutoUpdateCurrencies(boolean isAutoUpdateCurrencies) {
        this.isAutoUpdateCurrencies = isAutoUpdateCurrencies;
        save();
    }

    public long getAutoUpdateCurrenciesTimestamp() {
        return autoUpdateCurrenciesTimestamp;
    }

    public void setAutoUpdateCurrenciesTimestamp(long autoUpdateCurrenciesTimestamp) {
        this.autoUpdateCurrenciesTimestamp = autoUpdateCurrenciesTimestamp;
        save();
    }

    public String getLastFileExportPath() {
        return lastFileExportPath;
    }

    public void setLastFileExportPath(String lastFileExportPath) {
        this.lastFileExportPath = lastFileExportPath;
        save();
    }

    public IntervalType getIntervalIntervalType() {
        return intervalIntervalType;
    }

    public int getIntervalLength() {
        return intervalLength;
    }

    public void setIntervalTypeAndLength(IntervalType intervalIntervalType, int intervalLength) {
        this.intervalIntervalType = intervalIntervalType;
        this.intervalLength = intervalLength;
        save();
    }

    public boolean isAnalyticsOptOut() {
        return analyticsOptOut;
    }

    public void setAnalyticsOptOut(boolean analyticsOptOut) {
        this.analyticsOptOut = analyticsOptOut;
        save();
    }

    public String getMainCurrencyCode() {
        return mainCurrencyCode;
    }

    public void setMainCurrencyCode(String mainCurrencyCode) {
        this.mainCurrencyCode = mainCurrencyCode;
        save();
    }
}
