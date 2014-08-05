package com.code44.finance.backend.endpoint.body;

import com.code44.finance.backend.entity.CurrencyEntity;

import java.util.List;

public class CurrenciesBody {
    private List<CurrencyEntity> currencies;

    public List<CurrencyEntity> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CurrencyEntity> currencies) {
        this.currencies = currencies;
    }
}
