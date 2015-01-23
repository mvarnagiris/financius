package com.code44.finance.api.currencies;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.Request;
import com.code44.finance.api.Result;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateExchangeRatesRequest extends Request<ExchangeRatesResponse> {
    private final CurrenciesRequestService requestService;
    private final Context context;

    public UpdateExchangeRatesRequest(EventBus eventBus, CurrenciesRequestService requestService, Context context) {
        super(eventBus);
        this.requestService = Preconditions.notNull(requestService, "Request service cannot be null.");
        this.context = Preconditions.notNull(context, "Context cannot be null.");
    }

    @Override protected ExchangeRatesResponse performRequest() throws Exception {
        final List<Currency> currencies = getCurrencies();
        final List<String> codes = new ArrayList<>();
        for (int i = 0, size = currencies.size(); i < size; i++) {
            final Currency currency = currencies.get(i);
            for (int e = i + 1; e < size; e++) {
                final Currency otherCurrency = currencies.get(e);
                codes.add(currency.getCode() + otherCurrency.getCode());
                codes.add(otherCurrency.getCode() + currency.getCode());
            }
        }

        final GetExchangeRatesRequest request = new GetExchangeRatesRequest(eventBus, requestService, codes.toArray(new String[codes.size()]));
        final Result<ExchangeRatesResponse> result = request.call();
        if (!result.isSuccess()) {
            throw result.getError();
        }

        updateDatabase(currencies, result.getData());

        return result.getData();
    }

    private List<Currency> getCurrencies() {
        final Cursor cursor = Tables.Currencies.getQuery().from(context, CurrenciesProvider.uriCurrencies()).execute();
        if (cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        final List<Currency> currencies = new ArrayList<>();
        do {
            currencies.add(Currency.from(cursor));
        } while (cursor.moveToNext());
        return currencies;
    }

    private void updateDatabase(List<Currency> currencies, ExchangeRatesResponse response) {
        for (Currency currency : currencies) {
            Map<String, Currency.ExchangeRate> exchangeRates = currency.getExchangeRates();
            if (exchangeRates == null) {
                exchangeRates = new HashMap<>();
            }
            exchangeRates.putAll(response.getExchangeRates(currency.getCode()));
            currency.setExchangeRates(exchangeRates);
        }

        DataStore.bulkInsert().models(currencies).into(context, CurrenciesProvider.uriCurrencies());
    }
}
