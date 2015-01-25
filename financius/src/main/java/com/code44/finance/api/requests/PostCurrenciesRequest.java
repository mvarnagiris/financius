package com.code44.finance.api.requests;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.backend.endpoint.currencies.Currencies;
import com.code44.finance.backend.endpoint.currencies.model.CurrenciesBody;
import com.code44.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.CurrencyFormat;

import java.util.ArrayList;
import java.util.List;

public class PostCurrenciesRequest extends PostRequest<CurrenciesBody> {
    private final Currencies currenciesService;
    private final List<CurrencyFormat> currencies;

    public PostCurrenciesRequest(GcmRegistration gcmRegistration, Currencies currenciesService, List<CurrencyFormat> currencies) {
        super(null, gcmRegistration);
        Preconditions.notNull(currenciesService, "Currencies service cannot be null.");
        Preconditions.notNull(currencies, "Currencies list cannot be null.");

        this.currenciesService = currenciesService;
        this.currencies = currencies;
    }

    @Override protected CurrenciesBody createBody() {
        return new CurrenciesBody();
    }

    @Override protected void onAddPostData(CurrenciesBody body) {
        final List<CurrencyEntity> serverCurrencies = new ArrayList<>();
        for (CurrencyFormat currencyFormat : currencies) {
//            serverCurrencies.add(currency.asEntity());
        }
        body.setCurrencies(serverCurrencies);
    }

    @Override protected boolean isPostDataEmpty(CurrenciesBody body) {
        return body.getCurrencies().isEmpty();
    }

    @Override protected void performRequest(CurrenciesBody body) throws Exception {
        currenciesService.save(body);
    }
}
