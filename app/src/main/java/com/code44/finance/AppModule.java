package com.code44.finance;

import android.content.Context;

import com.code44.finance.api.currencies.CurrenciesApiModule;
import com.code44.finance.api.endpoints.EndpointsModule;
import com.code44.finance.data.providers.ProviderModule;
import com.code44.finance.receivers.ReceiversModule;
import com.code44.finance.services.ServicesModule;
import com.code44.finance.ui.common.activities.ActivitiesModule;
import com.code44.finance.ui.common.fragments.FragmentsModule;
import com.code44.finance.ui.common.views.ViewModule;
import com.code44.finance.ui.settings.security.SecurityModule;
import com.code44.finance.utils.analytics.AnalyticsModule;
import com.code44.finance.utils.executors.ExecutorsModule;
import com.code44.finance.utils.preferences.PreferencesModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {App.class},
        includes = {AnalyticsModule.class, ProviderModule.class, ExecutorsModule.class, ActivitiesModule.class,
                    FragmentsModule.class, CurrenciesApiModule.class, ViewModule.class, ReceiversModule.class, EndpointsModule.class,
                    ServicesModule.class, PreferencesModule.class, SecurityModule.class})
public final class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides @Singleton @ApplicationContext Context provideApplication() {
        return app;
    }
}
