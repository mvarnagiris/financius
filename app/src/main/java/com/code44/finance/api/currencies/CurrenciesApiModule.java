package com.code44.finance.api.currencies;

import com.code44.finance.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true)
public class CurrenciesApiModule {
    @Provides @Singleton public CurrenciesRequestService provideCurrenciesRequestService() {
        final String endpoint = "http://query.yahooapis.com";
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint).build();
        restAdapter.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
        return restAdapter.create(CurrenciesRequestService.class);
    }
}
