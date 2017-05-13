package com.code44.finance.ui.settings.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.code44.finance.R;
import com.code44.finance.common.security.SecurityType;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.preferences.PrefsObject;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.squareup.otto.Produce;

import static com.google.common.base.Preconditions.checkNotNull;

public class Security extends PrefsObject {
    public static final String PREFERENCES_KEY = "security";

    private static final long DEFAULT_UNLOCK_DURATION = 5 * 1000;

    private transient EventBus eventBus;

    private SecurityType securityType = SecurityType.None;
    private String password;
    private long lastUnlockTimestamp;
    private long unlockDuration = DEFAULT_UNLOCK_DURATION;

    private Security() {
    }

    static Security getInstance(@NonNull Context context, @NonNull EventBus eventBus) {
        checkNotNull(context, "Context cannot be null.");
        Security security = get(context, PREFERENCES_KEY, Security.class);
        if (security == null) {
            security = new Security();
        }

        security.setContext(context);
        security.eventBus = checkNotNull(eventBus, "EventBus cannot be null.");
        eventBus.register(security);

        return security;
    }

    public static String hashPassword(@Nullable String password) {
        if (Strings.isNullOrEmpty(password)) {
            return null;
        }
        return Hashing.md5().newHasher().putString(password, Charsets.UTF_8).hash().toString();
    }

    @Override public void clear() {
        super.clear();
        securityType = SecurityType.None;
        password = null;
        lastUnlockTimestamp = 0;
        unlockDuration = DEFAULT_UNLOCK_DURATION;
    }

    @Override protected String getPreferencesKey() {
        return PREFERENCES_KEY;
    }

    @Produce public Security produceSecurity() {
        return this;
    }

    public String getLockTitle() {
        switch (securityType) {
            case None:
                return getContext().getString(R.string.none);
            case Pin:
                return getContext().getString(R.string.pin);
            default:
                throw new IllegalStateException("Lock type " + securityType + " is not supported.");
        }
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setType(SecurityType securityType, String password) {
        setTypeWithoutHashing(securityType, hashPassword(password));
    }

    public void setTypeWithoutHashing(SecurityType securityType, String password) {
        this.securityType = securityType;
        this.password = password;
        save();
    }

    public void setLastUnlockTimestamp(long lastUnlockTimestamp) {
        this.lastUnlockTimestamp = lastUnlockTimestamp;
        save();
    }

    public String getPassword() {
        return password;
    }

    public boolean validate(String password) {
        switch (securityType) {
            case None:
                return true;
            default:
                return this.password.equals(hashPassword(password));
        }
    }

    public boolean isUnlockRequired() {
        return securityType != SecurityType.None && Math.abs(lastUnlockTimestamp - System.currentTimeMillis()) > unlockDuration;
    }

    public void notifyChanged() {
        eventBus.post(this);
    }
}
