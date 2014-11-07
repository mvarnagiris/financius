package com.code44.finance.api.currencies;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IOUtils;
import com.google.gson.JsonObject;

import java.util.UUID;

import retrofit.client.Response;

public class ExchangeRateRequest extends Request {
    private final Context context;
    private final CurrenciesRequestService requestService;
    private final String fromCode;
    private final String toCode;
    private final boolean storeData;

    private Currency currency;

    public ExchangeRateRequest(EventBus eventBus, Context context, CurrenciesRequestService requestService, String fromCode, String toCode, boolean storeData) {
        super(eventBus);
        Preconditions.notNull(eventBus, "EventBus cannot be empty.");
        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(requestService, "Request service cannot be null.");
        Preconditions.notEmpty(fromCode, "From code cannot be empty.");
        Preconditions.notEmpty(toCode, "To code cannot be empty.");

        this.context = context;
        this.requestService = requestService;
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.storeData = storeData;
    }

    @Override protected void performRequest() throws Exception {
        final Response rawResponse = requestService.getExchangeRate(fromCode, toCode);
        final JsonObject json = IOUtils.readJsonObject(rawResponse);
        final double exchangeRate = json.get("rate").getAsDouble();

        final Cursor cursor = Tables.Currencies.getQuery()
                .selection(" and " + Tables.Currencies.CODE + "=?", fromCode)
                .from(context, CurrenciesProvider.uriCurrencies())
                .execute();
        if (cursor.moveToFirst()) {
            currency = Currency.from(cursor);
        }
        IOUtils.closeQuietly(cursor);

        boolean currencyExists = true;
        if (currency == null || StringUtils.isEmpty(currency.getId())) {
            currencyExists = false;
            currency = new Currency();
            currency.setId(UUID.randomUUID().toString());
            currency.setCode(fromCode);
        }

        currency.setExchangeRate(exchangeRate);
        if (currencyExists && storeData) {
            DataStore.bulkInsert().values(currency.asValues()).into(context, CurrenciesProvider.uriCurrencies());
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRateRequest)) return false;

        ExchangeRateRequest that = (ExchangeRateRequest) o;

        return fromCode.equals(that.fromCode) && toCode.equals(that.toCode);
    }

    @Override public int hashCode() {
        int result = fromCode.hashCode();
        result = 31 * result + toCode.hashCode();
        return result;
    }

    public String getFromCode() {
        return fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public Currency getCurrency() {
        return currency;
    }
}
