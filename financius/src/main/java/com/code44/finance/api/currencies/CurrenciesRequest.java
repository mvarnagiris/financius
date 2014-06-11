package com.code44.finance.api.currencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.db.Tables;
import com.code44.finance.db.model.Currency;
import com.code44.finance.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.Query;
import com.google.gson.JsonObject;

import retrofit.client.Response;

public class CurrenciesRequest extends BaseRequest<Currency, CurrenciesRequestService> {
    private final Context context;
    private final String fromCode;
    private final String toCode;

    public CurrenciesRequest(CurrenciesRequestService requestService, Context context, String fromCode, String toCode) {
        super(requestService);
        this.context = context;
        this.fromCode = fromCode;
        this.toCode = toCode;
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
                .appendProjection(Tables.Currencies.ID.getName())
                .appendProjection(Tables.Currencies.PROJECTION)
                .appendSelection(Tables.Currencies.CODE + "=?")
                .appendArgs(fromCode).build()
                .asCursor(context, CurrenciesProvider.uriCurrencies());
        final Currency currency = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);

        currency.setExchangeRate(exchangeRate);
        context.getContentResolver().bulkInsert(CurrenciesProvider.uriCurrencies(), new ContentValues[]{currency.asContentValues()});

        return currency;
    }

    @Override
    protected String getUniqueId() {
        return fromCode + "_" + toCode;
    }
}
