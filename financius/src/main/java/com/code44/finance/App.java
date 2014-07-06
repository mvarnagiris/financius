package com.code44.finance;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.code44.finance.services.StartupService;

public class App extends Application {
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        startService(new Intent(context, StartupService.class));
    }
}
