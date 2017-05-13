package com.code44.finance.api.currencies;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.api.Request;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.executors.Network;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class CurrenciesApi {
    private final ExecutorService executor;
    private final Context context;
    private final EventBus eventBus;
    private final CurrenciesRequestService requestService;

    @Inject public CurrenciesApi(@Network ExecutorService executor, @ApplicationContext Context context, CurrenciesRequestService requestService, EventBus eventBus) {
        this.executor = checkNotNull(executor, "Executor cannot be null.");
        this.context = checkNotNull(context, "Context cannot be null.");
        this.eventBus = checkNotNull(eventBus, "EventBus cannot be null.");
        this.requestService = checkNotNull(requestService, "CurrenciesRequestService cannot be null.");
    }

    public void updateExchangeRates() {
        updateExchangeRates(null);
    }

    public void updateExchangeRates(String fromCode) {
        final UpdateExchangeRatesRequest request = new UpdateExchangeRatesRequest(eventBus, requestService, context, fromCode);
        executeRequest(request);
    }

    private void executeRequest(Request request) {
        executor.submit(request);
    }
}
