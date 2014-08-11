package com.code44.finance;

import android.app.Application;

import com.code44.finance.modules.AppModule;

import net.danlew.android.joda.JodaTimeAndroid;

import dagger.ObjectGraph;

public class App extends Application {
    private static App app;

    private ObjectGraph objectGraph;

    public static App get() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        objectGraph = ObjectGraph.create(new AppModule(this));
        JodaTimeAndroid.init(this);
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
