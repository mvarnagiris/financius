package com.code44.finance.backend.endpoint.body;

import com.code44.finance.backend.entity.CurrencyEntity;

import java.util.List;

public class CurrenciesBody extends EntitiesBody<CurrencyEntity> {
    private final List<CurrencyEntity> currencies;

    public CurrenciesBody(List<CurrencyEntity> currencies, String deviceRegId) {
        super(deviceRegId);
        this.currencies = currencies;
    }

    public List<CurrencyEntity> getCurrencies() {
        return currencies;
    }
}
