package com.code44.finance.api.requests;

import android.content.ContentValues;
import android.net.Uri;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.currencies.Currencies;
import com.code44.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;

import java.util.List;

import javax.inject.Inject;

public class GetCurrenciesRequest extends GetRequest<CurrencyEntity> {
    @Inject Currencies currenciesService;

    @Override protected long getLastTimestamp(User user) {
        return user.getCurrenciesTimestamp();
    }

    @Override protected List<CurrencyEntity> performRequest(long timestamp) throws Exception {
        return currenciesService.list(timestamp).execute().getItems();
    }

    @Override protected BaseModel getModelFrom(CurrencyEntity entity) {
        return Currency.from(entity);
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setCurrenciesTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override protected void onValuesCreated(ContentValues values) {
        super.onValuesCreated(values);
        values.remove(Tables.Currencies.EXCHANGE_RATE.getName());
    }
}
