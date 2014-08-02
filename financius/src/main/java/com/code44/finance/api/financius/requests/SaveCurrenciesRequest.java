package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.currencies.model.CurrenciesBody;
import com.code44.finance.data.db.model.Currency;

import java.util.ArrayList;
import java.util.List;

public class SaveCurrenciesRequest extends FinanciusBaseRequest<Void> {
    private final CurrenciesBody body;

    public SaveCurrenciesRequest(Context context, User user, List<Currency> currencies) {
        super(null, context, user);
        body = new CurrenciesBody();
        body.setCurrencies(prepareBody(currencies));
    }

    @Override
    protected Void performRequest() throws Exception {
        getCurrenciesService().save(body);
        return null;
    }

    private List<com.code44.finance.backend.endpoint.currencies.model.Currency> prepareBody(List<Currency> currencies) {
        final List<com.code44.finance.backend.endpoint.currencies.model.Currency> serverCurrencies = new ArrayList<>();
        for (Currency currency : currencies) {
            serverCurrencies.add(currency.toEntity());
        }
        return serverCurrencies;
    }
}
