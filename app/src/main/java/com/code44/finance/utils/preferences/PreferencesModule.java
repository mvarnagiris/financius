package com.code44.finance.utils.preferences;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.utils.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false)
public class PreferencesModule {
    @Provides @Singleton public GeneralPrefs provideUser(@ApplicationContext Context context, EventBus eventBus) {
        return GeneralPrefs.getInstance(context, eventBus);
    }
}
