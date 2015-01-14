package com.code44.finance.api.currencies;

import android.content.Context;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.utils.EventBus;

import java.util.List;
import java.util.concurrent.Executor;

public class CurrenciesApi {
    private final Executor executor;
    private final Context context;
    private final EventBus eventBus;
    private final CurrenciesRequestService requestService;

    public CurrenciesApi(Executor executor, Context context, EventBus eventBus, CurrenciesRequestService requestService) {
        this.executor = Preconditions.notNull(executor, "Executor cannot be null.");
        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.eventBus = Preconditions.notNull(eventBus, "EventBus cannot be null.");
        this.requestService = Preconditions.notNull(requestService, "CurrenciesRequestService cannot be null.");
    }

    public void getExchangeRates(String... codes) {
        final ExchangeRateRequest request = new ExchangeRateRequest(eventBus, context, requestService, fromCode, toCode, false);
        executeRequest(request);
    }

    public void getExchangeRate(String fromCode, String toCode) {
        final ExchangeRateRequest request = new ExchangeRateRequest(eventBus, context, requestService, fromCode, toCode, false);
        executeRequest(request);
    }

    public void updateExchangeRate(String fromCode, String toCode) {
        final ExchangeRateRequest request = new ExchangeRateRequest(eventBus, context, requestService, fromCode, toCode, true);
        executeRequest(request);
    }

    public void updateExchangeRates(List<String> fromCodes, String toCode) {
        final ExchangeRatesRequestOld request = new ExchangeRatesRequestOld(eventBus, context, requestService, fromCodes, toCode);
        executeRequest(request);
    }

    private void executeRequest(Request request) {
        executor.execute(request);
    }
}
