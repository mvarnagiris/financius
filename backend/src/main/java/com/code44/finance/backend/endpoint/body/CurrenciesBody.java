package com.code44.finance.backend.endpoint.body;

import com.code44.finance.backend.entity.Currency;

import java.util.List;

public class CurrenciesBody {
    private List<Currency> currencies;

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }
}
