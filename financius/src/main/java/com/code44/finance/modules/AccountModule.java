package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.services.StartupService;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.GeneralPrefs;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                StartupService.class
        }
)
public class AccountModule {
    @Provides @Singleton public User provideUser(@ApplicationContext Context context, DBHelper dbHelper, GcmRegistration gcmRegistration, EventBus eventBus) {
        return new User(context, dbHelper, gcmRegistration, eventBus);
    }

    @Provides @Singleton public GcmRegistration provideGcmRegistration(@ApplicationContext Context context) {
        return new GcmRegistration(context);
    }

    @Provides @Singleton public GeneralPrefs provideGeneralPrefs(@ApplicationContext Context context, EventBus eventBus) {
        return new GeneralPrefs(context, eventBus);
    }
}
