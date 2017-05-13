package com.code44.finance.ui.settings.security;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.utils.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false)
public class SecurityModule {
    @Provides @Singleton public Security provideUser(@ApplicationContext Context context, EventBus eventBus) {
        return Security.getInstance(context, eventBus);
    }
}
