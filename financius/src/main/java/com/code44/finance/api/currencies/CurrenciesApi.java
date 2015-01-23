package com.code44.finance.api.currencies;

import android.content.Context;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.utils.EventBus;

import java.util.concurrent.ExecutorService;

public class CurrenciesApi {
    private final ExecutorService executor;
    private final Context context;
    private final EventBus eventBus;
    private final CurrenciesRequestService requestService;

    public CurrenciesApi(ExecutorService executor, Context context, EventBus eventBus, CurrenciesRequestService requestService) {
        this.executor = Preconditions.notNull(executor, "Executor cannot be null.");
        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.eventBus = Preconditions.notNull(eventBus, "EventBus cannot be null.");
        this.requestService = Preconditions.notNull(requestService, "CurrenciesRequestService cannot be null.");
    }

    public void getExchangeRates(String... codes) {
        final GetExchangeRatesRequest request = new GetExchangeRatesRequest(eventBus, requestService, codes);
        executeRequest(request);
    }

    public void updateExchangeRates() {
        final UpdateExchangeRatesRequest request = new UpdateExchangeRatesRequest(eventBus, requestService, context);
        executeRequest(request);
    }

    private void executeRequest(Request request) {
        executor.submit(request);
    }
}
