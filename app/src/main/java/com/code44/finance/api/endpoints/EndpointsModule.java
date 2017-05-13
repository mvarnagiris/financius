package com.code44.finance.api.endpoints;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.utils.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false)
public class EndpointsModule {
    @Provides @Singleton public User provideUser(@ApplicationContext Context context, EventBus eventBus) {
        return User.getInstance(context, eventBus);
    }

    @Provides @Singleton public Device provideDevice(@ApplicationContext Context context) {
        return Device.getInstance(context);
    }
}
