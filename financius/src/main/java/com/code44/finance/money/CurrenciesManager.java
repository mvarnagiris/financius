package com.code44.finance.money;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.ExchangeRate;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.preferences.GeneralPrefs;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CurrenciesManager {
    private final Map<String, ExchangeRate> exchangeRates = new HashMap<>();
    private final GeneralPrefs generalPrefs;

    private String mainCurrencyCode;

    public CurrenciesManager(GeneralPrefs generalPrefs) {
        this.generalPrefs = Preconditions.notNull(generalPrefs, "GeneralPrefs cannot be null.");
    }

    public void updateExchangeRates(SQLiteDatabase database) {
        exchangeRates.clear();
        final Cursor cursor = Tables.ExchangeRates.getQuery().from(database, Tables.ExchangeRates.TABLE_NAME).execute();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final ExchangeRate exchangeRate = ExchangeRate.from(cursor);
                exchangeRates.put(getExchangeRateKey(exchangeRate.getFromCode(), exchangeRate.getToCode()), exchangeRate);
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);
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
    }

    public double getExchangeRate(String to) {
        return getExchangeRate(mainCurrencyCode, to);
    }

    public double getExchangeRate(String from, String to) {
        if (Strings.isEmpty(from) || Strings.isEmpty(to) || from.equals(to) || from.length() != 3 || to.length() != 3) {
            return 1;
        }

        final String key = getExchangeRateKey(from, to);
        final ExchangeRate exchangeRate = exchangeRates.get(key);
        if (exchangeRate == null) {
            return 1;
        }

        return exchangeRate.getRate(to);
    }

    private String getExchangeRateKey(String from, String to) {
        return from.compareTo(to) > 0 ? from + to : to + from;
    }
}
