package com.code44.finance.api.currencies;

import com.code44.finance.data.model.ExchangeRate;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExchangeRatesResponse {
    @SerializedName("query")
    private Query query;

    public Set<ExchangeRate> getExchangeRates() {
        final Set<ExchangeRate> exchangeRates = new HashSet<>();
        for (Rate rate : query.results.rates) {
            final ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setFromCode(rate.id.substring(0, 3));
            exchangeRate.setToCode(rate.id.substring(3));
            exchangeRate.setRate(rate.rate);
            exchangeRates.add(exchangeRate);
        }

        return exchangeRates;
    }

    private static class Query {
        @SerializedName("results")
        private Results results;
    }

    private static class Results {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") @SerializedName("rate")
        private List<Rate> rates;
    }

    private static class Rate {
        @SerializedName("id")
        private String id;

        @SerializedName("Rate")
        private double rate;
    }
}
