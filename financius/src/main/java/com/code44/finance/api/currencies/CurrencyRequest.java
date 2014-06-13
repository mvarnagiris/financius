package com.code44.finance.api.currencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.data.Query;
import com.google.gson.JsonObject;

import retrofit.client.Response;

public class CurrencyRequest extends BaseRequest<Currency, CurrenciesRequestService> {
    private final Context context;
    private final String fromCode;
    private final String toCode;

    public CurrencyRequest(CurrenciesRequestService requestService, Context context, String fromCode, String toCode) {
        super(requestService);
        this.context = context;
        this.fromCode = fromCode;
        this.toCode = toCode;
    }

    public static String getUniqueId(String fromCode, String toCode) {
        return fromCode + "_" + toCode;
    }

    @Override
    protected Response performRequest(CurrenciesRequestService requestService) throws Exception {
        return requestService.getExchangeRate(fromCode, toCode);
    }

    @Override
    protected Currency parseResponse(Response rawResponse) throws Exception {
        final JsonObject json = IOUtils.readJsonObject(rawResponse);
        final double exchangeRate = json.get("rate").getAsDouble();

        final Cursor cursor = Query.get()
                .projection(Tables.Currencies.ID.getName())
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.CODE + "=?")
                .args(fromCode).build()
                .asCursor(context, CurrenciesProvider.uriCurrencies());
        final Currency currency = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);

        currency.setExchangeRate(exchangeRate);
        context.getContentResolver().bulkInsert(CurrenciesProvider.uriCurrencies(), new ContentValues[]{currency.asContentValues()});

        return currency;
    }

    @Override
    protected String getUniqueId() {
        return getUniqueId(fromCode, toCode);
    }

    @Override
    protected BaseRequestEvent<Currency, ? extends BaseRequest<Currency, CurrenciesRequestService>> createEvent(Response rawResponse, Currency parsedResponse, Exception error, BaseRequestEvent.State state) {
        return new CurrencyRequestEvent(this, rawResponse, parsedResponse, error, state);
    }

    public static class CurrencyRequestEvent extends BaseRequestEvent<Currency, CurrencyRequest> {
        protected CurrencyRequestEvent(CurrencyRequest request, Response rawResponse, Currency parsedResponse, Exception error, State state) {
            super(request, rawResponse, parsedResponse, error, state);
        }
    }
}
