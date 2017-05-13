package com.code44.finance.api.currencies;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.Request;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.ExchangeRate;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.ExchangeRatesProvider;
import com.code44.finance.utils.EventBus;
import com.crashlytics.android.Crashlytics;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class UpdateExchangeRatesRequest extends Request<ExchangeRatesResponse> {
    private final CurrenciesRequestService requestService;
    private final Context context;
    private final String fromCode;

    public UpdateExchangeRatesRequest(EventBus eventBus, CurrenciesRequestService requestService, Context context, String fromCode) {
        super(eventBus);
        this.requestService = checkNotNull(requestService, "Request service cannot be null.");
        this.context = checkNotNull(context, "Context cannot be null.");
        this.fromCode = fromCode;
    }

    @Override protected ExchangeRatesResponse performRequest() throws Exception {
        final List<String> codes = getCodes();

        final StringBuilder sb = new StringBuilder();
        sb.append("select * from yahoo.finance.xchange where pair in (");
        int index = 0;
        for (String code : codes) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append("\"").append(code).append("\"");
            index++;
        }
        sb.append(")");

        final String query = sb.toString();
        Crashlytics.log("http://query.yahooapis.com/v1/public/yql?env=store://datatables.org/alltableswithkeys&format=json&q=" + query);
        final ExchangeRatesResponse response = requestService.getExchangeRates(query);
        updateDatabase(response);

        return response;
    }

    private List<String> getCodes() {
        final List<CurrencyFormat> currencyFormats = getCurrencyFormats();
        final List<String> codes = new ArrayList<>();
        for (int i = 0, size = currencyFormats.size(); i < size; i++) {
            final CurrencyFormat currencyFormat = currencyFormats.get(i);
            for (int e = i + 1; e < size; e++) {
                final CurrencyFormat otherCurrencyFormat = currencyFormats.get(e);
                codes.add(currencyFormat.getCode() + otherCurrencyFormat.getCode());
            }

            if (!Strings.isNullOrEmpty(fromCode)) {
                codes.add(fromCode + currencyFormat.getCode());
            }
        }
        return codes;
    }

    private List<CurrencyFormat> getCurrencyFormats() {
        final Cursor cursor = Tables.CurrencyFormats.getQuery().from(context, CurrenciesProvider.uriCurrencies()).execute();
        if (cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        final List<CurrencyFormat> currencies = new ArrayList<>();
        do {
            currencies.add(CurrencyFormat.from(cursor));
        } while (cursor.moveToNext());
        return currencies;
    }

    private void updateDatabase(ExchangeRatesResponse response) {
        final Set<ExchangeRate> exchangeRates = response.getExchangeRates();
        DataStore.bulkInsert().models(exchangeRates).into(context, ExchangeRatesProvider.uriExchangeRates());
    }
}
