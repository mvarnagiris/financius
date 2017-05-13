package com.code44.finance.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.gson.Gson;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class PrefsObject {
    private transient Context context;

    /**
     * Add or remove object from {@link SharedPreferences}.
     *
     * @param context Any context.
     * @param key     Key of the object.
     * @param value   Object that needs to be stored, or if null, cleared from {@link SharedPreferences}.
     */
    protected static void put(@NonNull Context context, @NonNull String key, @Nullable Object value) {
        checkNotNull(context, "Context cannot be null.");
        checkNotNull(key, "Key cannot be null.");

        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (value != null) {
            editor.putString(key, new Gson().toJson(value)).apply();
        } else {
            editor.remove(key).apply();
        }
    }

    /**
     * Get object from {@link SharedPreferences}.
     *
     * @param context Any context.
     * @param key     Key of the object.
     * @param cls     Class of expected object.
     * @param <T>     Object type.
     * @return Object from {@link SharedPreferences} or {@code null} if object was now found.
     */
    protected static <T> T get(@NonNull Context context, @NonNull String key, @NonNull Class<T> cls) {
        checkNotNull(context, "Context cannot be null.");
        checkNotNull(key, "Key cannot be null.");
        checkNotNull(cls, "Class cannot be null.");

        final SharedPreferences preferences = getSharedPreferences(context);
        if (!preferences.contains(key)) {
            return null;
        }

        final String serialized = preferences.getString(key, null);
        if (serialized == null) {
            return null;
        }

        return new Gson().fromJson(serialized, cls);
    }

    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public Context getContext() {
        checkState(context != null, "Context cannot be null. You must call setContext(Context) before calling this method.");
        return context;
    }

    public void setContext(Context context) {
        checkState(this.context == null, "Context is already set.");
        this.context = context.getApplicationContext();
    }

    public void save() {
        put(getContext(), getPreferencesKeyAndCheck(), this);
    }

    public void clear() {
        put(getContext(), getPreferencesKeyAndCheck(), null);
    }

    protected abstract String getPreferencesKey();

    private String getPreferencesKeyAndCheck() {
        final String preferencesKey = getPreferencesKey();
        checkState(!Strings.isNullOrEmpty(preferencesKey), "Preferences key cannot be empty. getPreferencesKey() should return a non-empty string.");
        return preferencesKey;
    }
}
