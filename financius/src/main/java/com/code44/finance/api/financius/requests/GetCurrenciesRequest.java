package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;

import com.code44.finance.api.User;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;

import java.util.ArrayList;
import java.util.List;

public class GetCurrenciesRequest extends FinanciusBaseRequest<Void> {
    public GetCurrenciesRequest(Context context, User user) {
        super(null, context, user);
    }

    @Override
    protected Void performRequest() throws Exception {
        final List<com.code44.finance.backend.endpoint.currencies.model.Currency> serverCurrencies = getCurrenciesService().list().execute().getItems();
        final List<ContentValues> currenciesValues = new ArrayList<>();
        for (com.code44.finance.backend.endpoint.currencies.model.Currency serverCurrency : serverCurrencies) {
            currenciesValues.add(Currency.from(serverCurrency).asContentValues());
        }

        DataStore.bulkInsert().values(currenciesValues).into(CurrenciesProvider.uriCurrencies());
        return null;
    }
}
