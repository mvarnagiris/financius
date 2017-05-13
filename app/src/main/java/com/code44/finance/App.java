package com.code44.finance;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {
    private ObjectGraph objectGraph;

    public static App with(Context context) {
        return (App) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setFontAttrId(R.attr.fontPath).build());
        JodaTimeAndroid.init(this);
        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
