package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.App;

public class GeneralPrefs extends Prefs {
    private static final String PREFIX = "general_";

    private static GeneralPrefs singleton;

    private final EventBus eventBus;

    private boolean isAutoUpdateCurrencies;
    private long autoUpdateCurrenciesTimestamp;
    private boolean googleDriveBackupEnabled;

    public GeneralPrefs(Context context, EventBus eventBus) {
        super(context);
        this.eventBus = eventBus;
        refresh();
    }

    public static synchronized GeneralPrefs get() {
        if (singleton == null) {
            singleton = new GeneralPrefs(App.getContext(), EventBus.get());
        }
        return singleton;
    }

    public void notifyChanged() {
        eventBus.post(this);
    }

    public void refresh() {
        isAutoUpdateCurrencies = getBoolean("isAutoUpdateCurrencies", true);
        autoUpdateCurrenciesTimestamp = getLong("autoUpdateCurrenciesTimestamp", 0);
        googleDriveBackupEnabled = getBoolean("googleDriveBackupEnabled", false);
    }

    public void clear() {
        clear("isAutoUpdateCurrencies", "autoUpdateCurrenciesTimestamp", "googleDriveBackupEnabled");
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

    public boolean isGoogleDriveBackupEnabled() {
        return googleDriveBackupEnabled;
    }

    public void setGoogleDriveBackupEnabled(boolean googleDriveBackupEnabled) {
        this.googleDriveBackupEnabled = googleDriveBackupEnabled;
        setBoolean("googleDriveBackupEnabled", googleDriveBackupEnabled);
    }

    @Override protected String getPrefix() {
        return PREFIX;
    }
}
