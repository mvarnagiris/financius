package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.currencies.model.CurrencyEntity;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
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
        long currenciesTimestamp = user.getCurrenciesTimestamp();
        final List<CurrencyEntity> serverCurrencies = getCurrenciesService().list(currenciesTimestamp).execute().getItems();
        final List<ContentValues> currenciesValues = new ArrayList<>();
        for (CurrencyEntity serverCurrency : serverCurrencies) {
            final ContentValues values = Currency.from(serverCurrency).asContentValues();
            values.remove(Tables.Currencies.EXCHANGE_RATE.getName());
            currenciesValues.add(values);
            if (currenciesTimestamp < serverCurrency.getEditTs()) {
                currenciesTimestamp = serverCurrency.getEditTs();
            }
        }

        DataStore.bulkInsert().values(currenciesValues).into(CurrenciesProvider.uriCurrencies());
        user.setCurrenciesTimestamp(currenciesTimestamp);
        return null;
    }
}
