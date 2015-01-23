package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.BuildConfig;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.CurrenciesRequestService;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.qualifiers.Network;
import com.code44.finance.services.StartupService;
import com.code44.finance.utils.EventBus;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true,
        injects = {
                StartupService.class
        }
)
public class CurrenciesApiModule {
    @Provides @Singleton public CurrenciesApi provideCurrenciesApi(@Network ExecutorService executor, @ApplicationContext Context context, EventBus eventBus, CurrenciesRequestService currenciesRequestService) {
        return new CurrenciesApi(executor, context, eventBus, currenciesRequestService);
    }

    @Provides @Singleton public CurrenciesRequestService provideCurrenciesRequestService() {
        final String endpoint = "http://query.yahooapis.com";
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint).build();
        restAdapter.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
        return restAdapter.create(CurrenciesRequestService.class);
    }
}
