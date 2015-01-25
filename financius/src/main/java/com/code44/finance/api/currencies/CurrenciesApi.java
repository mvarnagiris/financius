package com.code44.finance.api.currencies;

import android.content.Context;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.utils.EventBus;

import java.util.concurrent.ExecutorService;

public class CurrenciesApi {
    private final ExecutorService executor;
    private final Context context;
    private final EventBus eventBus;
    private final CurrenciesRequestService requestService;
    private final CurrenciesManager currenciesManager;

    public CurrenciesApi(ExecutorService executor, Context context, EventBus eventBus, CurrenciesRequestService requestService, CurrenciesManager currenciesManager) {
        this.executor = Preconditions.notNull(executor, "Executor cannot be null.");
        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.eventBus = Preconditions.notNull(eventBus, "EventBus cannot be null.");
        this.requestService = Preconditions.notNull(requestService, "CurrenciesRequestService cannot be null.");
        this.currenciesManager = Preconditions.notNull(currenciesManager, "CurrenciesManager cannot be null");
    }

    public void updateExchangeRates() {
        updateExchangeRates(null);
    }

    public void updateExchangeRates(String fromCode) {
        final UpdateExchangeRatesRequest request = new UpdateExchangeRatesRequest(eventBus, requestService, context, currenciesManager, fromCode);
        executeRequest(request);
    }

    private void executeRequest(Request request) {
        executor.submit(request);
    }
}
