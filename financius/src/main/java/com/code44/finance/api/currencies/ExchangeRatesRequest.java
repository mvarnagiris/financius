package com.code44.finance.api.currencies;

import android.content.ContentValues;
import android.content.Context;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesRequest extends Request {
    private final Context context;
    private final CurrenciesRequestService requestService;
    private final List<String> fromCodes;
    private final String toCode;

    public ExchangeRatesRequest(EventBus eventBus, Context context, CurrenciesRequestService requestService, List<String> fromCodes, String toCode) {
        super(eventBus);
        Preconditions.checkNotNull(eventBus, "EventBus cannot be empty.");
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(requestService, "Request service cannot be null.");
        Preconditions.checkNotNull(fromCodes, "From codes cannot be null.");
        Preconditions.checkNotEmpty(toCode, "To code cannot be empty.");

        this.context = context;
        this.requestService = requestService;
        this.fromCodes = fromCodes;
        this.toCode = toCode;
    }

    @Override
    protected void performRequest() throws Exception {
        final List<ContentValues> valuesList = new ArrayList<>();
        for (String fromCode : fromCodes) {
            final Currency currency = getCurrencyWithUpdatedExchangeRate(fromCode);
            if (currency != null) {
                valuesList.add(currency.asContentValues());
            }
        }

        if (!valuesList.isEmpty()) {
            DataStore.bulkInsert().values(valuesList).into(context, CurrenciesProvider.uriCurrencies());
        }
    }

    private Currency getCurrencyWithUpdatedExchangeRate(String fromCode) throws IOException {
        final ExchangeRateRequest request = new ExchangeRateRequest(eventBus, context, requestService, fromCode, toCode, false);
        request.run();
        return request.getCurrency();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRatesRequest)) return false;

        ExchangeRatesRequest that = (ExchangeRatesRequest) o;

        return toCode.equals(that.toCode);
    }

    @Override
    public int hashCode() {
        return toCode.hashCode();
    }
}
