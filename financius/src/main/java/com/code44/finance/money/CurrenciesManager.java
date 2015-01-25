package com.code44.finance.money;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.ExchangeRate;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.squareup.otto.Produce;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CurrenciesManager {
    private final Map<String, ExchangeRate> exchangeRateCache = new HashMap<>();
    private final EventBus eventBus;
    private final GeneralPrefs generalPrefs;
    private String mainCurrencyCode;

    public CurrenciesManager(EventBus eventBus, GeneralPrefs generalPrefs) {
        this.eventBus = Preconditions.notNull(eventBus, "EventBus cannot be null.");
        this.generalPrefs = Preconditions.notNull(generalPrefs, "GeneralPrefs cannot be null.");
        eventBus.register(this);
    }

    @Produce public CurrenciesManager produceCurrenciesManager() {
        return this;
    }

    public void updateExchangeRates(SQLiteDatabase database) {
        exchangeRateCache.clear();
        final Cursor cursor = Tables.ExchangeRates.getQuery().from(database, Tables.ExchangeRates.TABLE_NAME).execute();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final ExchangeRate exchangeRate = ExchangeRate.from(cursor);
                exchangeRateCache.put(getExchangeRateKey(exchangeRate.getFromCode(), exchangeRate.getToCode()), exchangeRate);
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);
        notifyChanged();
    }

    public String getMainCurrencyCode() {
        if (!Strings.isEmpty(mainCurrencyCode)) {
            return mainCurrencyCode;
        }

        try {
            return java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode().toUpperCase();
        } catch (Exception ignored) {
        }

        return "USD";
    }

    public void setMainCurrencyCode(String mainCurrencyCode) {
        this.mainCurrencyCode = mainCurrencyCode;
        generalPrefs.setMainCurrencyCode(mainCurrencyCode);
        notifyChanged();
    }

    public double getExchangeRateFromMain(String to) {
        return getExchangeRate(mainCurrencyCode, to);
    }

    public double getExchangeRate(String from, String to) {
        if (from.equals(to)) {
            return 1;
        }

        final String key = getExchangeRateKey(from, to);
        final ExchangeRate exchangeRate = exchangeRateCache.get(key);
        if (exchangeRate == null) {
            return 1;
        }

        return exchangeRate.getRate(to);
    }

    private void notifyChanged() {
        eventBus.post(this);
    }

    private String getExchangeRateKey(String from, String to) {
        return from.compareTo(to) > 0 ? from + to : to + from;
    }
}
