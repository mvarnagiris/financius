package com.code44.finance.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import com.code44.finance.utils.PrefsHelper;

public class AppUser
{
    private static AppUser instance = null;
    private Context context;
    // -----------------------------------------------------------------------------------------------------------------
    private boolean isDriveBackupEnabled;
    private String driveDeviceName;

    private AppUser(Context context)
    {
        this.context = context.getApplicationContext();

        // Read app user preferences
        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        isDriveBackupEnabled = prefs.getBoolean(PrefsHelper.PREF_APP_USER_DRIVE_BACKUP_ENABLED, false);
        driveDeviceName = prefs.getString(PrefsHelper.PREF_APP_USER_DRIVE_DEVICE_NAME, Build.MODEL);
    }

    public static AppUser getDefault(Context context)
    {
        if (instance == null)
            instance = new AppUser(context);
        return instance;
    }

    public String getDriveDeviceName()
    {
        return driveDeviceName;
    }

    public void setDriveDeviceName(String driveDeviceName)
    {
        this.driveDeviceName = driveDeviceName;
        PrefsHelper.storeString(context, PrefsHelper.PREF_APP_USER_DRIVE_DEVICE_NAME, driveDeviceName);
    }

    public boolean isDriveBackupEnabled()
    {
        return isDriveBackupEnabled;
    }

    public void setDriveBackupEnabled(boolean driveBackupEnabled)
    {
        isDriveBackupEnabled = driveBackupEnabled;
        PrefsHelper.storeBoolean(context, PrefsHelper.PREF_APP_USER_DRIVE_BACKUP_ENABLED, isDriveBackupEnabled);
    }
}