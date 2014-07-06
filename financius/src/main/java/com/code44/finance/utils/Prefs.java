package com.code44.finance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public abstract class Prefs {
    private final Context mContext;

    protected Prefs(Context context) {
        this.mContext = context.getApplicationContext();
    }

    protected static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected abstract String getPrefix();

    public String getKey(String keySuffix) {
        return getPrefix() + keySuffix;
    }

    protected Context getContext() {
        return mContext;
    }

    protected void setString(String keySuffix, String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(mContext).edit();
        if (TextUtils.isEmpty(value)) {
            editor.remove(getKey(keySuffix));
        } else {
            editor.putString(getKey(keySuffix), value);
        }
        editor.apply();
    }

    protected String getString(String keySuffix, String defaultValue) {
        final SharedPreferences sharedPreferences = getSharedPreferences(mContext);
        return sharedPreferences.getString(getKey(keySuffix), defaultValue);
    }

    protected void setInteger(String keySuffix, Integer value) {
        final SharedPreferences.Editor editor = getSharedPreferences(mContext).edit();
        if (value == null) {
            editor.remove(getKey(keySuffix));
        } else {
            editor.putInt(getKey(keySuffix), value);
        }
        editor.apply();
    }

    protected int getInteger(String keySuffix, int defaultValue) {
        final SharedPreferences sharedPreferences = getSharedPreferences(mContext);
        return sharedPreferences.getInt(getKey(keySuffix), defaultValue);
    }

    protected void setBoolean(String keySuffix, Boolean value) {
        final SharedPreferences.Editor editor = getSharedPreferences(mContext).edit();
        if (value == null) {
            editor.remove(getKey(keySuffix));
        } else {
            editor.putBoolean(getKey(keySuffix), value);
        }
        editor.apply();
    }

    protected boolean getBoolean(String keySuffix, boolean defaultValue) {
        final SharedPreferences sharedPreferences = getSharedPreferences(mContext);
        return sharedPreferences.getBoolean(getKey(keySuffix), defaultValue);
    }

    protected void setLong(String keySuffix, Long value) {
        final SharedPreferences.Editor editor = getSharedPreferences(mContext).edit();
        if (value == null) {
            editor.remove(getKey(keySuffix));
        } else {
            editor.putLong(getKey(keySuffix), value);
        }
        editor.apply();
    }

    protected long getLong(String keySuffix, long defaultValue) {
        final SharedPreferences sharedPreferences = getSharedPreferences(mContext);
        return sharedPreferences.getLong(getKey(keySuffix), defaultValue);
    }

    protected void clear(String... suffixes) {
        if (suffixes == null || suffixes.length == 0) {
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences(mContext).edit();
        String prefix = getPrefix();

        for (String suffix : suffixes) {
            editor.remove(prefix + suffix);
        }

        editor.apply();
    }
}
