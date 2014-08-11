package com.code44.finance.api.requests;

import com.code44.finance.backend.endpoint.currencies.Currencies;
import com.code44.finance.backend.endpoint.currencies.model.CurrenciesBody;
import com.code44.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.code44.finance.data.db.model.Currency;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PostCurrenciesRequest extends PostRequest<CurrenciesBody> {
    private final List<Currency> currencies;
    @Inject Currencies currenciesService;

    public PostCurrenciesRequest(List<Currency> currencies) {
        this.currencies = currencies;
    }

    @Override protected CurrenciesBody createBody() {
        return new CurrenciesBody();
    }

    @Override protected void onAddPostData(CurrenciesBody body) {
        final List<CurrencyEntity> serverCurrencies = new ArrayList<>();
        for (Currency currency : currencies) {
            serverCurrencies.add(currency.toEntity());
        }
        body.setCurrencies(serverCurrencies);
    }

    @Override protected void performRequest(CurrenciesBody body) throws Exception {
        currenciesService.save(body);
    }
}
