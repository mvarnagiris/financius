package com.code44.finance.api.currencies;

import com.code44.finance.App;
import com.code44.finance.BuildConfig;
import com.code44.finance.db.model.Currency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.RestAdapter;

public class CurrenciesAsyncApi {
    private static CurrenciesAsyncApi singleton;

    private final ExecutorService executorService;
    private final CurrenciesRequestService currenciesRequestService;

    CurrenciesAsyncApi(ExecutorService executorService, CurrenciesRequestService currenciesRequestService) {
        this.executorService = executorService;
        this.currenciesRequestService = currenciesRequestService;
    }

    public static synchronized CurrenciesAsyncApi get() {
        if (singleton == null) {
            final String endpoint = "http://rate-exchange.appspot.com";
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(endpoint)
                    .build();
            restAdapter.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);
            CurrenciesRequestService requestService = restAdapter.create(CurrenciesRequestService.class);
            singleton = new CurrenciesAsyncApi(Executors.newCachedThreadPool(), requestService);
        }
        return singleton;
    }

    public void updateExchangeRate(String fromCode) {
        final CurrenciesRequest request = new CurrenciesRequest(currenciesRequestService, App.getAppContext(), fromCode, Currency.getDefault().getCode());
        executeRequest(request);
    }

    private void executeRequest(CurrenciesRequest request) {
        executorService.submit(request);
    }
}
