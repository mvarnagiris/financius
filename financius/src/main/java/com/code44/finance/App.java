package com.code44.finance;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

public class App extends Application {
    private static App app;

    public static Context getContext() {
        return app;
    }

    @Override public void onCreate() {
        super.onCreate();
        app = this;

//        if (BuildConfig.DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .penaltyDialog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyDeath()
//                    .penaltyLog()
//                    .build());
//        }
        JodaTimeAndroid.init(this);
    }
}
