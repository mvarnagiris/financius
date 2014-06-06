package com.code44.finance.providers;

import android.content.ContentUris;
import android.net.Uri;

import com.code44.finance.db.model.Currency;

public class CurrenciesProvider extends BaseModelProvider<Currency> {
    public static Uri uriCurrencies() {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(CurrenciesProvider.class) + "/" + Currency.class.getSimpleName());
    }

    public static Uri uriCurrency(long currencyId) {
        return ContentUris.withAppendedId(uriCurrencies(), currencyId);
    }

    @Override
    protected Class<Currency> getModelClass() {
        return Currency.class;
    }
}
