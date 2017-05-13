package com.code44.finance.api.endpoints;

import android.content.Context;
import android.support.annotation.NonNull;

import com.code44.finance.BuildConfig;
import com.code44.finance.utils.preferences.PrefsObject;

import static com.google.common.base.Preconditions.checkNotNull;

public class Device extends PrefsObject {
    private static final String PREFERENCES_KEY = "device";

    private String registrationId;
    private String deviceName;
    private int registeredVersion = Integer.MIN_VALUE;
    private boolean isRegisteredWithServer;

    private Device() {
    }

    static Device getInstance(@NonNull Context context) {
        checkNotNull(context, "Context cannot be null.");
        Device device = get(context, PREFERENCES_KEY, Device.class);
        if (device == null) {
            device = new Device();
        }

        device.setContext(context);

        int currentVersion = BuildConfig.VERSION_CODE;
        if (currentVersion > device.registeredVersion) {
            device.registrationId = null;
            device.deviceName = null;
            device.setRegisteredWithServer(false);
        }

        return device;
    }

    @Override public void clear() {
        super.clear();
        registrationId = null;
        deviceName = null;
        registeredVersion = Integer.MIN_VALUE;
        isRegisteredWithServer = false;
    }

    @Override protected String getPreferencesKey() {
        return PREFERENCES_KEY;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
        this.registeredVersion = BuildConfig.VERSION_CODE;
        setRegisteredWithServer(false);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        save();
    }

    public boolean isRegisteredWithServer() {
        return isRegisteredWithServer;
    }

    public void setRegisteredWithServer(boolean isRegisteredWithServer) {
        this.isRegisteredWithServer = isRegisteredWithServer;
        save();
    }
}
