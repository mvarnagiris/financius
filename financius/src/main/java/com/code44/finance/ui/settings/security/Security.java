package com.code44.finance.ui.settings.security;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.Prefs;
import com.squareup.otto.Produce;

public class Security extends Prefs {
    public static final String PREFIX = "security_";

    private static final long DEFAULT_UNLOCK_DURATION = 5 * 1000;

    private final EventBus eventBus;

    private Type type;
    private String password;
    private long lastUnlockTimestamp;
    private long unlockDuration;

    public Security(Context context, EventBus eventBus) {
        super(context);
        this.eventBus = eventBus;
        refresh();
        eventBus.register(this);
    }

    @Override protected String getPrefix() {
        return PREFIX;
    }

    @Produce public Security produceSecurity() {
        return this;
    }

    public String getLockTitle() {
        switch (type) {
            case None:
                return getContext().getString(R.string.none);
            case Pin:
                return getContext().getString(R.string.pin);
            default:
                throw new IllegalStateException("Lock type " + type + " is not supported.");
        }
    }

    public void clear() {
        clear("type", "password", "lastUnlockTimestamp", "unlockDuration");
        refresh();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type, String password) {
        this.type = type;
        this.password = password;
        setInteger("type", type.asInt());
        setString("password", password);
        notifyChanged();
    }

    public void setLastUnlockTimestamp(long lastUnlockTimestamp) {
        this.lastUnlockTimestamp = lastUnlockTimestamp;
        setLong("lastUnlockTimestamp", lastUnlockTimestamp);
    }

    private void refresh() {
        type = Type.fromInt(getInteger("type", 0));
        password = getString("password", "");
        lastUnlockTimestamp = getLong("lastUnlockTimestamp", 0);
        unlockDuration = getLong("unlockDuration", DEFAULT_UNLOCK_DURATION);
    }

    private void notifyChanged() {
        eventBus.post(this);
    }

    public static enum Type {
        None(0), Pin(1);

        private final int value;

        private Type(int value) {
            this.value = value;
        }

        public static Type fromInt(int value) {
            switch (value) {
                case 1:
                    return Pin;
                default:
                    return None;
            }
        }

        public int asInt() {
            return value;
        }
    }
}
