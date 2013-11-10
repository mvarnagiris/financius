package com.code44.finance.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.code44.finance.utils.PrefsHelper;

public class GoogleUser
{
    private static GoogleUser instance = null;
    private Context context;
    // -----------------------------------------------------------------------------------------------------------------
    private String accountName;

    private GoogleUser(Context context)
    {
        this.context = context;

        // Read google user preferences
        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        accountName = prefs.getString(PrefsHelper.PREF_GOOGLE_USER_ACCOUNT_NAME, null);
    }

    public static GoogleUser getDefault(Context context)
    {
        if (instance == null)
            instance = new GoogleUser(context);
        return instance;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
        PrefsHelper.storeString(context, PrefsHelper.PREF_GOOGLE_USER_ACCOUNT_NAME, accountName);
    }

    public boolean hasAccount()
    {
        return !TextUtils.isEmpty(accountName);
    }
}