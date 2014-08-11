package com.code44.finance.api.currencies;

import com.code44.finance.data.db.model.Currency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CurrenciesApi {
    private final ExecutorService executorService;

    public CurrenciesApi() {
        this.executorService = Executors.newCachedThreadPool();
    }

    public void updateExchangeRate(String fromCode) {
        final String toCode = Currency.getDefault().getCode();
        final CurrencyRequest request = new CurrencyRequest(fromCode, toCode);
        executeRequest(request);
    }

    private void executeRequest(CurrencyRequest request) {
        executorService.submit(request);
    }
}
