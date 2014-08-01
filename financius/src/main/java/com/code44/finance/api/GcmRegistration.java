package com.code44.finance.api;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.code44.finance.App;
import com.code44.finance.utils.Prefs;

public class GcmRegistration extends Prefs {
    private static final String PREFIX = "gcm_";

    private static GcmRegistration singleton;

    private final AppVersionProvider appVersionProvider;

    private String registrationId;
    private int registeredVersion;
    private boolean isRegisteredWithServer;

    private GcmRegistration(Context context, AppVersionProvider appVersionProvider) {
        super(context);
        this.appVersionProvider = appVersionProvider;
        refresh();
    }

    public synchronized static GcmRegistration get() {
        if (singleton == null) {
            singleton = new GcmRegistration(App.getAppContext(), new DefaultAppVersionProvider());
        }
        return singleton;
    }

    public void refresh() {
        int currentVersion = appVersionProvider.getAppVersion(getContext());
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
        this.registeredVersion = appVersionProvider.getAppVersion(getContext());
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

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public static interface AppVersionProvider {
        public int getAppVersion(Context context);
    }

    static class DefaultAppVersionProvider implements AppVersionProvider {
        @Override
        public int getAppVersion(Context context) {
            try {
                //noinspection ConstantConditions
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                // should never happen
                throw new RuntimeException("Could not get package name: " + e);
            }
        }
    }
}
