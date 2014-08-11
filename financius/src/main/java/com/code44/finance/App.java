package com.code44.finance;

import android.app.Application;

import com.code44.finance.modules.AppModule;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class App extends Application {
    @SuppressWarnings("FieldCanBeLocal")
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
        JodaTimeAndroid.init(this);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }
}
