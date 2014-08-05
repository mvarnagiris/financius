package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.currencies.model.CurrenciesBody;
import com.code44.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.code44.finance.data.db.model.Currency;

import java.util.ArrayList;
import java.util.List;

public class SaveCurrenciesRequest extends FinanciusBaseRequest<Void> {
    private final CurrenciesBody body;

    public SaveCurrenciesRequest(Context context, User user, List<Currency> currencies) {
        super(null, context, user);
        body = preparePostBody(currencies);
    }

    @Override
    protected Void performRequest() throws Exception {
        getCurrenciesService().save(body).execute();
        return null;
    }

    private CurrenciesBody preparePostBody(List<Currency> currencies) {
        final List<CurrencyEntity> serverCurrencies = new ArrayList<>();
        for (Currency currency : currencies) {
            serverCurrencies.add(currency.toEntity());
        }

        final CurrenciesBody body = new CurrenciesBody();
        body.setCurrencies(serverCurrencies);
        body.setDeviceRegId(GcmRegistration.get().getRegistrationId());

        return body;
    }
}
