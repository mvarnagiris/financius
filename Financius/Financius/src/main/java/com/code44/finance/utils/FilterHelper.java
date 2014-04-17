package com.code44.finance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;

import de.greenrobot.event.EventBus;

public class FilterHelper
{
    private static final String PREFIX = "filter_helper_";
    // -----------------------------------------------------------------------------------------------------------------
    private static FilterHelper instance;
    // -----------------------------------------------------------------------------------------------------------------
    private Context context;
    private long periodStart;
    private long periodEnd;
    private long accountID;

    private FilterHelper(Context context)
    {
        this.context = context.getApplicationContext();

        final SharedPreferences prefs = PrefsHelper.getPrefs(context);
        periodStart = prefs.getLong(PREFIX + "periodStart", 0);
        periodEnd = prefs.getLong(PREFIX + "periodEnd", 0);
        accountID = prefs.getLong(PREFIX + "accountID", -1);
    }

    public static FilterHelper getDefault(Context context)
    {
        if (instance == null)
            instance = new FilterHelper(context);
        return instance;
    }

    public long getPeriodStart()
    {
        return periodStart;
    }

    public void setPeriodStart(long periodStart)
    {
        this.periodStart = periodStart;
        PrefsHelper.getPrefs(context).edit().putLong(PREFIX + "periodStart", periodStart).apply();
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public long getPeriodEnd()
    {
        return periodEnd;
    }

    public void setPeriodEnd(long periodEnd)
    {
        this.periodEnd = periodEnd;
        PrefsHelper.getPrefs(context).edit().putLong(PREFIX + "periodEnd", periodEnd).apply();
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public boolean isPeriodSet()
    {
        return periodStart > 0 || periodEnd > 0;
    }

    public void clearPeriod()
    {
        periodStart = 0;
        periodEnd = 0;
        accountID = -1;
        EventBus.getDefault().post(new FilterChangedEvent());
    }


    public void clearAccount()
    {
        accountID = -1;
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public boolean isAccountSet()
    {
        return accountID != -1;
    }

    public long getAccountID() {
        return accountID;
    }

    public String getAccountName() {
        Cursor c = null;

        if(accountID == -1)
            return "";

        try {
            c = context.getContentResolver().query(AccountsProvider.uriAccounts(),
                    new String[]{Tables.Accounts.TITLE},
                    Tables.Accounts.T_ID + "=?",
                    new String[]{String.valueOf(accountID)}, null);
            if(c != null && c.moveToFirst())
            {
                return c.getString(c.getColumnIndex(Tables.Accounts.TITLE));
            }
        }
        catch(Exception e)
        {
            if (c != null && !c.isClosed())
                c.close();
        }
        return "";
    }

    public void setAccountID(long id){
        accountID = id;
        PrefsHelper.getPrefs(context).edit().putLong(PREFIX + "accountID", accountID).apply();
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public void clearAll()
    {
        // clearPeriod
        periodStart = 0;
        periodEnd = 0;

        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public static class FilterChangedEvent
    {
    }
}