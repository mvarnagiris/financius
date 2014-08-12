package com.code44.finance;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.code44.finance.data.db.DBHelper;
import com.code44.finance.modules.AppModule;
import com.code44.finance.modules.library.ContextProvider;
import com.code44.finance.modules.library.InjectorProvider;
import com.code44.finance.utils.Injector;
import com.code44.finance.utils.PeriodHelper;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class App extends Application implements Injector {
    private static App app;

    @Inject PeriodHelper periodHelper;
    @Inject DBHelper dbHelper;

    private ObjectGraph objectGraph;

    public static Context getContext() {
        return app;
    }

    public static Injector getInjector() {
        return app;
    }

    public static PeriodHelper getPeriodHelper() {
        return app.periodHelper;
    }

    public static DBHelper getDBHelper() {
        return app.dbHelper;
    }

    @Override public void onCreate() {
        super.onCreate();
        app = this;

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyDeath()
                    .penaltyLog()
                    .build());
        }

        objectGraph = ObjectGraph.create(
                new ContextProvider(this),
                new InjectorProvider(this),
                new AppModule()
        );
        inject(this);
        JodaTimeAndroid.init(this);
    }

    @Override public void inject(Object object) {
        objectGraph.inject(object);
    }
}
