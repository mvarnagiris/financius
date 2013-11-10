package com.code44.finance.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import com.code44.finance.ui.settings.lock.LockActivity;
import com.code44.finance.ui.settings.lock.LockFragment;
import com.code44.finance.ui.settings.lock.LockPatternFragment;

public class SecurityHelper
{
    public static final int APP_LOCK_NONE = 0;
    public static final int APP_LOCK_PATTERN = 1;
    // --------------------------------------------------------------------------------------------------------------------------------
    public static final int MODE_NEW = 1;
    public static final int MODE_COMPARE = 2;
    public static final int MODE_CLEAR = 3;
    // --------------------------------------------------------------------------------------------------------------------------------
    public static final long DEFAULT_APP_UNLOCK_DURATION = DateUtils.MINUTE_IN_MILLIS;
    // --------------------------------------------------------------------------------------------------------------------------------
    public static final String p3 = "3kfwiRx20UM0Igj8f6TQwHgEUsyKqYMe1Ol0oUC0LxLFeMXPYPInrxI/MHn4";
    // --------------------------------------------------------------------------------------------------------------------------------
    private static SecurityHelper instance = null;
    private Context context;
    // --------------------------------------------------------------------------------------------------------------------------------
    private int appLock;
    private String lockCode;
    private long unlockTimestamp;
    private long unlockDuration;

    private SecurityHelper(Context context)
    {
        this.context = context.getApplicationContext();

        // Read security preferences
        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        appLock = prefs.getInt(PrefsHelper.PREF_SECURITY_APP_LOCK, APP_LOCK_NONE);
        lockCode = prefs.getString(PrefsHelper.PREF_SECURITY_APP_LOCK_CODE, null);
        unlockTimestamp = prefs.getLong(PrefsHelper.PREF_SECURITY_LAST_UNLOCK_TIMESTAMP, 0);
        unlockDuration = prefs.getLong(PrefsHelper.PREF_SECURITY_UNLOCK_DURATION, DEFAULT_APP_UNLOCK_DURATION);
    }

    public static SecurityHelper getDefault(Context context)
    {
        if (instance == null)
            instance = new SecurityHelper(context);
        return instance;
    }

    public void setNewCode(int appLock, String lockCode)
    {
        this.appLock = appLock;
        this.lockCode = lockCode;
        this.unlockTimestamp = System.currentTimeMillis();
        PrefsHelper.storeInt(context, PrefsHelper.PREF_SECURITY_APP_LOCK, appLock);
        PrefsHelper.storeString(context, PrefsHelper.PREF_SECURITY_APP_LOCK_CODE, lockCode);
        PrefsHelper.storeLong(context, PrefsHelper.PREF_SECURITY_LAST_UNLOCK_TIMESTAMP, unlockTimestamp);
    }

    public void updateUnlockTimestamp()
    {
        if (appLock == APP_LOCK_NONE)
            return;
        this.unlockTimestamp = System.currentTimeMillis();
        PrefsHelper.storeLong(context, PrefsHelper.PREF_SECURITY_LAST_UNLOCK_TIMESTAMP, unlockTimestamp);
    }

    public void clearLock()
    {
        this.appLock = APP_LOCK_NONE;
        this.lockCode = null;
        this.unlockTimestamp = 0;
        PrefsHelper.storeInt(context, PrefsHelper.PREF_SECURITY_APP_LOCK, appLock);
        PrefsHelper.storeString(context, PrefsHelper.PREF_SECURITY_APP_LOCK_CODE, lockCode);
        PrefsHelper.storeLong(context, PrefsHelper.PREF_SECURITY_LAST_UNLOCK_TIMESTAMP, unlockTimestamp);
    }

    public int getAppLockCode()
    {
        return appLock;
    }

    /**
     * Checks if it's necessary to unlock the app.
     */
    public void checkSecurity(Activity activity, boolean force)
    {
        // If app lock is not set or we are still withing allowed unlock duration and we are not forcing unlock, then no need to unlock
        if (appLock == APP_LOCK_NONE || (System.currentTimeMillis() - unlockTimestamp <= unlockDuration && !force))
            return;

        // Unlock app
        LockActivity.startLockCompare(activity);
    }

    public LockFragment getLockFragmentForUnlock()
    {
        return getLockFragment(appLock, LockFragment.LT_COMPARE, lockCode);
    }

    public LockFragment getLockFragmentForNew(int newAppLock)
    {
        return getLockFragment(newAppLock, LockFragment.LT_NEW, null);
    }

    private LockFragment getLockFragment(int appLock, int lockType, String lockCode)
    {
        switch (appLock)
        {
            case APP_LOCK_PATTERN:
                return LockPatternFragment.newInstance(lockType, lockCode);
        }

        return null;
    }
}