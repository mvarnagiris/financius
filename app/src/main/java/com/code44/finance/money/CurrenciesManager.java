package com.code44.finance.money;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.ApplicationContext;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.ExchangeRate;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.ExchangeRatesProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class CurrenciesManager {
    private final Map<String, ExchangeRate> exchangeRates = new HashMap<>();
    private final Context context;
    private final GeneralPrefs generalPrefs;

    private String mainCurrencyCode;
    private boolean isExchangeRatesFetched = false;

    @Inject public CurrenciesManager(@ApplicationContext Context context, GeneralPrefs generalPrefs) {
        this.context = checkNotNull(context, "Context cannot be null.");
        this.generalPrefs = checkNotNull(generalPrefs, "GeneralPrefs cannot be null.");
        setMainCurrencyCode(generalPrefs.getMainCurrencyCode());
    }

    public void updateExchangeRates(SQLiteDatabase database) {
        final Cursor cursor = Tables.ExchangeRates.getQuery().from(database, Tables.ExchangeRates.TABLE_NAME).execute();
        updateExchangeRates(cursor);
    }

    public String getMainCurrencyCode() {
        if (!Strings.isNullOrEmpty(mainCurrencyCode)) {
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
        context.getContentResolver().notifyChange(CurrenciesProvider.uriCurrencies(), null);
        context.getContentResolver().notifyChange(TransactionsProvider.uriTransactions(), null);
        context.getContentResolver().notifyChange(AccountsProvider.uriAccounts(), null);
    }

    public boolean isMainCurrency(String currencyCode) {
        return currencyCode != null && currencyCode.equals(mainCurrencyCode);
    }

    public double getExchangeRate(String to) {
        return getExchangeRate(mainCurrencyCode, to);
    }

    public double getExchangeRate(String from, String to) {
        fetchExchangeRatesIfNecessary();
        if (Strings.isNullOrEmpty(from) || Strings.isNullOrEmpty(to) || from.equals(to) || from.length() != 3 || to.length() != 3) {
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

    private void fetchExchangeRatesIfNecessary() {
        if (isExchangeRatesFetched) {
            return;
        }

        final Cursor cursor = Tables.ExchangeRates.getQuery().from(context, ExchangeRatesProvider.uriExchangeRates()).execute();
        updateExchangeRates(cursor);
    }

    private void updateExchangeRates(Cursor cursor) {
        exchangeRates.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final ExchangeRate exchangeRate = ExchangeRate.from(cursor);
                exchangeRates.put(getExchangeRateKey(exchangeRate.getFromCode(), exchangeRate.getToCode()), exchangeRate);
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);
        isExchangeRatesFetched = true;
    }
}
