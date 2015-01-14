package com.code44.finance.api.currencies;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRatesResult {
    @SerializedName("Query")
    private Query query;

    public Map<String, Double> getExchangeRates() {
        final Map<String, Double> exchangeRates = new HashMap<>();

        for (Rate rate : query.results.rates) {
            exchangeRates.put(rate.id, rate.rate);
        }

        return exchangeRates;
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
