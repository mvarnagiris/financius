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
        periodStart = 0;
        periodEnd = 0;
        accountID = -1;
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
        if(this.periodEnd > 0 && periodStart > this.periodEnd)
        {//we should swap the start & end time
            this.periodStart = this.periodEnd;
            this.periodEnd = periodStart;
        }
        else
        {
            this.periodStart = periodStart;
        }
        EventBus.getDefault().post(new FilterChangedEvent());
    }

    public long getPeriodEnd()
    {
        return periodEnd;
    }

    public void setPeriodEnd(long periodEnd)
    {
        if(this.periodStart > 0 && periodEnd < this.periodStart)
        {//we should swap the start & end time
            this.periodEnd = this.periodStart;
            this.periodStart = periodEnd;
        }
        else
        {
            this.periodEnd = periodEnd;
        }
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