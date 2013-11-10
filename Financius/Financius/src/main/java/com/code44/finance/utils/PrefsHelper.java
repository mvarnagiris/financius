package com.code44.finance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import com.code44.finance.ui.settings.SettingsActivity;
import de.greenrobot.event.EventBus;

public class PrefsHelper
{
    // Security
    // --------------------------------------------------------------------------------------------------------------------------------
    public static final String PREFIX_SECURITY = "security_settings_";
    public static final String PREF_SECURITY_APP_LOCK = PREFIX_SECURITY + "app_lock";
    public static final String PREF_SECURITY_APP_LOCK_CODE = PREFIX_SECURITY + "app_lock_code";
    public static final String PREF_SECURITY_LAST_UNLOCK_TIMESTAMP = PREFIX_SECURITY + "last_unlock_timestamp";
    public static final String PREF_SECURITY_UNLOCK_DURATION = PREFIX_SECURITY + "unlock_duration";
    // Period
    // --------------------------------------------------------------------------------------------------------------------------------
    public static final String PREFIX_PERIOD = "period_";
    public static final String PREF_PERIOD_TYPE = PREFIX_PERIOD + "period_type";
    public static final String PREF_PERIOD_ACTIVE_START = PREFIX_PERIOD + "active_start";
    public static final String PREF_PERIOD_ACTIVE_END = PREFIX_PERIOD + "active_end";
    // Exchange rates
    // -----------------------------------------------------------------------------------------------------------------
    public static final String PREFIX_EXCHANGE_RATES = "exchange_rates_";
    public static final String PREF_EXCHANGE_RATES_UPDATE_EXCHANGE_RATES = SettingsActivity.PREF_UPDATE_EXCHANGE_RATES;
    public static final String PREF_EXCHANGE_RATES_TIMESTAMP = PREFIX_EXCHANGE_RATES + "exchange_rates_timestamp";
    // Google user
    // -----------------------------------------------------------------------------------------------------------------
    public static final String PREFIX_GOOGLE_USER = "google_user_";
    public static final String PREF_GOOGLE_USER_ACCOUNT_NAME = PREFIX_GOOGLE_USER + "account_name";
    // App user
    // -----------------------------------------------------------------------------------------------------------------
    public static final String PREFIX_APP_USER = "appuser_";
    public static final String PREF_APP_USER_DRIVE_BACKUP_ENABLED = PREFIX_APP_USER + "drive_backup_enabled";
    public static final String PREF_APP_USER_DRIVE_DEVICE_NAME = PREFIX_APP_USER + "drive_device_name";
    // General
    // -----------------------------------------------------------------------------------------------------------------
    private static final String PREFIX_GENERAL = "general_";
    private static final String PREF_FIRST_APP_START = PREFIX_GENERAL + "first_app_start";
    private static final String PREF_SHOW_DONATE_IN_NAVIGATION = PREFIX_GENERAL + "show_donate_in_navigation";
    // Settings
    // -----------------------------------------------------------------------------------------------------------------
    private static final String PREF_SETTINGS_FOCUS_CATEGORIES_SEARCH = SettingsActivity.PREF_FOCUS_CATEGORIES_SEARCH;
    // Service
    // -----------------------------------------------------------------------------------------------------------------
    private static final String PREFIX_SERVICE = "service_";
    // -----------------------------------------------------------------------------------------------------------------
    private static PrefsHelper instance = null;
    private final Context context;
    // -----------------------------------------------------------------------------------------------------------------
    private long firstAppStart;
    private boolean showDonateInNavigation;
    private boolean focusCategoriesSearch;

    private PrefsHelper(Context context)
    {
        this.context = context.getApplicationContext();

        final SharedPreferences prefs = getPrefs(context);

        // Read general preferences
        firstAppStart = prefs.getLong(PREF_FIRST_APP_START, 0);
        showDonateInNavigation = prefs.getBoolean(PREF_SHOW_DONATE_IN_NAVIGATION, true);

        // Read settings preferences
        focusCategoriesSearch = prefs.getBoolean(PREF_SETTINGS_FOCUS_CATEGORIES_SEARCH, false);
    }

    public static PrefsHelper getDefault(Context context)
    {
        if (instance == null)
            instance = new PrefsHelper(context);
        return instance;
    }

    public static SharedPreferences getPrefs(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void storeString(Context context, String key, String value)
    {
        getPrefs(context).edit().putString(key, value).apply();
    }

    public static void storeInt(Context context, String key, int value)
    {
        getPrefs(context).edit().putInt(key, value).apply();
    }

    public static void storeLong(Context context, String key, long value)
    {
        getPrefs(context).edit().putLong(key, value).apply();
    }

    public static void storeBoolean(Context context, String key, boolean value)
    {
        getPrefs(context).edit().putBoolean(key, value).apply();
    }

    public static String getLastSuccessfulServiceWorkTimePrefName(String prefix, int requestType, String suffix)
    {
        return PREFIX_SERVICE + prefix + "_" + requestType + (suffix != null ? suffix : "");
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void onAppStart()
    {
        if (firstAppStart == 0)
            firstAppStart = System.currentTimeMillis();
        storeLong(context, PREF_FIRST_APP_START, firstAppStart);
    }

    public boolean isEnoughTimeForDonateInNavigation()
    {
        return System.currentTimeMillis() - firstAppStart > DateUtils.WEEK_IN_MILLIS * 2;
    }

    public boolean showDonateInNavigation()
    {
        return showDonateInNavigation;
    }

    public void setShowDonateInNavigation(boolean showDonateInNavigation)
    {
        this.showDonateInNavigation = showDonateInNavigation;
        storeBoolean(context, PREF_SHOW_DONATE_IN_NAVIGATION, showDonateInNavigation);
        EventBus.getDefault().post(new ShowDonateInNavigationChangedEvent());
    }

    public boolean isFocusCategoriesSearch()
    {
        return focusCategoriesSearch;
    }

    public void setFocusCategoriesSearch(boolean focusCategoriesSearch)
    {
        this.focusCategoriesSearch = focusCategoriesSearch;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public static class ShowDonateInNavigationChangedEvent
    {
    }
}
