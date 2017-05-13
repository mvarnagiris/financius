package com.code44.finance.api.endpoints.requests.currencies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.GetEntitiesRequest;
import com.code44.finance.backend.financius.model.CurrencyEntity;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;

import java.util.List;

public class GetCurrenciesRequest extends GetEntitiesRequest<CurrencyFormat, CurrencyEntity> {
    public GetCurrenciesRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user) {
        super(eventBus, endpointFactory, context, user);
    }

    @Override protected long getLastUpdateTimestamp(User user) {
        return user.getLastCurrenciesUpdateTimestamp();
    }

    @Override protected List<CurrencyEntity> performRequest(long timestamp) throws Exception {
        return getEndpoint().listCurrencies(timestamp).execute().getItems();
    }

    @Override protected CurrencyFormat getModelFrom(CurrencyEntity entity) {
        return CurrencyFormat.from(entity);
    }

    @Override protected Uri getSaveUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastCurrenciesUpdateTimestamp(lastUpdateTimestamp);
    }
}