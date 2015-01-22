package com.code44.finance.api.currencies;

import com.code44.finance.data.model.Currency;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRatesResponse {
    @SerializedName("Query")
    private Query query;
    private Map<String, Map<String, Currency.ExchangeRate>> exchangeRates;

    public Map<String, Currency.ExchangeRate> getExchangeRates(String code) {
        updateExchangeRatesIfNecessary();
        final Map<String, Currency.ExchangeRate> rates = exchangeRates.get(code);
        if (rates == null) {
            return Collections.emptyMap();
        }

        return rates;
    }

    private void updateExchangeRatesIfNecessary() {
        if (exchangeRates != null) {
            return;
        }

        exchangeRates = new HashMap<>();
        for (Rate rate : query.results.rates) {
            final String fromCode = rate.id.substring(0, 3);
            final String toCode = rate.id.substring(3);

            Map<String, Currency.ExchangeRate> fromToRates = exchangeRates.get(fromCode);
            if (fromToRates == null) {
                fromToRates = new HashMap<>();
                exchangeRates.put(fromCode, fromToRates);
            }

            fromToRates.put(toCode, new Currency.ExchangeRate(fromCode, toCode, rate.rate));
        }
    }

    private static class Query {
        @SerializedName("results")
        private Results results;
    }

    private static class Results {
        @SerializedName("rate")
        private List<Rate> rates;
    }

    private static class Rate {
        @SerializedName("id")
        private String id;

        @SerializedName("Rate")
        private double rate;
    }
}
