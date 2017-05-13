package com.code44.finance.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.code44.finance.api.endpoints.User;
import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.common.security.SecurityType;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.utils.preferences.GeneralPrefs;

public final class Migration {
    private Migration() {
    }

    public static void upgradeTo96(Context context, User user, Security security, GeneralPrefs generalPrefs) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        // User
        final String userPrefix = "user_";
        user.setId(sharedPreferences.getString(userPrefix + "id", null));
        user.setGoogleId(sharedPreferences.getString(userPrefix + "googleId", null));
        user.setEmail(sharedPreferences.getString(userPrefix + "email", null));
        user.setFirstName(sharedPreferences.getString(userPrefix + "firstName", null));
        user.setLastName(sharedPreferences.getString(userPrefix + "lastName", null));
        user.setPhotoUrl(sharedPreferences.getString(userPrefix + "photoUrl", null));
        user.setCoverUrl(sharedPreferences.getString(userPrefix + "coverUrl", null));
        user.notifyChanged();

        // Security
        final String securityPrefix = "security_";
        security.setType(SecurityType.fromInt(sharedPreferences.getInt(securityPrefix + "securityType", SecurityType.None.asInt())), sharedPreferences
                .getString(securityPrefix + "password", null));
        security.setLastUnlockTimestamp(sharedPreferences.getLong(securityPrefix + "lastUnlockTimestamp", 0));
        if (security.getSecurityType() != SecurityType.None) {
            security.setType(security.getSecurityType(), security.getPassword());
        }
        security.notifyChanged();

        // GeneralPrefs
        final String generalPrefix = "general_";
        generalPrefs.setLastVersionCode(sharedPreferences.getInt(generalPrefix + "lastVersionCode", 0));
        generalPrefs.setAutoUpdateCurrencies(sharedPreferences.getBoolean(generalPrefix + "isAutoUpdateCurrencies", true));
        generalPrefs.setAutoUpdateCurrenciesTimestamp(sharedPreferences.getLong(generalPrefix + "autoUpdateCurrenciesTimestamp", 0));
        generalPrefs.setLastFileExportPath(sharedPreferences.getString(generalPrefix + "lastFileExportPath", null));
        try {
            generalPrefs.setIntervalTypeAndLength(IntervalType.valueOf(sharedPreferences.getString(generalPrefix + "intervalType", IntervalType.Month
                    .toString())), sharedPreferences.getInt(generalPrefix + "intervalLength", 1));
        } catch (Exception ignore) {
            generalPrefs.setIntervalTypeAndLength(IntervalType.Month, 1);
        }
        generalPrefs.setAnalyticsOptOut(sharedPreferences.getBoolean(generalPrefix + "analyticsOptOut", false));
        generalPrefs.setMainCurrencyCode(sharedPreferences.getString(generalPrefix + "mainCurrencyCode", null));
    }
}
