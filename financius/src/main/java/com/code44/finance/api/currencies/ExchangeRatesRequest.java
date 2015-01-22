package com.code44.finance.api.currencies;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRatesRequest extends Request<ExchangeRatesResponse> {
    private final CurrenciesRequestService requestService;
    private final Context context;
    private final boolean updateDatabase;
    private final String[] codes;

    public ExchangeRatesRequest(EventBus eventBus, CurrenciesRequestService requestService, Context context, boolean updateDatabase, String... codes) {
        super(eventBus);
        Preconditions.notNull(eventBus, "EventBus cannot be empty.");
        this.requestService = Preconditions.notNull(requestService, "Request service cannot be null.");
        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.updateDatabase = updateDatabase;
        this.codes = Preconditions.notNull(codes, "Codes cannot be null.");
    }

    @Override protected ExchangeRatesResponse performRequest() throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("select * from yahoo.finance.xchange where pair in (");
        for (String code : codes) {
            sb.append("\"").append(code).append("\",");
        }
        sb.append(")");

        final ExchangeRatesResponse response = requestService.getExchangeRates(sb.toString());

        if (updateDatabase) {
            updateDatabase(response);
        }

        return response;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRatesRequest)) return false;

        final ExchangeRatesRequest that = (ExchangeRatesRequest) o;

        //noinspection RedundantIfStatement
        if (!Arrays.equals(codes, that.codes)) return false;

        return true;
    }

    @Override public int hashCode() {
        return Arrays.hashCode(codes);
    }

    private void updateDatabase(ExchangeRatesResponse response) {
        final Cursor cursor = Tables.Currencies.getQuery().from(context, CurrenciesProvider.uriCurrencies()).execute();
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }

        final List<Model> currencies = new ArrayList<>();
        do {
            final Currency currency = Currency.from(cursor);
            Map<String, Currency.ExchangeRate> exchangeRates = currency.getExchangeRates();
            if (exchangeRates == null) {
                exchangeRates = new HashMap<>();
            }
            exchangeRates.putAll(response.getExchangeRates(currency.getCode()));
            currency.setExchangeRates(exchangeRates);
        } while (cursor.moveToNext());

        DataStore.bulkInsert().models(currencies).into(context, CurrenciesProvider.uriCurrencies());
    }
}
