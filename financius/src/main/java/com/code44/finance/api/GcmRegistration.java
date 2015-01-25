package com.code44.finance.api;

import android.content.Context;

import com.code44.finance.BuildConfig;
import com.code44.finance.utils.preferences.Prefs;

public class GcmRegistration extends Prefs {
    private static final String PREFIX = "gcm_";

    private String registrationId;
    private int registeredVersion;
    private boolean isRegisteredWithServer;

    public GcmRegistration(Context context) {
        super(context);
        refresh();
    }

    @Override protected String getPrefix() {
        return PREFIX;
    }

    public void refresh() {
        int currentVersion = BuildConfig.VERSION_CODE;
        registeredVersion = getInteger("registeredVersion", Integer.MIN_VALUE);

        if (currentVersion > registeredVersion) {
            registrationId = null;
            setRegisteredWithServer(false);
        } else {
            registrationId = getString("registrationId", null);
            isRegisteredWithServer = getBoolean("isRegisteredWithServer", false);
        }
    }

    public void clear() {
        clear("registrationId", "registeredVersion", "isRegisteredWithServer");
        refresh();
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
        this.registeredVersion = BuildConfig.VERSION_CODE;
        setString("registrationId", registrationId);
        setInteger("registeredVersion", registeredVersion);
        setRegisteredWithServer(false);
    }

    public boolean isRegisteredWithServer() {
        return isRegisteredWithServer;
    }

    public void setRegisteredWithServer(boolean isRegisteredWithServer) {
        this.isRegisteredWithServer = isRegisteredWithServer;
        setBoolean("isRegisteredWithServer", isRegisteredWithServer);
    }
}
