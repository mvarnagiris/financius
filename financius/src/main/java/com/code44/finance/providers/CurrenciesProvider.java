package com.code44.finance.providers;

import com.code44.finance.db.model.Currency;

public class CurrenciesProvider extends BaseModelProvider<Currency> {
    @Override
    protected Class<Currency> getModelClass() {
        return Currency.class;
    }
}
