package com.code44.finance;

import android.app.Application;
import com.code44.finance.utils.PrefsHelper;

public class FinanciusApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        PrefsHelper.getDefault(this).onAppStart();
    }
}
