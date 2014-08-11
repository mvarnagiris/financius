package com.code44.finance;

import android.app.Application;
import android.os.StrictMode;

import com.code44.finance.modules.AppModule;
import com.code44.finance.modules.RequestModule;
import com.code44.finance.modules.library.ContextProvider;
import com.code44.finance.modules.library.InjectorProvider;
import com.code44.finance.utils.Injector;

import net.danlew.android.joda.JodaTimeAndroid;

import dagger.ObjectGraph;

public class App extends Application implements Injector {
    private static App app;

    private ObjectGraph objectGraph;

    public static App get() {
        return app;
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
                new RequestModule(),
                new AppModule()
        );
        JodaTimeAndroid.init(this);
    }

    @Override public void inject(Object object) {
        objectGraph.inject(object);
    }
}
