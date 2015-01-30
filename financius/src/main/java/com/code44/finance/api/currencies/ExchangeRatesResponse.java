package com.code44.finance.api.currencies;

import com.code44.finance.data.model.ExchangeRate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.Set;

public class ExchangeRatesResponse {
    @SerializedName("query")
    private JsonObject query;

    public Set<ExchangeRate> getExchangeRates() {
        final Set<ExchangeRate> exchangeRates = new HashSet<>();
        final JsonArray jsonArray = query.getAsJsonObject("results").getAsJsonArray("rate");
        for (int i = 0, size = jsonArray.size(); i < size; i++) {
            final JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            final String id = jsonObject.get("id").getAsString();
            final double rate = jsonObject.get("Rate").getAsDouble();
            final ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setFromCode(id.substring(0, 3));
            exchangeRate.setToCode(id.substring(3));
            exchangeRate.setRate(rate);
            exchangeRates.add(exchangeRate);
        }

        return exchangeRates;
    }
}
