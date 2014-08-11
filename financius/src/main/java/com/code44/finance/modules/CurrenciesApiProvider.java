package com.code44.finance.modules;

import com.code44.finance.BuildConfig;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.api.currencies.CurrenciesRequestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(
        injects = {
                CurrenciesApi.class,
                CurrenciesRequestService.class
        },
        library = true,
        complete = false
)
public class CurrenciesApiProvider {
    @Provides @Singleton public CurrenciesApi provideCurrenciesApi() {
        return new CurrenciesApi();
    }

    @Provides public CurrenciesRequestService provideCurrenciesRequestService() {
        final String endpoint = "http://rate-exchange.appspot.com";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .build();
        restAdapter.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
        return restAdapter.create(CurrenciesRequestService.class);
    }
}
