package com.code44.finance.api.currencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;
import com.google.gson.JsonObject;

import retrofit.client.Response;

public class CurrencyRequest extends BaseRequest<Currency> {
    private final CurrenciesRequestService requestService;
    private final Context context;
    private final String fromCode;
    private final String toCode;

    public CurrencyRequest(CurrenciesRequestService requestService, Context context, String fromCode, String toCode) {
        super(getUniqueId(fromCode, toCode));
        this.requestService = requestService;
        this.context = context;
        this.fromCode = fromCode;
        this.toCode = toCode;
    }

    public static String getUniqueId(String fromCode, String toCode) {
        return fromCode + "_" + toCode;
    }

    @Override
    protected Currency performRequest() throws Exception {
        return parseResponse(requestService.getExchangeRate(fromCode, toCode));
    }

    private Currency parseResponse(Response rawResponse) throws Exception {
        final JsonObject json = IOUtils.readJsonObject(rawResponse);
        final double exchangeRate = json.get("rate").getAsDouble();

        final Cursor cursor = Query.create()
                .projection(Tables.Currencies.ID.getName())
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.CODE + "=?")
                .args(fromCode)
                .from(context, CurrenciesProvider.uriCurrencies())
                .execute();
        final Currency currency = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);

        currency.setExchangeRate(exchangeRate);
        if (currency.getId() > 0) {
            context.getContentResolver().bulkInsert(CurrenciesProvider.uriCurrencies(), new ContentValues[]{currency.asContentValues()});
        }

        return currency;
    }

    @Override
    public String getUniqueId() {
        return getUniqueId(fromCode, toCode);
    }

    @Override
    protected BaseRequestEvent<Currency, ? extends BaseRequest<Currency>> createEvent(Currency result, Exception error, BaseRequestEvent.State state) {
        return new CurrencyRequestEvent(this, result, error, state);
    }

    public static class CurrencyRequestEvent extends BaseRequestEvent<Currency, CurrencyRequest> {
        protected CurrencyRequestEvent(CurrencyRequest request, Currency result, Exception error, State state) {
            super(request, result, error, state);
        }
    }
}
