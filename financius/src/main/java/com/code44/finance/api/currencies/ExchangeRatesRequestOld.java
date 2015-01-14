package com.code44.finance.api.currencies;

import android.content.ContentValues;
import android.content.Context;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesRequestOld extends Request {
    private final Context context;
    private final CurrenciesRequestService requestService;
    private final List<String> fromCodes;
    private final String toCode;

    public ExchangeRatesRequestOld(EventBus eventBus, Context context, CurrenciesRequestService requestService, List<String> fromCodes, String toCode) {
        super(eventBus);
        Preconditions.notNull(eventBus, "EventBus cannot be empty.");


        Preconditions.notNull(fromCodes, "From codes cannot be null.");
        Preconditions.notEmpty(toCode, "To code cannot be empty.");

        this.context = Preconditions.notNull(context, "Context cannot be null.");
        this.requestService = Preconditions.notNull(requestService, "Request service cannot be null.");
        this.fromCodes = fromCodes;
        this.toCode = toCode;
    }

    @Override protected void performRequest() throws Exception {
        final List<ContentValues> valuesList = new ArrayList<>();
        for (String fromCode : fromCodes) {
            final Currency currency = getCurrencyWithUpdatedExchangeRate(fromCode);
            if (currency != null) {
                valuesList.add(currency.asValues());
            }
        }

        if (!valuesList.isEmpty()) {
            DataStore.bulkInsert().values(valuesList).into(context, CurrenciesProvider.uriCurrencies());
        }
    }

    private Currency getCurrencyWithUpdatedExchangeRate(String fromCode) {
        final ExchangeRateRequest request = new ExchangeRateRequest(eventBus, context, requestService, fromCode, toCode, false);
        request.run();
        return request.getCurrency();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRatesRequestOld)) return false;

        ExchangeRatesRequestOld that = (ExchangeRatesRequestOld) o;

        return toCode.equals(that.toCode);
    }

    @Override public int hashCode() {
        return toCode.hashCode();
    }
}
