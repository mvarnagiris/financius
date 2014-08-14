package com.code44.finance.api.currencies;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.App;
import com.code44.finance.api.Request;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IOUtils;
import com.google.gson.JsonObject;

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
        Preconditions.checkNotNull(eventBus, "EventBus cannot be empty.");
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(requestService, "Request service cannot be null.");
        Preconditions.checkNotEmpty(fromCode, "From code cannot be empty.");
        Preconditions.checkNotEmpty(toCode, "To code cannot be empty.");

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

        final Cursor cursor = Query.create()
                .projection(Tables.Currencies.ID.getName())
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.CODE + "=?", fromCode)
                .from(App.getContext(), CurrenciesProvider.uriCurrencies())
                .execute();
        if (cursor.moveToFirst()) {
            currency = Currency.from(cursor);
        }
        IOUtils.closeQuietly(cursor);

        if (currency == null || StringUtils.isEmpty(currency.getServerId())) {
            currency = null;
            return;
        }

        currency.setExchangeRate(exchangeRate);
        if (storeData) {
            DataStore.bulkInsert().values(currency.asContentValues()).into(context, CurrenciesProvider.uriCurrencies());
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

    public Currency getCurrency() {
        return currency;
    }
}
